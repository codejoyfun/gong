package com.runwise.supply.business;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.view.PinnedSectionListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.ConfigResponse;
import com.runwise.supply.business.entity.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/1/20.
 */

public class WebViewFragment extends NetWorkFragment {
    @ViewInject(R.id.pinnedListView)
    private PinnedSectionListView pinnedListView;
    private List<Item> itemArr;
    private SettingAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        itemArr = ((CarSettingFragmentContainer)getActivity()).getItemList();
        itemArr = new ArrayList<Item>();
        adapter = new SettingAdapter();
        adapter.setData(itemArr);
        pinnedListView.setAdapter(adapter);
        request();
    }
    private void request(){
        String carId = ((CarSettingFragmentContainer)getActivity()).getCollectCarId();
        CarTypeFragment.CarRequest request = new CarTypeFragment.CarRequest(carId);
        sendConnection("/car/config_info.json",request,1000,true, ConfigResponse.class);

    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.webview_layout;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        ConfigResponse response = (ConfigResponse)result;
        if (response != null && response.getData() != null && response.getData().getEntity() != null
                && response.getData().getEntity().getDetail() != null && response.getData().getEntity().getDetail().getBaseinfo() != null
                && response.getData().getEntity().getDetail().getBaseinfo().getItems() != null){
            Item item = new Item(1,response.getData().getEntity().getDetail().getBaseinfo().getName(),"");
            itemArr.add(item);
            for (ConfigResponse.DataBean.EntityBean.DetailBean.BaseinfoBean.ItemsBean bean : response.getData().getEntity().getDetail().getBaseinfo().getItems()){
                Item itemContent =  new Item(0,bean.getField_name(),bean.getField_value());
                itemArr.add(itemContent);
            }
        }
        if (response != null && response.getData() != null && response.getData().getEntity() != null
                && response.getData().getEntity().getDetail() != null && response.getData().getEntity().getDetail().getCarleninfo() != null
                && response.getData().getEntity().getDetail().getCarleninfo().getItems() != null){
            Item item = new Item(1,response.getData().getEntity().getDetail().getCarleninfo().getName(),"");
            itemArr.add(item);
            for (ConfigResponse.DataBean.EntityBean.DetailBean.CarleninfoBean.ItemsBeanX bean : response.getData().getEntity().getDetail().getCarleninfo().getItems()){
                Item itemContent =  new Item(0,bean.getField_name(),bean.getField_value());
                itemArr.add(itemContent);
            }
        }
        adapter.setData(itemArr);


    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    private class SettingAdapter extends IBaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext,R.layout.setting_layout_content,null);
                viewHolder.ll = (LinearLayout)convertView.findViewById(R.id.ll);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Boolean isTitle = (getItemViewType(position) == 1);
            if (isTitle){
                viewHolder.ll.setBackgroundColor(ContextCompat.getColor(mContext, R.color.setting_title_bg));
                viewHolder.title.setTextColor(ContextCompat.getColor(mContext,R.color.setting_title));
            }else {
                viewHolder.ll.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.white));
                viewHolder.title.setTextColor(ContextCompat.getColor(mContext,R.color.settting_content));
            }
            Item item = itemArr.get(position);
            viewHolder.title.setText(item.getTitle());
            if (isTitle){
                viewHolder.content.setText("");
            }else{
                viewHolder.content.setText(item.getContent());
            }
            //viewHolder.content.setText("asdfsdfsdfsdfsdfsf");
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return ((Item)getItem(position)).getType();
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            if (viewType == 1){     //type为1时，为标题
                return true;
            }else
                return false;
        }
    }
    class ViewHolder{
        LinearLayout ll;        //ll
        TextView title;         //左边的标题
        TextView content;       //右边的值
    }
}
