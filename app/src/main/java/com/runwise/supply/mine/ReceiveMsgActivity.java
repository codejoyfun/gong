package com.runwise.supply.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.entity.FinishActEvent;
import com.runwise.supply.mine.entity.QuestionResult;
import com.runwise.supply.mine.entity.RequestQuestion;
import com.runwise.supply.mine.entity.UsMessageRequest;
import com.runwise.supply.mine.entity.UsMessageResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 发表留言
 */
public class ReceiveMsgActivity extends NetWorkActivity {
    private final int REQUEST_MAIN = 1;

    @ViewInject(R.id.giveMessage)
    private EditText giveMessage;

    private QuestionResult usResult;

    @ViewInject(R.id.title)
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivemsg);
        this.setTitleText(true,"给我们留言");
        this.setTitleLeftText(true,"反馈建议");
        this.setTitleRightText(true,"提交");
        usResult = (QuestionResult) this.getIntent().getSerializableExtra("bean");
        QuestionResult.PageListBean.QuestionListBean bean =  usResult.getPage_list().get(1).getQuestion_list().get(0);
        title.setText(bean.getTitil());
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                dialog.setModel(CustomDialog.RIGHT);
                dialog.setMessage("您的反馈全部收到，放心，接下来我们会做的更好！");
                dialog.setMessageGravity();
                dialog.setCancelable(false);
                dialog.setRightBtnListener("我知道啦", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                        EventBus.getDefault().post(new FinishActEvent());
                    }
                });
                dialog.show();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }
    @OnClick({R.id.right_layout,R.id.left_layout})
    public void doClickHandler(View view) {
        switch (view.getId()) {
            case R.id.right_layout:
                String message = giveMessage.getText().toString();
                if(TextUtils.isEmpty(message)) {
                    ToastUtil.show(mContext,"请输入留言");
                    return;
                }
                RequestQuestion question = new RequestQuestion();
                question.setUser_input_id(usResult.getUser_input_id());
                List<RequestQuestion.QuestionIdsBean> requestlist = new ArrayList<>();
                for(QuestionResult.PageListBean.QuestionListBean pageBean : usResult.getPage_list().get(0).getQuestion_list()) {
                    RequestQuestion.QuestionIdsBean questionIdsBean = new RequestQuestion.QuestionIdsBean();
                    questionIdsBean.setId(pageBean.getId());
                    questionIdsBean.setValue((int)pageBean.getRaValue() + "");
                    requestlist.add(questionIdsBean);
                }
                QuestionResult.PageListBean.QuestionListBean bean =  usResult.getPage_list().get(1).getQuestion_list().get(0);
                RequestQuestion.QuestionIdsBean questionIdsBean = new RequestQuestion.QuestionIdsBean();
                questionIdsBean.setId(bean.getId());
                questionIdsBean.setValue(message);
                requestlist.add(questionIdsBean);
                question.setQuestion_ids(requestlist);

                sendConnection("/gongfu/survey/question",question,REQUEST_MAIN,true,null);
                break;
            case R.id.left_layout:
                this.finish();
                break;
        }
    }
}
