package com.runwise.supply.repertory;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.runwise.supply.R;


public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener{
    private List<Fragment> fragments; 
    private RadioGroup rgs;
    private FragmentActivity fragmentActivity; 
    private int fragmentContentId; 

    private int currentTab; 

    private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; 

    public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;

        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();

        rgs.setOnCheckedChangeListener(this);


    }
    public void addOther () {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(1));
        ft.commit();
        ft.show(fragments.get(0));
        ft.hide(fragments.get(1));
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        for(int i = 0; i < rgs.getChildCount(); i++){
            if(rgs.getChildAt(i).getId() == checkedId){
                Fragment fragment = fragments.get(i);
                FragmentTransaction ft = obtainFragmentTransaction(i);

                getCurrentFragment().onPause(); 

                if(fragment.isAdded()){
                    fragment.onResume(); 
                }else{
                    ft.add(fragmentContentId, fragment);
                }
                showTab(i);
                ft.commit();

                if(null != onRgsExtraCheckedChangedListener){
                    onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup, checkedId, i);
                }

            }
        }

    }

    /**
     * 切换tab
     * @param idx
     */
    public void showTab(int idx){
        for(int i = 0; i < fragments.size(); i++){
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);

            if(idx == i){
                ft.show(fragment);
            }else{
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 获取一个带动画的FragmentTransaction
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        // 设置切换动画
        if(index > currentTab){
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        }else{
            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment(){
        return fragments.get(currentTab);
    }
    public Fragment getFragmentByTab (int position) {
    	 return fragments.get(position);
    }

    public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
        return onRgsExtraCheckedChangedListener;
    }

    public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
        this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
    }

    /**
     *  切换tab额外功能功能接口
     */
    public static class OnRgsExtraCheckedChangedListener{
        public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index){

        }
    }

}
