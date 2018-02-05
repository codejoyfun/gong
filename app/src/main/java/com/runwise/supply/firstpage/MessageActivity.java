package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.kids.commonframe.base.BaseActivity;
import com.runwise.supply.R;
import com.runwise.supply.message.MessageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.fragment)
    FrameLayout mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, new MessageFragment());
        // addToBackStack添加到回退栈,addToBackStack与ft.add(R.id.fragment, new
        // MyFragment())效果相当
        // ft.addToBackStack("test");
        ft.commit();
    }
}
