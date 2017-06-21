package com.runwise.supply.message;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

public class DividePayStepingActivity extends BaseActivity {
    @ViewInject(R.id.paystep1_text1)
    private TextView paystep1_text1;
    @ViewInject(R.id.paystep1_text2)
    private TextView paystep1_text2;
    @ViewInject(R.id.paystep1_text3)
    private TextView paystep1_text3;

    @ViewInject(R.id.paystep2_text1)
    private EditText paystep2_text1;
    @ViewInject(R.id.paystep2_text2)
    private EditText paystep2_text2;
    @ViewInject(R.id.paystep2_text3)
    private TextView paystep2_text3;
    @ViewInject(R.id.paystep2_text4)
    private TextView paystep2_text4;
    @ViewInject(R.id.paystep2_text5)
    private EditText paystep2_text5;
    @ViewInject(R.id.paystep2_text6)
    private EditText paystep2_text6;
    @ViewInject(R.id.paystep2_text7)
    private EditText paystep2_text7;
    @ViewInject(R.id.paystep2_text8)
    private EditText paystep2_text8;
    @ViewInject(R.id.paystep2_text9)
    private EditText paystep2_text9;
    @ViewInject(R.id.paystep2_text10)
    private TextView paystep2_text10;
    @ViewInject(R.id.paystep2_text11)
    private EditText paystep2_text11;
    @ViewInject(R.id.paystep2_text12)
    private TextView paystep2_text12;
    @ViewInject(R.id.paystep2_text13)
    private EditText paystep2_text13;
    @ViewInject(R.id.paystep2_text14)
    private TextView paystep2_text14;
    @ViewInject(R.id.paystep2_text15)
    private EditText paystep2_text15;
    @ViewInject(R.id.paystep2_text16)
    private EditText paystep2_text16;


    @ViewInject(R.id.paystep3_text1)
    private EditText paystep3_text1;
    @ViewInject(R.id.paystep3_text2)
    private EditText paystep3_text2;
    @ViewInject(R.id.paystep3_text3)
    private TextView paystep3_text3;
    @ViewInject(R.id.paystep3_text4)
    private EditText paystep3_text4;
    @ViewInject(R.id.paystep3_text5)
    private EditText paystep3_text5;
    @ViewInject(R.id.paystep3_text6)
    private TextView paystep3_text6;

    @ViewInject(R.id.paystep4_pic1)
    private SimpleDraweeView paystep4_pic1;
    @ViewInject(R.id.paystep4_pic2)
    private SimpleDraweeView paystep4_pic2;
    @ViewInject(R.id.paystep4_pic3)
    private SimpleDraweeView paystep4_pic3;
    @ViewInject(R.id.paystep4_pic4)
    private SimpleDraweeView paystep4_pic4;
    @ViewInject(R.id.paystep4_pic5)
    private SimpleDraweeView paystep4_pic5;
    @ViewInject(R.id.paystep4_pic6)
    private SimpleDraweeView paystep4_pic6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devide_pay_steping);
        setTitleText(true,"审核中");
        setTitleLeftIcon(true,R.drawable.back_btn);
    }

    @OnClick(R.id.left_layout)
    public void doFinish(View view) {
        this.finish();
    }
}
