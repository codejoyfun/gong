package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class ProductListFragment extends NetWorkFragment {
    public enum DataType{
        ALL("all"),
        LENGCANGHUO("lengcanghuo"),
        FREEZE("freeze"),
        DRY("dry");

        private final String type;

        DataType(String type) {
            this.type =type;
        }

        public String getType() {
            return type;
        }
    }
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    public class ProductAdapter extends IBaseAdapter {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.product_layout_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
            final EditText editText = viewHolder.editText;
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int currentNum = Integer.valueOf(editText.getText().toString());
                    if (currentNum < 0){
                        editText.setText(--currentNum);
                    }

                }
            });
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int currentNum = Integer.valueOf(editText.getText().toString());
                    editText.setText(++currentNum);
                }
            });

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView    sDv;    //头像
            @ViewInject(R.id.content)
            TextView            content;//内容
            @ViewInject(R.id.addBtn)
            ImageButton         addBtn; //添加按钮
            @ViewInject(R.id.input_minus)
            ImageButton         inputMBtn;//减
            @ViewInject(R.id.input_add)
            ImageButton         inputPBtn;//加
            @ViewInject(R.id.editText)
            EditText            editText; //输入框


        }
    }
}
