package com.runwise.supply.orderpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.mine.NotiySettingDateActivity;
import com.runwise.supply.orderpage.entity.LotDetail;
import com.runwise.supply.tools.StatusBarUtil;

/**
 * 批次详情
 */
public class LotListDetailActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private ListView pullListView;
    private NotifyListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;

    @ViewInject(R.id.imageLayout)
    private LinearLayout imageLayout;
    private String lotId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_lotdetail);
        this.setTitleText(true,"批次详情");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        this.setTitleRigthIcon(false,R.drawable.nav_add);

        lotId = this.getIntent().getStringExtra("lotId");

        pullListView.setOnItemClickListener(this);
        adapter = new NotifyListAdapter();
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page, 10);
//        loadingLayout.setOnRetryClickListener(this);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    @OnClick(R.id.right_layout)
    public void doRightClick(View view) {
        Intent intent = new Intent(this ,NotiySettingDateActivity.class);
        startActivity(intent);
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/v2/lot/" + lotId,request,where,showDialog,LotDetail.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                LotDetail mainResult = (LotDetail)result.getResult().getData();
                adapter.setData(mainResult.getLot().getAttList());
                loadingLayout.onSuccess(1,"该商品无批次信息",R.drawable.default_ico_none);
                if(mainResult.getLot().getAttachmentList() != null && !mainResult.getLot().getAttachmentList().isEmpty()) {
                    imageLayout.setVisibility(View.VISIBLE);
                    for (Integer integer : mainResult.getLot().getAttachmentList()) {
                        View view = View.inflate(mContext,R.layout.image_view_layout,null);
                        String url = Constant.BASE_URL + "/web/content/" + integer;
                        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.picView);
                        FrecoFactory.getInstance(mContext).disPlay(simpleDraweeView,url);
                        imageLayout.addView(view);
                    }
                }
                else {
                    imageLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        loadingLayout.onFailure("",R.drawable.nonocitify_icon);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }

    public class NotifyListAdapter extends IBaseAdapter<LotDetail.LotBean.AttListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lotdetail, null);
                holder = new ViewHolder();
                holder.what = (TextView) convertView.findViewById(R.id.what);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            LotDetail.LotBean.AttListBean bean = mList.get(position);
            holder.what.setText(bean.getKey()+":");
            holder.time.setText(bean.getValue());
            return convertView;
        }

        class ViewHolder {
            TextView what;
            TextView time;
        }

    }
}
