package com.runwise.supply;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by Dong on 2017/10/30.
 */

public class ImageActivity extends BaseActivity {
    public static final String INTENT_KEY_IMG_URL = "image_url";
    @ViewInject(R.id.photo_view)
    PhotoDraweeView photoDraweeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        photoDraweeView.setPhotoUri(Uri.parse(getIntent().getStringExtra(INTENT_KEY_IMG_URL)));
        photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
//        photoDraweeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }
}
