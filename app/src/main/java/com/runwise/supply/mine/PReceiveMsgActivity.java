package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.mine.entity.QuestionQuest;
import com.runwise.supply.mine.entity.QuestionResult;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * 发表留言
 */
public class PReceiveMsgActivity extends NetWorkActivity {
    private final int REQUEST_MAIN = 1;


    private NotifyListAdapter adapter;
    @ViewInject(R.id.listView)
    private ListView listView;

    private QuestionResult.PageListBean pageListBean;
    private QuestionResult usResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmesg);
        this.setTitleText(true,"反馈建议");
        this.setTitleLeftIcon(true,R.drawable.nav_closed);
        this.setTitleRigthIcon(true,R.drawable.nav_next);
        adapter = new NotifyListAdapter();
        listView.setAdapter(adapter);
        sendConnection("/gongfu/survey",new QuestionQuest(),REQUEST_MAIN,true,QuestionResult.class);
    }
    @OnClick(R.id.right_layout)
    public void doNext(View view) {
        boolean isUser = false;
        for(QuestionResult.PageListBean.QuestionListBean bean :adapter.getList()) {
            if (bean.getRaValue() == 0) {
                isUser = true;
            }
        }
        if(isUser) {
            dialog.setModel(CustomDialog.RIGHT);
            dialog.setRightBtnListener("知道啦",null);
            dialog.setMessage("请评星之后在进行下一步");
            dialog.show();
        }
        else {
            if(pageListBean != null) {
                Intent intent = new Intent(this, ReceiveMsgActivity.class);
                intent.putExtra("bean", usResult);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                 usResult = (QuestionResult) result.getResult();
                List<QuestionResult.PageListBean> pageListBeanList = usResult.getPage_list();
                if(!pageListBeanList.isEmpty()) {
                    pageListBean = pageListBeanList.get(0);
                    adapter.setData(pageListBean.getQuestion_list());
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }
    @OnClick({R.id.commitMsg,R.id.left_layout})
    public void doClickHandler(View view) {
        switch (view.getId()) {
            case R.id.commitMsg:

                break;
            case R.id.left_layout:
                doBackPress();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        doBackPress();
    }

    private void doBackPress() {
        boolean isUser = false;
        for(QuestionResult.PageListBean.QuestionListBean bean :adapter.getList()) {
            if (bean.getRaValue() != 0) {
                isUser = true;
            }
        }
        if(isUser) {
            dialog.setModel(CustomDialog.BOTH);
            dialog.setLeftBtnListener("退出反馈", new CustomDialog.DialogListener() {
                @Override
                public void doClickButton(Button btn, CustomDialog dialog) {
                    finish();
                }
            });
            dialog.setRightBtnListener("继续反馈",null);
            dialog.setMessage("当前反馈尚未完成");
            dialog.show();
        }
        else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishActEvent(FinishActEvent event) {
        this.finish();
    }

    public class NotifyListAdapter extends IBaseAdapter<QuestionResult.PageListBean.QuestionListBean> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reveied, null);
                holder = new ViewHolder();
                holder.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            final QuestionResult.PageListBean.QuestionListBean bean = mList.get(position);
            holder.title.setText(bean.getTitil());
            holder.ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser) {
                        bean.setRaValue(rating);
                    }
                }
            });
            holder.ratingbar.setRating(bean.getRaValue());
            return convertView;
        }

        class ViewHolder {
            RatingBar ratingbar;
            TextView title;
        }

    }
}
