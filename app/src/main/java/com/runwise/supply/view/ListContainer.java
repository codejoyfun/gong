package com.runwise.supply.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductAdapterV2;
import com.runwise.supply.adapter.TypeAdapter;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductAdapter;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

public class ListContainer extends LinearLayout {

    public TypeAdapter typeAdapter;

    public ListView getRecyclerView2() {
        return recyclerView2;
    }

    private ListView recyclerView2;
//    private LinearLayoutManager linearLayoutManager;
    private List<ProductBasicList.ListBean> foodBeanList;
    private List<String> mCategoryList;
    private boolean move;
    private int index;
    private Context mContext;

    public ProductAdapter getProductAdapterV2() {
        return mProductAdapterV2;
    }

    public void setProductAdapterV2(ProductAdapter productAdapterV2) {
        mProductAdapterV2 = productAdapterV2;
    }

    public ProductAdapter mProductAdapterV2;
    private TextView tvStickyHeaderView;
    private View stickView;

    private float MILLISECONDS_PER_INCH = 0.01f;  //修改可以改变数据,越大速度越慢

    public ListContainer(Context context) {
        super(context);
    }

    public void init(String category, List<ProductBasicList.ListBean> foodBeanList, List<String> categoryList, ProductActivityV2.ProductCountSetter productCountSetter) {
        this.foodBeanList = foodBeanList;
        mCategoryList = categoryList;
        typeAdapter = new TypeAdapter(categoryList, category);
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recycler1);
        recyclerView2 = (ListView) findViewById(R.id.recycler2);
        if (mCategoryList == null||mCategoryList.size() == 0) {
            recyclerView1.setVisibility(View.GONE);
            findViewById(R.id.stick_header).setVisibility(View.GONE);
        }
        if (foodBeanList.size() == 0) {
            recyclerView2.setVisibility(View.GONE);
            return;
        }
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
                if (!scrollFlag) {
                    typeAdapter.fromClick = true;
                    typeAdapter.setChecked(i);
                    String type = view.getTag().toString();
                    boolean findIt = false;
                    for (int ii = 0; ii < finalFoodBeanList.size(); ii++) {
                        ProductBasicList.ListBean typeBean = finalFoodBeanList.get(ii);
                        if (typeBean.getCategoryChild().equals(type)) {
                            findIt = true;
                            index = ii;
                            moveToPosition(index);
                            break;
                        }
                    }
                    if (!findIt) {
                        ToastUtil.show(getContext(), "该分类下没有商品!");
                    }
                }
            }
        });
//        linearLayoutManager = new LinearLayoutManager(mContext);
//        recyclerView2.setLayoutManager(linearLayoutManager);
//        recyclerView2.setLayoutManager(getFastSmoothScrollToPosition());
//        ((DefaultItemAnimator) recyclerView2.getItemAnimator()).setSupportsChangeAnimations(false);
        setUpProductRecyclerView();
        mProductAdapterV2.setProductCountSetter(productCountSetter);
    }


    public ListContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflate(mContext, R.layout.view_listcontainer, this);
    }

    private void moveToPosition(int n) {
        recyclerView2.setSelection(n);
    }

    private boolean scrollFlag = false;// 标记是否滑动
    private int lastVisibleItemPosition;// 标记上次滑动位置
    public void setUpProductRecyclerView() {
        mProductAdapterV2 = new ProductAdapter(getContext(),false);
        mProductAdapterV2.setData(foodBeanList);

        stickView = findViewById(R.id.stick_header);
        recyclerView2.setAdapter(mProductAdapterV2);
        tvStickyHeaderView = (TextView) findViewById(R.id.tv_header);
        tvStickyHeaderView.setText(foodBeanList.get(0).getCategoryChild());
        recyclerView2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                typeAdapter.fromClick = false;
                return false;
            }
        });
        recyclerView2.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case SCROLL_STATE_FLING:
                        scrollFlag = false;
                        break;
                    case SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {



//                if (move) {
//                    move = false;
//                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
//                    int n = index - listView.getFirstVisiblePosition();
//                    if (0 <= n && n < listView.getChildCount()) {
//                        //获取要置顶的项顶部离RecyclerView顶部的距离
//                        int top = listView.getChildAt(n).getTop();
//                        //最后的移动
//                        listView.smoothScrollBy(0, top);
//                    }
//                } else {
                    View stickyInfoView = findChildViewUnder(listView, stickView.getMeasuredWidth() / 2, 5);
                    if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                        tvStickyHeaderView.setText(String.valueOf(stickyInfoView.getContentDescription()));
                        typeAdapter.setType(String.valueOf(stickyInfoView.getContentDescription()));
                    }

                    View transInfoView = findChildViewUnder(listView, stickView.getMeasuredWidth() / 2, stickView.getMeasuredHeight
                            () + 1);
                    if (transInfoView != null && transInfoView.getTag() != null) {
                       View positionView = transInfoView.findViewById(R.id.stick_header);
                        int transViewStatus = (int) positionView.getTag();
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
//                }
            }
        });
    }

    public View findChildViewUnder(AbsListView listView,float x, float y) {
        final int count = listView.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = listView.getChildAt(i);
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);
            //判断该点是否在childView的范围内
            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }


    public TypeAdapter getTypeAdapter() {
        return typeAdapter;
    }

    public void setTypeAdapter(TypeAdapter typeAdapter) {
        this.typeAdapter = typeAdapter;
    }
}
