package com.runwise.supply.firstpage;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ReturnEvent;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnBean;
import com.runwise.supply.firstpage.entity.ReturnRequest;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.entity.CommitOrderRequest;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 2017/7/22.
 */

public class ReturnActivity extends NetWorkActivity implements ReturnFragment.ReturnCallback{
    private static final int RETURN = 0;
    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
    private OrderResponse.ListBean lbean;
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    private View dialogView;
    private PopupWindow mPopWindow;
    //存放退货对应数据记录productId -----> ReturnBean
    private Map<String,ReturnBean> countMap = new HashMap<>();
    private int currentEditCount = 0;            //当前弹窗的写入的可退数量
    private ReturnBean currentRb;               //当前弹窗的rb
    private boolean isCountChange;              //用来避免，输入退货监听的再次执行

    public Map<String, ReturnBean> getCountMap() {
        return countMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.return_layout);
        setTitleText(true,"申请退货");
        setTitleLeftIcon(true,R.drawable.nav_back);
        Bundle bundle = getIntent().getExtras();
        lbean = bundle.getParcelable("order");
        if (lbean != null && lbean.getLines() != null){
            datas.addAll(lbean.getLines());
        }
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        initPopWindow();
    }
    @OnClick({R.id.title_iv_left,R.id.commitBtn})
    public void btnClick(View v){
        switch(v.getId()){
            case R.id.title_iv_left:
                dialog.setMessage("确认取消退货");
                dialog.setMessageGravity();
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                    }
                });
                dialog.show();
                break;
            case R.id.commitBtn:
                dialog.setMessage("确认对所选商品进行退货?");
                dialog.setMessageGravity();
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        sendReturnRequest();
                    }
                });
                dialog.show();
                break;

        }
    }

    private void sendReturnRequest() {
        ReturnRequest rr = new ReturnRequest();
        List<ReturnRequest.ProductsBean> list = new ArrayList<>();
        Iterator iterator = countMap.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            ReturnBean rb = countMap.get(key);
            if (rb.getReturnCount() == 0){
                continue;
            }
            ReturnRequest.ProductsBean  rsb = new ReturnRequest.ProductsBean();
            rsb.setProduct_id(Integer.valueOf(key));
            rsb.setQty(rb.getReturnCount());
            rsb.setReason(rb.getNote());
            list.add(rsb);

        }
        if (list.size() == 0){
            ToastUtil.show(mContext,"请先选择退货数量");
            return;
        }
        rr.setProducts(list);
        StringBuffer sb = new StringBuffer("/gongfu//v2/order/");
        sb.append(lbean.getOrderID()).append("/return/");
        sendConnection(sb.toString(),rr,RETURN,true,BaseEntity.ResultBean.class);
    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.return_pop_layout, null);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        RelativeLayout mainRL = (RelativeLayout) dialogView.findViewById(R.id.mainRL);
        mainRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        final EditText editText = (EditText)dialogView.findViewById(R.id.editText);
        isCountChange = true;
        editText.setText("0");
        editText.setSelection(1);
        isCountChange = false;
        ImageButton input_minus = (ImageButton)dialogView.findViewById(R.id.input_minus);
        ImageButton input_add = (ImageButton)dialogView.findViewById(R.id.input_add);
        final CheckBox cb1 = (CheckBox)dialogView.findViewById(R.id.cb1);
        final CheckBox cb2 = (CheckBox)dialogView.findViewById(R.id.cb2);
        final CheckBox cb3 = (CheckBox)dialogView.findViewById(R.id.cb3);
        final EditText questionEt = (EditText)dialogView.findViewById(R.id.questionEt);
        Button sureBtn = (Button)dialogView.findViewById(R.id.sureBtn);
        input_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRb != null){
                    if (currentEditCount < currentRb.getMaxReturnCount()){
                        currentEditCount++;
                        isCountChange = true;
                        editText.setText(currentEditCount+"");
                        isCountChange = false;
                        editText.setSelection(String.valueOf(currentEditCount).length());
                    }else{
                        ToastUtil.show(mContext,"数量不能超过"+currentRb.getMaxReturnCount());
                    }
                }
            }
        });
        input_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEditCount > 0){
                    currentEditCount--;
                }
                isCountChange = true;
                editText.setText(currentEditCount+"");
                isCountChange = false;
                editText.setSelection(String.valueOf(currentEditCount).length());
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isCountChange){
                    int maxCanReturnCount = currentRb.getMaxReturnCount();
                    int currentCount = TextUtils.isEmpty(s.toString()) ? 0 : Integer.valueOf(s.toString());
                    if (currentCount > maxCanReturnCount){
                        ToastUtil.show(mContext,"退货数量不能超过"+maxCanReturnCount);
                        int beginCount;
                        if (countMap.get(String.valueOf(currentRb.getpId())) != null){
                            beginCount = countMap.get(String.valueOf(currentRb.getpId())).getReturnCount();
                        }else{
                            beginCount = 0;
                        }
                        isCountChange = true;
                        editText.setText(String.valueOf(beginCount));
                        editText.setSelection(String.valueOf(beginCount).length());
                        isCountChange = false;
                        return;
                    }else{
                        isCountChange = true;
                        editText.setText(String.valueOf(currentCount));
                        editText.setSelection(String.valueOf(currentCount).length());
                        isCountChange = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    questionEt.append("商品损坏");
                    cb1.setTextColor(Color.parseColor("#6BB400"));
                }else{
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("商品损坏","");
                    questionEt.setText(newContent);
                    cb1.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    questionEt.append("包装损坏");
                    cb2.setTextColor(Color.parseColor("#6BB400"));
                }else{
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("包装损坏","");
                    questionEt.setText(newContent);
                    cb2.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    questionEt.append("商品与包装不符合");
                    cb3.setTextColor(Color.parseColor("#6BB400"));
                }else{
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("商品与包装不符合","");
                    questionEt.setText(newContent);
                    cb3.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRb != null){
                    //这里有个删除的动作
                    if (Integer.valueOf(editText.getText().toString()) == 0){
                        if (countMap.containsKey(String.valueOf(currentRb.getpId()))){
                            countMap.remove(String.valueOf(currentRb.getpId()));
                        }
                    }else{
                        currentRb.setReturnCount(Integer.valueOf(editText.getText().toString()));
                        currentRb.setNote(questionEt.getText().toString());
                        countMap.put(String.valueOf(currentRb.getpId()),currentRb);
                    }
                    //更新fragment列表内容
                    EventBus.getDefault().post(new ReturnEvent());
                }
                mPopWindow.dismiss();
            }
        });
        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case RETURN:
                ToastUtil.show(mContext,"退货成功");
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void returnBtnClick(ReturnBean rb) {
        currentRb = rb;
        //同时
        if (!mPopWindow.isShowing()){
            View rootview = LayoutInflater.from(this).inflate(R.layout.receive_layout, null);
            mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
            TextView nameTv = (TextView) dialogView.findViewById(R.id.nameTv);
            nameTv.setText(rb.getName());
            TextView tipTv = (TextView)dialogView.findViewById(R.id.tipTv);
            EditText questionEt = (EditText)dialogView.findViewById(R.id.questionEt);
            CheckBox cb1 = (CheckBox)dialogView.findViewById(R.id.cb1);
            CheckBox cb2 = (CheckBox)dialogView.findViewById(R.id.cb2);
            CheckBox cb3 = (CheckBox)dialogView.findViewById(R.id.cb3);
            tipTv.setText("最多可申请"+rb.getMaxReturnCount()+"件");
            EditText et = (EditText)dialogView.findViewById(R.id.editText);
            if (countMap.containsKey(currentRb.getpId()+"")){
                currentEditCount = countMap.get(currentRb.getpId()+"").getReturnCount();
                et.setText(String.valueOf(currentEditCount));
                questionEt.setText(countMap.get(currentRb.getpId()+"").getNote());
            }else{
                currentEditCount = 0;
                et.setText(String.valueOf(currentEditCount));
                questionEt.setText("");
            }
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
        }
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            if (datas != null && datas.size() > 0){
                bundle.putParcelableArrayList("datas",datas);
            }
            ReturnFragment allFragment = new ReturnFragment();
            allFragment.type = DataType.ALL;
            allFragment.setCallback(ReturnActivity.this);
            allFragment.setArguments(bundle);
            ReturnFragment coldFragment = new ReturnFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            coldFragment.setCallback(ReturnActivity.this);
            ReturnFragment freezeFragment = new ReturnFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            freezeFragment.setCallback(ReturnActivity.this);
            ReturnFragment dryFragment = new ReturnFragment();
            dryFragment.type = DataType.DRY;
            dryFragment.setArguments(bundle);
            dryFragment.setCallback(ReturnActivity.this);
            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }
}
