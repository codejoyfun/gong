package com.runwise.supply.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;

import java.io.FileNotFoundException;

import io.vov.vitamio.utils.Log;

/**
 * Created by libin on 2017/1/18.
 */

public class SignActivity extends BaseActivity {
    private PaintView  paintView;
    @ViewInject(R.id.paintContainer)
    private LinearLayout paintContainer;
    private int paintHeight;
    private int paintWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_layout);
        paintContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //只需要获取一次高度，获取后移除监听器
                        paintContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //这里高度应该定义为成员变量，定义为局部为展示代码方便
                        paintHeight = paintContainer.getHeight();
                        intPaintView();
                    }
                }
        );
    }
    private void intPaintView(){
        paintView = new PaintView(mContext, CommonUtils.getScreenWidth(this),paintHeight);
        paintView.requestFocus();
        paintContainer.addView(paintView);
    }
    @OnClick({R.id.rePaint,R.id.sure})
    public  void btnClick(View view){
        if (view.getId() == R.id.rePaint){
            paintView.clear();
        }
        else {
            String signPath = "";
            //保存为图片，跳出
            Bitmap bitmap = paintView.getPaintBitmap();
            //存到文件目录
            try {
                signPath = CommonUtils.saveSignBitmapToFile(mContext,bitmap,null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(getLocalClassName(),"文件存储失败");
            }
            Intent data = new Intent();
            data.putExtra("signPath",signPath);
            setResult(Activity.RESULT_OK,data);
            finish();
        }
    }
}
