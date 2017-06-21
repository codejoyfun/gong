package com.runwise.supply;

import android.os.Bundle;

import com.kids.commonframe.base.BaseActivity;

public class TestMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        setTitleText(true,"测试");
        setTitleLeftIcon(true,R.drawable.back_btn);
    }
}
