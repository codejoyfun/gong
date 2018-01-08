package com.runwise.supply.mine;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runwise.supply.R;
import com.runwise.supply.tools.FingerprintHelper;


/**
 * Created by Dong on 2017/12/26.
 */

public class FingerprintDialog  extends DialogFragment {

    private TextView mCancelButton;
    private String msg;
    private TextView mTvMessage;
    private FingerprintHelper mFingerprintHelper;
    private FingerprintHelper.OnAuthenticateListener authenticateListener;

    public void setFingerprintHelper(FingerprintHelper fingerprintHelper){
        mFingerprintHelper = fingerprintHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fingerprint, container, false);
        mCancelButton = (TextView) v.findViewById(R.id.tv_fingerprint_cancel);
        mTvMessage = (TextView)v.findViewById(R.id.tv_dialog_fingerprint);
        if(!TextUtils.isEmpty(msg))mTvMessage.setText(msg);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintHelper.startListening(authenticateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintHelper.stopListening();
    }

    public void setCallback(FingerprintHelper.OnAuthenticateListener callback){
        this.authenticateListener = callback;
    }

    public void setText(String text){
        msg = text;
    }




}
