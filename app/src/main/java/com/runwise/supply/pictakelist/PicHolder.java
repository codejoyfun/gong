package com.runwise.supply.pictakelist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.runwise.supply.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;


public class PicHolder extends PinnedRecyclerViewHolder {
    private SimpleDraweeView picTakePic;
    private ImageView picButton;
    private ResizeOptions imageSize;

    private Context context;
    private SelectPictureActivity mActivity;
    private int styleType;

    public PicHolder(Context context, View itemView, ResizeOptions imageSize, int styleType) {
        super(itemView);
        this.context = context;
        this.styleType = styleType;
        mActivity = (SelectPictureActivity)context;
        picTakePic = (SimpleDraweeView) itemView.findViewById(R.id.picTakePic);
        picButton = (ImageView) itemView.findViewById(R.id.picButton);
        //设置纵横比
        picTakePic.setAspectRatio(1);
        this.imageSize = imageSize;
    }

    private void setButtonSelect(boolean select, ImageView picButton, SimpleDraweeView img) {
        if (select) {
            picButton.setImageResource(R.drawable.list_checkbox_check);
            img.getHierarchy().setControllerOverlay(context.getResources().getDrawable(R.drawable.item_pv_selected_bg));
        } else {
            picButton.setImageResource(R.drawable.list_checkbox_uncheck);
            img.getHierarchy().setControllerOverlay(null);
        }
    }

    @Override
    public void onBind(final PicTake bean, final int position) {
        picTakePic.setTag(bean.getUrl());
        if(styleType == Constant.STYLE_CAMERA){
            if (position == 0) {
                picTakePic.setImageResource(R.drawable.xiangji);
                picButton.setVisibility(View.GONE);
            } else {
                picButton.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(bean.getUrl());
                FrecoFactory.getInstance(context).disPlay(picTakePic, uri, imageSize);
            }
        }else {
            picButton.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(bean.getUrl());
            FrecoFactory.getInstance(context).disPlay(picTakePic, uri, imageSize);
        }

        setButtonSelect(bean.isSelect(), picButton, picTakePic);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bean.isSelect()) {
                    setButtonSelect(false, picButton, picTakePic);
                    bean.setSelect(false);
                    mActivity.setSelectToLable();
                } else {
                    if (mActivity.getSelectPic() >= Constant.PIC_MAX_COUNT) {
                        ToastUtil.show(mActivity, "最多选择" + Constant.PIC_MAX_COUNT + "张图片");
                        return;
                    }
                    bean.setSelect(true);
                    setButtonSelect(true, picButton, picTakePic);
                    mActivity.setSelectToLable();
                }
            }
        });
        picTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(styleType == Constant.STYLE_CAMERA){
                    if (position == 0) {
                        mActivity.selectPicFromCamera();
                    } else {
                        Intent intent = new Intent(context, LookPictureActivity.class);
                        intent.putExtra("position", position);
                        mActivity.startActivityForResult(intent, mActivity.REQUEST_LOOK_VIEW);
                    }
                }else {
                    Intent intent = new Intent(context, LookPictureActivity.class);
                    intent.putExtra("position", (position - bean.getSection())+1);
                    mActivity.startActivityForResult(intent, mActivity.REQUEST_LOOK_VIEW);
                }
            }
        });
    }
}
