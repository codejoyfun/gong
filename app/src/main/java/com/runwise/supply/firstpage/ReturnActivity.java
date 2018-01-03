package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.ReturnEvent;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.ReturnRequestSuccessActivity;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnBean;
import com.runwise.supply.firstpage.entity.ReturnRequest;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * Created by libin on 2017/7/22.
 */

public class ReturnActivity extends NetWorkActivity implements ReturnFragment.ReturnCallback {
    private static final int RETURN = 0;
    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    private OrderResponse.ListBean lbean;
    private ArrayList<OrderResponse.ListBean.LinesBean> datas = new ArrayList<>();
    private View dialogView;
    private PopupWindow mPopWindow;
    //存放退货对应数据记录productId -----> ReturnBean
    private Map<String, ReturnBean> countMap = new HashMap<>();
    private double currentEditCount = 0;            //当前弹窗的写入的可退数量
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
        setTitleText(true, "申请退货");
        setTitleLeftIcon(true, R.drawable.nav_back);
        Bundle bundle = getIntent().getExtras();
        lbean = bundle.getParcelable("order");
        if (lbean != null && lbean.getLines() != null) {
            datas.addAll(lbean.getLines());
        }
        initPopWindow();
        requestCategory();
    }

    private void requestCategory() {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }


    @OnClick({R.id.title_iv_left, R.id.commitBtn, R.id.iv_open})
    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_open:
                if (mProductTypeWindow.isShowing()) {
                    mProductTypeWindow.dismiss();
                } else {
                    showPopWindow();
                }
                break;
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

    private void showPopWindow() {
        int y = findViewById(R.id.title_bg).getHeight() + smartTabLayout.getHeight();
        mProductTypeWindow.showAtLocation(getRootView(getActivityContext()), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    private void sendReturnRequest() {
        ReturnRequest rr = new ReturnRequest();
        List<ReturnRequest.ProductsBean> list = new ArrayList<>();
        Iterator iterator = countMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            ReturnBean rb = countMap.get(key);
            if (rb.getReturnCount() == 0) {
                continue;
            }
            ReturnRequest.ProductsBean rsb = new ReturnRequest.ProductsBean();
            rsb.setProduct_id(Integer.valueOf(key));
            rsb.setQty(rb.getReturnCount());
            rsb.setReason(rb.getNote());
            list.add(rsb);

        }
        if (list.size() == 0) {
            ToastUtil.show(mContext, "请先选择退货数量");
            return;
        }
        rr.setProducts(list);
        StringBuffer sb = new StringBuffer("/gongfu//v2/order/");
        sb.append(lbean.getOrderID()).append("/return/");
        sendConnection(sb.toString(), rr, RETURN, true, FinishReturnResponse.class);
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
        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);
        isCountChange = true;
        editText.setText("0");
        editText.setSelection(1);
        isCountChange = false;
        ImageButton input_minus = (ImageButton) dialogView.findViewById(R.id.input_minus);
        ImageButton input_add = (ImageButton) dialogView.findViewById(R.id.input_add);
        final CheckBox cb1 = (CheckBox) dialogView.findViewById(R.id.cb1);
        final CheckBox cb2 = (CheckBox) dialogView.findViewById(R.id.cb2);
        final CheckBox cb3 = (CheckBox) dialogView.findViewById(R.id.cb3);
        final EditText questionEt = (EditText) dialogView.findViewById(R.id.questionEt);
        Button sureBtn = (Button) dialogView.findViewById(R.id.sureBtn);
        input_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRb != null) {
                    String strValue = editText.getText().toString();
                    currentEditCount = TextUtils.isEmpty(strValue)?0:Double.valueOf(strValue);
                    if (currentEditCount + 1 <= currentRb.getMaxReturnCount()) {
                        currentEditCount = currentEditCount + 1;
                        isCountChange = true;
                        editText.setText(NumberUtil.subZeroAndDot(String.valueOf(currentEditCount)));
                        isCountChange = false;
                    } else {
                        ToastUtil.show(mContext, "数量不能超过" + NumberUtil.getIOrD(currentRb.getMaxReturnCount()));
                    }
                }
            }
        });
        input_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strValue = editText.getText().toString();
                currentEditCount = TextUtils.isEmpty(strValue)?0:Double.valueOf(strValue);
                currentEditCount = currentEditCount - 1;
                if (currentEditCount < 0) currentEditCount = 0;
                isCountChange = true;
                editText.setText(NumberUtil.subZeroAndDot(String.valueOf(currentEditCount)));
                isCountChange = false;
                //editText.setSelection(NumberUtil.getIOrD(currentEditCount).length());
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            String previousText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().startsWith("."))return;
                double maxCanReturnCount = currentRb.getMaxReturnCount();
                double currentCount = TextUtils.isEmpty(s.toString()) ? 0 : Double.valueOf(s.toString());
                if (currentCount > maxCanReturnCount) {
                    ToastUtil.show(mContext, "退货数量不能超过" + NumberUtil.getIOrD(maxCanReturnCount));
                    double beginCount;
                    if (countMap.get(String.valueOf(currentRb.getpId())) != null) {
                        beginCount = countMap.get(String.valueOf(currentRb.getpId())).getReturnCount();
                    } else {
                        beginCount = 0;
                    }
                    editText.setText(previousText);
//                    editText.setSelection(previousText.length());
                    return;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionEt.append("商品损坏");
                    cb1.setTextColor(Color.parseColor("#6BB400"));
                } else {
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("商品损坏", "");
                    questionEt.setText(newContent);
                    cb1.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionEt.append("包装损坏");
                    cb2.setTextColor(Color.parseColor("#6BB400"));
                } else {
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("包装损坏", "");
                    questionEt.setText(newContent);
                    cb2.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionEt.append("商品与包装不符合");
                    cb3.setTextColor(Color.parseColor("#6BB400"));
                } else {
                    String content = questionEt.getText().toString();
                    String newContent = content.replaceAll("商品与包装不符合", "");
                    questionEt.setText(newContent);
                    cb3.setTextColor(Color.parseColor("#999999"));
                }
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRb != null) {
                    //这里有个删除的动作
                    String strValue = editText.getText().toString();
                    double value = TextUtils.isEmpty(strValue)?0:new BigDecimal(strValue).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    if (value == 0) {
                        if (countMap.containsKey(String.valueOf(currentRb.getpId()))) {
                            countMap.remove(String.valueOf(currentRb.getpId()));
                        }
                    } else {
                        currentRb.setReturnCount(value);
                        currentRb.setNote(questionEt.getText().toString());
                        countMap.put(String.valueOf(currentRb.getpId()), currentRb);
                    }
                    //更新fragment列表内容
                    EventBus.getDefault().post(new ReturnEvent());
                }
                mPopWindow.dismiss();
            }
        });
//        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    CategoryRespone categoryRespone;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case RETURN:
//                ToastUtil.show(mContext, "退货成功");
//                finish();
                FinishReturnResponse finishReturnResponse = (FinishReturnResponse) result.getResult().getData();
                FinishReturnResponse.ReturnOrder returnOrder = finishReturnResponse.getReturnOrder();
                ReturnRequestSuccessActivity.start(this, returnOrder);
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                break;
        }
    }

    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<OrderResponse.ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<OrderResponse.ListBean.LinesBean>());
        }
        for (OrderResponse.ListBean.LinesBean linesBean : datas) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(linesBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<OrderResponse.ListBean.LinesBean> linesBeen = map.get(listBean.getCategory());
                if (linesBeen == null) {
                    linesBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), linesBeen);
                }
                linesBeen.add(linesBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<OrderResponse.ListBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newProductFragment(datas));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        smartTabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来
        viewPager.setOffscreenPageLimit(titles.size());
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                IntEvent intEvent = new IntEvent();
                intEvent.setHeight(viewPager.getHeight());
                EventBus.getDefault().post(intEvent);
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(smartTabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(smartTabLayout.MODE_SCROLLABLE);
        }
        initPopWindow((ArrayList<String>) titles);
    }

    public ReturnFragment newProductFragment(ArrayList<OrderResponse.ListBean.LinesBean> value) {
        ReturnFragment returnFragment = new ReturnFragment();
        Bundle bundle = new Bundle();
        if (datas != null && datas.size() > 0) {
            bundle.putParcelableArrayList("datas", value);
        }
        returnFragment.setArguments(bundle);
        returnFragment.setCallback(ReturnActivity.this);
        return returnFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, DensityUtil.getScreenW(getActivityContext()), DensityUtil.getScreenH(getActivityContext()) - (findViewById(R.id.title_bg).getHeight() + smartTabLayout.getHeight()), true);
        mProductTypeWindow.setContentView(dialog);
//        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewPager.setCurrentItem(position);
                smartTabLayout.getTabAt(position).select();
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void returnBtnClick(ReturnBean rb) {
        currentRb = rb;
        //同时
        if (!mPopWindow.isShowing()) {
            View rootview = LayoutInflater.from(this).inflate(R.layout.receive_layout, null);
            mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
            TextView nameTv = (TextView) dialogView.findViewById(R.id.nameTv);
            nameTv.setText(rb.getName());
            TextView tipTv = (TextView) dialogView.findViewById(R.id.tipTv);
            EditText questionEt = (EditText) dialogView.findViewById(R.id.questionEt);
            CheckBox cb1 = (CheckBox) dialogView.findViewById(R.id.cb1);
            CheckBox cb2 = (CheckBox) dialogView.findViewById(R.id.cb2);
            CheckBox cb3 = (CheckBox) dialogView.findViewById(R.id.cb3);
            tipTv.setText("最多可申请" + NumberUtil.getIOrD(rb.getMaxReturnCount()) + "件");
            EditText et = (EditText) dialogView.findViewById(R.id.editText);
            if (countMap.containsKey(currentRb.getpId() + "")) {
                currentEditCount = countMap.get(currentRb.getpId() + "").getReturnCount();
                et.setText(NumberUtil.getIOrD(currentEditCount));
                questionEt.setText(countMap.get(currentRb.getpId() + "").getNote());
            } else {
                currentEditCount = 0;
                et.setText(String.valueOf((int) currentEditCount));
                questionEt.setText("");
            }
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
        }
    }
}
