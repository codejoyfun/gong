package com.runwise.supply.tools;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.mine.SettingActivity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Dong on 2017/12/26.
 */

public class FingerprintHelper {

    private static final String DEFAULT_KEY_NAME = "runwise";
    private final FingerprintManagerCompat mFingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private CancellationSignal mCancellationSignal;
    private FingerprintManagerCompat.CryptoObject mCryptoObject;

    public FingerprintHelper(Context context, FingerprintManagerCompat fingerprintManagerCompat){
        mFingerprintManager = fingerprintManagerCompat;
        keyguardManager =(KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    /**
     * 是否支持指纹识别
     */
    public boolean isSupported(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(keyguardManager==null)return false;
            //有硬件支持，有指纹
            if(mFingerprintManager.isHardwareDetected() &&
                    keyguardManager.isKeyguardSecure() &&
                    mFingerprintManager.hasEnrolledFingerprints()){
                return true;
            }
        }
        return false;
    }

    /**
     * 设备支持指纹识别的基础上，初始化必要对象
     */
    public void init(){

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        Cipher defaultCipher;
        Cipher cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        SecretKey key = null;
        try{
            mKeyStore.load(null);
            key = (SecretKey) mKeyStore.getKey(DEFAULT_KEY_NAME, null);
            Log.d("haha","key is null:"+(key==null));
        }catch(KeyStoreException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (UnrecoverableKeyException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch (CertificateException e){
            e.printStackTrace();
        }

        createKey(DEFAULT_KEY_NAME, true);
//        createKey(KEY_NAME_NOT_INVALIDATED, false);

        if (initCipher(defaultCipher, DEFAULT_KEY_NAME)) {
            mCryptoObject = new FingerprintManagerCompat.CryptoObject(defaultCipher);
            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     *
     */
    @TargetApi(23)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     *
     * @param keyName the key name to init the cipher
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    @TargetApi(23)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }
        catch (KeyPermanentlyInvalidatedException e) {
            e.printStackTrace();
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @TargetApi(23)
    public void startListening(OnAuthenticateListener listener) {
        authenticateListener = listener;
        mCancellationSignal = new CancellationSignal();
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager.authenticate(mCryptoObject,  0 /* flags */, mCancellationSignal, callback, null);
        try{
            Log.d("haha", Base64.encodeToString(mCryptoObject.getCipher().doFinal("test test".getBytes()),Base64.DEFAULT));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 回调
     */
    private FingerprintManagerCompat.AuthenticationCallback callback = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            //取消的时候调用
            if(authenticateListener!=null)authenticateListener.onAuthenticate(STATUS_CANCEL,mCryptoObject);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            if(authenticateListener!=null)authenticateListener.onAuthenticate(STATUS_SUCCEED,mCryptoObject);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            //验证失败的时候
            if(authenticateListener!=null)authenticateListener.onAuthenticate(STATUS_FAILED,mCryptoObject);
        }
    };

    private OnAuthenticateListener authenticateListener;
    public static final int STATUS_SUCCEED = 0;
    public static final int STATUS_FAILED = 1;
    public static final int STATUS_CANCEL = 2;
    public interface OnAuthenticateListener{
        void onAuthenticate(int status, FingerprintManagerCompat.CryptoObject cryptoObject);
    }
    public void setAuthenticateListener(OnAuthenticateListener listener){
        authenticateListener = listener;
    }

    @TargetApi(23)
    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    /**
     * 对于整个app，是否开启指纹识别
     * @param context
     * @return
     */
    public static boolean isFingerprintEnabled(Context context){
        return (boolean)SPUtils.get(context,SP_CONSTANTS.SP_FG_ENABLED,false);
    }

    /**
     * 对于这个用户，是否开启指纹识别，用于开关按钮
     * 考虑一种情况：
     * APP是打开指纹识别的，但是用户登录了另外一个账号，这时这个账号的指纹识别按钮应该是关闭的
     * 当这时重新打开按钮，重新验证指纹，那么会更新指纹对应的账号
     * @param context
     * @return
     */
    public static boolean isUserFingerprintEnabled(Context context,String login){
        if(isFingerprintEnabled(context)){
            String fgUser = (String)SPUtils.get(context,SP_CONSTANTS.SP_FG_USER,"");
            if(!TextUtils.isEmpty(fgUser) && fgUser.equals(login)){
                return true;
            }
        }
        return false;
    }

    /**
     * 打开/关闭指纹识别
     * @param context
     * @param enabled
     */
    public static void setFingerprintEnabled(Context context,boolean enabled,String username,String company){

        SPUtils.put(context, SP_CONSTANTS.SP_FG_ENABLED, enabled);
        if(enabled){
            SPUtils.put(context, SP_CONSTANTS.SP_FG_COMPANY,company);
            SPUtils.put(context, SP_CONSTANTS.SP_FG_USER,username);
            String password = (String) SPUtils.get(context,SP_CONSTANTS.SP_CUR_PW,"");
            SPUtils.put(context,SP_CONSTANTS.SP_PW,password);
        }else{
            SPUtils.remove(context, SP_CONSTANTS.SP_FG_COMPANY);
            SPUtils.remove(context, SP_CONSTANTS.SP_FG_USER);
            SPUtils.remove(context,SP_CONSTANTS.SP_PW);
            ToastUtil.show(context,"您已成功关闭指纹登录");
        }
    }

    public static boolean needPrompt(Context context,String userId){
        return (Boolean)SPUtils.get(context,"needFgPrompt"+userId,true);
    }

    public static void setPrompted(Context context,String userId){
        SPUtils.put(context,"needFgPrompt"+userId,false);
    }
}
