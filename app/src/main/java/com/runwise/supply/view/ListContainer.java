package com.runwise.supply.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kids.commonframe.base.util.CommonUtils;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductAdapterV2;
import com.runwise.supply.adapter.TypeAdapter;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

public class ListContainer extends LinearLayout {

    public TypeAdapter typeAdapter;
    private RecyclerView recyclerView2;
    private LinearLayoutManager linearLayoutManager;
    private List<ProductBasicList.ListBean> foodBeanList;
    private List<String> mCategoryList;
    private boolean move;
    private int index;
    private Context mContext;
    public ProductAdapterV2 foodAdapter;
    private TextView tvStickyHeaderView;
    private View stickView;

    public ListContainer(Context context) {
        super(context);
    }

    private void init(List<ProductBasicList.ListBean> foodBeanList,List<String> categoryList) {
        this.foodBeanList = foodBeanList;
        mCategoryList = categoryList;
        typeAdapter = new TypeAdapter(categoryList);
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recycler1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(mContext));
        View view = new View(mContext);
        view.setMinimumHeight(CommonUtils.dip2px(mContext, 50));
        typeAdapter.addFooterView(view);
        typeAdapter.bindToRecyclerView(recyclerView1);
        recyclerView1.addItemDecoration(new SimpleDividerDecoration(mContext));
        ((DefaultItemAnimator) recyclerView1.getItemAnimator()).setSupportsChangeAnimations(false);
        List<ProductBasicList.ListBean> finalFoodBeanList = foodBeanList;
        recyclerView1.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if (recyclerView2.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    typeAdapter.fromClick = true;
                    typeAdapter.setChecked(i);
                    String type = view.getTag().toString();
                    for (int ii = 0; ii < finalFoodBeanList.size(); ii++) {
                        ProductBasicList.ListBean typeBean = finalFoodBeanList.get(ii);
                        if (typeBean.getCategoryChild().equals(type)) {
                            index = ii;
                            moveToPosition(index);
                            break;
                        }
                    }
                }
            }
        });
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler2);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView2.setLayoutManager(linearLayoutManager);
        ((DefaultItemAnimator) recyclerView2.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public ListContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflate(mContext, R.layout.view_listcontainer, this);
    }

    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView2.scrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = recyclerView2.getChildAt(n - firstItem).getTop();
            recyclerView2.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView2.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }
    }


    public void setAddClick() {
        foodAdapter = new ProductAdapterV2(foodBeanList);
        recyclerView2.setAdapter(foodAdapter);
        stickView = findViewById(R.id.stick_header);
        tvStickyHeaderView = (TextView) findViewById(R.id.tv_header);
        tvStickyHeaderView.setText("类别0");
        recyclerView2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                typeAdapter.fromClick = false;
                return false;
            }
        });
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (move) {
                    move = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = index - linearLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < recyclerView.getChildCount()) {
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        int top = recyclerView.getChildAt(n).getTop();
                        //最后的移动
                        recyclerView.smoothScrollBy(0, top);
                    }
                } else {
                    View stickyInfoView = recyclerView.findChildViewUnder(stickView.getMeasuredWidth() / 2, 5);
                    if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                        tvStickyHeaderView.setText(String.valueOf(stickyInfoView.getContentDescription()));
                        typeAdapter.setType(String.valueOf(stickyInfoView.getContentDescription()));
                    }

                    View transInfoView = recyclerView.findChildViewUnder(stickView.getMeasuredWidth() / 2, stickView.getMeasuredHeight
                            () + 1);
                    if (transInfoView != null && transInfoView.getTag() != null) {
                        int transViewStatus = (int) transInfoView.getTag();
                        int dealtY = transInfoView.getTop() - stickView.getMeasuredHeight();
                        if (transViewStatus == ProductAdapterV2.HAS_STICKY_VIEW) {
                            if (transInfoView.getTop() > 0) {
                                stickView.setTranslationY(dealtY);
                            } else {
                                stickView.setTranslationY(0);
                            }
                        } else if (transViewStatus == ProductAdapterV2.NONE_STICKY_VIEW) {
                            stickView.setTranslationY(0);
                        }
                    }
                }
            }
        });
    }
}
