package com.kids.commonframe.base.util.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.internal.http.multipart.Part;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.kids.commonframe.R;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.LoginData;
import com.kids.commonframe.base.ReLoginData;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.MobileUtil;
import com.kids.commonframe.base.util.ObjectTransformUtil;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.SelfOrderTimeStatisticsUtil;
import com.kids.commonframe.base.util.UmengUtil;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_DB_NAME;
import static com.kids.commonframe.base.util.SPUtils.FILE_KEY_HOST;

/**
 * 网络请求帮助类
 *
 * @param <T>
 */
public class NetWorkHelper<T extends BaseEntity> {
    /**
     * The default socket timeout in milliseconds
     */
    public static final int DEFAULT_TIMEOUT_MS = 3*60*1000;

    /**
     * The default number of retries
     * 不要retry，防止重复下单等操作
     */
    public static final int DEFAULT_MAX_RETRIES = 0;

    /**
     * The default backoff multiplier
     */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    private static RequestQueue requestQuerue;
    private Map<Integer, Request<T>> requestStack;
    private Map<String, String> bodyParams;
    private String bodyParamStr;

    private BaseActivity baseActivity;
    private Context context;
    private NetWorkCallBack<T> newWorkCallBack;

    private PaseInterface<T> paseInterface;

    public NetWorkHelper(Context context) {
        this.context = context;
    }

    public NetWorkHelper(Context context, NetWorkCallBack<T> newWorkCallBack) {
        initHelper(context, null, newWorkCallBack);
    }

    public NetWorkHelper(BaseActivity baseActivity, NetWorkCallBack<T> newWorkCallBack) {
        initHelper(baseActivity, baseActivity, newWorkCallBack);
    }

    public NetWorkHelper(Context context, BaseActivity baseActivity, NetWorkCallBack<T> newWorkCallBack) {
        initHelper(context, baseActivity, newWorkCallBack);
    }

    //请求附带时间戳
    public static long REQUEST_TIMESTAMP = 0;
    public static long RESPONSE_TIME = 0;
    public static long RESPONSE_CURRENT_TIME;

    public static void setRequestTimestamp() {
        if (RESPONSE_TIME != 0) {
            REQUEST_TIMESTAMP = RESPONSE_TIME + SystemClock.elapsedRealtime() - RESPONSE_CURRENT_TIME;
        } else {
            REQUEST_TIMESTAMP = System.currentTimeMillis();
        }

    }

    public static void setResponseTime(long responseTime) {
        if (RESPONSE_TIME == 0) {
            RESPONSE_TIME = responseTime;
            RESPONSE_CURRENT_TIME = SystemClock.elapsedRealtime();
        }
    }

    /**
     * 设置回调函数
     */
    public void setOnNetWorkCallBack(NetWorkCallBack<T> newWorkCallBack) {
        this.newWorkCallBack = newWorkCallBack;
    }

    /**
     * 设置解析器
     */
    public void setOnParseImpl(PaseInterface<T> paseInterface) {
        this.paseInterface = paseInterface;
    }

    /**
     * 撤销所有请求
     */
    public void onStopAllRequest() {
        for (Entry<Integer, Request<T>> requestBean : requestStack.entrySet()) {
            Request<T> noRequest = requestStack.get(requestBean.getKey());
            noRequest.cancel();
        }
    }

    /**
     * 返回当前解析器类型
     *
     * @return 是否是JSON解析
     */
    public boolean isJsonPraseType() {
        if (paseInterface != null) {
            if (paseInterface instanceof IXmlParseImp) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 当为post请求是 调用该方法设置body体 参数,此方法必须在sendConnection 方法前调用
     *
     * @param bodyKey
     * @param bodyValue
     */
    public void setBodyParams(String[] bodyKey, String[] bodyValue) {
        if (bodyKey.length != bodyValue.length) {
            throw new IllegalArgumentException("check your BodyParams key or value length!");
        }
        bodyParams = new HashMap<String, String>();
        String bodyParam = "body参数：";
        for (int i = 0; i < bodyKey.length; i++) {
            bodyParam += bodyKey[i] + "=" + bodyValue[i] + "&";
            bodyParams.put(bodyKey[i], bodyValue[i]);
        }
    }

    /**
     * 设置post请求的请求体 不是 key vaule形式
     *
     * @param bodyParam
     */
    public void setBodyParams(String bodyParam) {
        this.bodyParams = null;
        this.bodyParamStr = bodyParam;
    }

    /**
     * 指定timeStamp
     * 现在先用于首页重新下单，需要指定相同的timestamp
     *
     * @param method
     * @param url
     * @param argsKeys
     * @param argsValues
     * @param where
     * @param showDialog
     * @param targerClass
     * @param partList
     * @return
     */
    public long sendConnection(int method, String url, String[] argsKeys,
                               String[] argsValues, int where, boolean showDialog, Class<?> targerClass, List<Part> partList, long timeStamp) {
        if (argsKeys.length != argsValues.length) {
            throw new IllegalArgumentException("check your Params key or value length!");
        }
        StringBuffer queryParam = new StringBuffer();
        for (int i = 0; i < argsKeys.length; i++) {
            queryParam.append(argsKeys[i] + "=" + argsValues[i] + "&");
        }
        if (queryParam.length() > 0 && "&".equals(queryParam.charAt(queryParam.length() - 1))) {
            queryParam.deleteCharAt(queryParam.length() - 1);
            url += "?" + queryParam.toString();
        }
        if (!url.contains("order/undone_orders/") && !url.contains("gongfu/v2/return_order/")) {
            LogUtils.e(url);
        }
        RequestSuccessListener<T> succeessLietener = new RequestSuccessListener<T>(where, targerClass, url, bodyParamStr);
        RequestErrorListener errorLietener = new RequestErrorListener(where, url, bodyParamStr);
        HttpCallBack<T> httpCallback = new HttpCallBack<T>
                (url, succeessLietener, errorLietener, where, method, bodyParams, bodyParamStr, partList, targerClass, timeStamp);

        Request<T> request = requestQuerue.add(httpCallback);
        requestStack.put(where, request);
        if (showDialog && baseActivity != null) {
            baseActivity.showIProgressDialog();
        }
        return timeStamp;
    }

    /**
     * 发送http请求
     *
     * @param url
     * @param argsKeys   url参数key
     * @param argsValues url参数值
     * @param where
     * @param showDialog 是否显示进度条
     */
    public long sendConnection(int method, String url, String[] argsKeys,
                               String[] argsValues, int where, boolean showDialog, Class<?> targerClass, List<Part> partList, boolean useUnLoginDB) {
        setRequestTimestamp();
        long timeStamp = REQUEST_TIMESTAMP;
        if (argsKeys.length != argsValues.length) {
            throw new IllegalArgumentException("check your Params key or value length!");
        }
        StringBuffer queryParam = new StringBuffer();
        for (int i = 0; i < argsKeys.length; i++) {
            queryParam.append(argsKeys[i] + "=" + argsValues[i] + "&");
        }
        if (queryParam.length() > 0 && "&".equals(queryParam.charAt(queryParam.length() - 1))) {
            queryParam.deleteCharAt(queryParam.length() - 1);
            url += "?" + queryParam.toString();
        }
        if (!url.contains("order/undone_orders/") && !url.contains("gongfu/v2/return_order/")) {
            LogUtils.e(url);
        }
        RequestSuccessListener<T> succeessLietener = new RequestSuccessListener<T>(where, targerClass, url, bodyParamStr);
        RequestErrorListener errorLietener = new RequestErrorListener(where, url, bodyParamStr);
        HttpCallBack<T> httpCallback = new HttpCallBack<T>
                (url, succeessLietener, errorLietener, where, method, bodyParams, bodyParamStr, partList, targerClass, timeStamp);
        httpCallback.setUseUnLoginDB(useUnLoginDB);

        Request<T> request = requestQuerue.add(httpCallback);
        requestStack.put(where, request);
        if (showDialog && baseActivity != null) {
            baseActivity.showIProgressDialog();
        }
        return timeStamp;
    }

//	/**
//	 * 封装后台 xml 请求，尽可能的简化了请求
//	 * @param bizCode  接口业务代码
//	 * @param bodyKeys  附加参数
//	 * @param bodyValues
//	 * @param where
//	 * @param showDialog
//	 * @param targerClass
//	 */
//	public  void sendConnection(String bizCode,String[] bodyKeys,String[] bodyValues, int where, boolean showDialog, Class<?> targerClass) {
//		this.setXmlParseType();
//		StringBuffer requestParams = new StringBuffer();
//		for ( int i = 0; i < bodyKeys.length; i ++ ) {
//			String requestKey = bodyKeys[i];
//			String requestValue = bodyValues[i];
//			if(TextUtils.isEmpty(requestKey)){
//				requestParams.append(requestValue);
//			}
//			else{
//				requestParams.append("<"+requestKey+">" + requestValue + "</"+requestKey+">");
//			}
//		}
//		String request = "<Root><BizCode>"+bizCode+"</BizCode><ClientType>Android_Phone_Teacher</ClientType>"
//				+ "<ClientOS>"+ CommonUtils.getVersionName(context)+"</ClientOS><ClientIP>10.38.1.110</ClientIP>" + "<Award></Award>"
//				+ "<SessionId></SessionId>" + "<SvcContent>" + "<![CDATA[" + "<Request>"
//				+ requestParams.toString()+ "</Request>  ]]></SvcContent></Root>";
//		LogUtils.e(request);
//		setBodyParams(new String[]{"xmlmsg","type","ps"}, new String[]{request,"xml","0"});
//		sendConnection(Method.POST, GlobalConstant.BASE_URL, new String[]{}, new String[]{}, where, showDialog, targerClass,null);
//	}

    /**
     * json请求封装
     *
     * @param bizName     接口URL
     * @param params      参数对象
     * @param where       标示
     * @param showDialog  对话框
     * @param targerClass 结果class类
     */
    public long sendConnection(String bizName, Object params, int where, boolean showDialog, Class<?> targerClass) {
        this.setJsonParseType();
        if (params != null) {
            bodyParamStr = JSON.toJSONString(params);
            if (!bizName.contains("order/undone_orders/") && !bizName.contains("gongfu/v2/return_order/")) {
                LogUtils.e(bodyParamStr);
            }
        } else {
            bodyParamStr = "{}";
        }
        String url = (String) SPUtils.get(context, FILE_KEY_HOST, "");
        if (!TextUtils.isEmpty(url)) {
            Constant.BASE_URL = url;
        }
        return sendConnection(Method.POST, getHost(bizName) + bizName, new String[]{}, new String[]{}, where, showDialog, targerClass, null, false);
    }

    /**
     * json请求封装
     *
     * @param bizName     接口URL
     * @param params      参数对象
     * @param where       标示
     * @param showDialog  对话框
     * @param targerClass 结果class类
     */
    public void sendConnection(String host, String bizName, Object params, int where, boolean showDialog, Class<?> targerClass, boolean useUnLoginDB) {
        this.setJsonParseType();
        if (params != null) {
            bodyParamStr = JSON.toJSONString(params);
            if (!bizName.contains("order/undone_orders/") && !bizName.contains("gongfu/v2/return_order/")) {
                LogUtils.e(bodyParamStr);
            }
        } else {
            bodyParamStr = "{}";
        }
        String url = (String) SPUtils.get(context, FILE_KEY_HOST, "");
        if (!TextUtils.isEmpty(url)) {
            Constant.BASE_URL = url;
        }
        sendConnection(Method.POST, host + bizName, new String[]{}, new String[]{}, where, showDialog, targerClass, null, useUnLoginDB);
    }


    public void sendConnection(String bizName, int where, boolean showDialog, Class<?> targerClass) {
        this.setJsonParseType();
        String url = (String) SPUtils.get(context, FILE_KEY_HOST, "");
        if (!TextUtils.isEmpty(url)) {
            Constant.BASE_URL = url;
        }
        sendConnection(Method.POST, getHost(bizName) + bizName, new String[]{}, new String[]{}, where, showDialog, targerClass, null, false);
    }

    public void sendConnection(String bizName, Map<String, String> paramMap, int where, boolean showDialog, Class<?> targerClass) {
        this.setJsonParseType();
        bodyParams = paramMap;
        String url = (String) SPUtils.get(context, FILE_KEY_HOST, "");
        if (!TextUtils.isEmpty(url)) {
            Constant.BASE_URL = url;
        }
        sendConnection(Method.POST, getHost(bizName) + bizName, new String[]{}, new String[]{}, where, showDialog, targerClass, null, false);
    }

    /**
     * json请求,针对包含文件类型
     *
     * @param bizName
     * @param partList
     * @param where
     * @param showDialog
     * @param targerClass
     */
    public void sendConnection(String bizName, List<Part> partList, int where, boolean showDialog, Class<?> targerClass) {
        this.setJsonParseType();
        String url = (String) SPUtils.get(context, FILE_KEY_HOST, "");
        if (!TextUtils.isEmpty(url)) {
            Constant.BASE_URL = url;
        }
        sendConnection(Method.POST, getHost(bizName) + bizName, new String[]{}, new String[]{}, where, showDialog, targerClass, partList, false);
    }


    @SuppressLint("UseSparseArrays")
    private void initHelper(Context context, BaseActivity baseActivity, NetWorkCallBack<T> newWorkCallBack) {
        this.baseActivity = baseActivity;
        this.context = context;
        this.newWorkCallBack = newWorkCallBack;

        requestQuerue = getInstance(context);
        requestStack = new HashMap<Integer, Request<T>>();
        setDefaultPaseImp();
    }

    private static RequestQueue getInstance(Context context) {
        if (requestQuerue == null) {
            synchronized (RequestQueue.class) {
                if (requestQuerue == null) {
                    requestQuerue = Volley.newRequestQueue(context);
                }
            }
        }
        return requestQuerue;
    }

    private void setDefaultPaseImp() {
        paseInterface = new IXmlParseImp();
    }

    /**
     * 在发送请求之前调用改方法设置解析类型
     * 设置解析类型为JSON
     */
    public void setJsonParseType() {
        if (!isJsonPraseType()) {
            paseInterface = new IJsonParseImp();
        }
    }

    /**
     * 在发送请求之前调用改方法设置解析类型
     * 设置解析类型为XML
     */
    public void setXmlParseType() {
        if (isJsonPraseType()) {
            paseInterface = new IXmlParseImp();
        }
    }

    //    public static final String DEFAULT_DATABASE_NAME = "LBZTest0922";
//    public static final String DEFAULT_DATABASE_NAME = "LBZTest0914";
//    public static final String DEFAULT_DATABASE_NAME = "LBZTest0927";
//    public static final String DEFAULT_DATABASE_NAME = "lbz80";
//    public static final String DEFAULT_DATABASE_NAME = "LBZTest1023";
//    public static final String DEFAULT_DATABASE_NAME = "LBZ-Test";
//    public static final String DEFAULT_DATABASE_NAME = "MF-PreGolive-001";
//    public static final String DEFAULT_DATABASE_NAME = "";
//    public static final String DEFAULT_DATABASE_NAME = "LBZ-Golive-01Test";
//    public static final String DEFAULT_DATABASE_NAME = "ZY-PreGolive-001";
//    public static final String DEFAULT_DATABASE_NAME = "MFtest1025";
//    public static final String DEFAULT_DATABASE_NAME = "MF-Test";
    public static final String DEFAULT_DATABASE_NAME = "MFTest1117";

    //    public static  String DEFAULT_DATABASE_NAME = "LBZ-Golive-01";
    // -------------------------------------------------
    private class HttpCallBack<M extends BaseEntity> extends BaseXmlRequest<T> {
        private Class<?> targerClass;
        private int where;
        private Map<String, String> paramsMap;
        //请求体参数,优先使用
        private String paramsStr;
        private List<Part> filePartList;
        String url;
        private long timeStamp;
        private boolean useUnLoginDB = false;

        public HttpCallBack(String url, Listener<T> listener, ErrorListener errorListener, int where, int method, Map<String, String> paramsMap, String paramsStr, List<Part> partList, Class<?> targerClass, long timeStamp) {
            super(method, url, listener, errorListener);
            this.url = url;
            RetryPolicy policy = new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
            this.setRetryPolicy(policy);
            this.where = where;
            this.targerClass = targerClass;
            this.paramsMap = paramsMap;
            this.paramsStr = paramsStr;
            this.filePartList = partList;
            this.timeStamp = timeStamp;
        }

        public void setUseUnLoginDB(boolean useUnLoginDB) {
            this.useUnLoginDB = useUnLoginDB;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            setRequestTimestamp();
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            if (parsed.length() > 4000) {
                for (int i = 0; i < parsed.length(); i += 4000) {
                    if (i + 4000 < parsed.length()) {
                        if (!url.contains("order/undone_orders/") && !url.contains("gongfu/v2/return_order/")) {
                            LogUtils.e(url + " " + parsed.substring(i, i + 4000));
                        }
                    } else {
                        if (!url.contains("order/undone_orders/") && !url.contains("gongfu/v2/return_order/")) {
                            LogUtils.e(url + " " + parsed.substring(i, parsed.length()));
                        }
                    }
                }
            } else {
                if (!url.contains("order/undone_orders/") && !url.contains("gongfu/v2/return_order/")) {
                    LogUtils.e(url + " " + parsed);
                }
            }
            T resultObj = null;
            if (newWorkCallBack != null) {
                resultObj = newWorkCallBack.onParse(where, BaseEntity.class, parsed);
            }
            if (resultObj == null && paseInterface != null) {
                resultObj = paseInterface.paseResult(BaseEntity.class, parsed);
                BaseEntity.ResultBean resultBean = resultObj.getResult();
                if (resultBean != null && resultBean.getData() != null) {
                    try {
                        JSONObject jsonObject = (JSONObject) resultBean.getData();
                        resultBean.setDataJson(jsonObject);
                        Object object = JSON.parseObject(jsonObject.toJSONString(), targerClass);
                        resultBean.setData(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(parsed);
                    try {
                        BaseEntity.ResultBean object = (BaseEntity.ResultBean) JSON.parseObject(jsonObject.getString("result"), targerClass);
                        resultObj.setResult(object);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, String> responseHeaders = response.headers;
            String rawCookies = responseHeaders.get("Set-Cookie");
            if (targerClass == LoginData.class || targerClass == ReLoginData.class) {
                if (resultObj.getResult() != null && "A0006".equals(resultObj.getResult().getState())) {
                    LogUtils.e("登陆存sessionid----------------" + rawCookies.substring(0, rawCookies.indexOf(";")));
                    SPUtils.put(context, "sign", rawCookies.substring(0, rawCookies.indexOf(";")));
                }
            }
            return Response.success(resultObj, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected VolleyError parseNetworkError(VolleyError volleyError) {
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                volleyError = new VolleyError(new String(volleyError.networkResponse.data));
            }
            return volleyError;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            if (paramsMap != null) {
                LogUtils.e("参数:" + paramsMap.toString());
            }
            return paramsMap;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            if (filePartList != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    Part.sendParts(baos, filePartList.toArray(new Part[filePartList.size()]));
                } catch (IOException e) {
                    LogUtils.e(e.getMessage());
                }
                return baos.toByteArray();
            }
            byte[] paramsByte = super.getBody();
            if (paramsByte == null) {
                return encodeParameter(paramsStr, getParamsEncoding());
            }
            return paramsByte;
        }

        @Override
        public String getBodyContentType() {
            if (filePartList != null) {
                return "multipart/form-data; boundary=" + Part.getBoundary();
            }
            if (paseInterface != null && paseInterface instanceof IJsonParseImp) {
                return "application/json";
            }
            return super.getBodyContentType();
        }


        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
//			String appSecret = "6d73a17134d1b211c656e58135c19504";
//			long currentTime = System.currentTimeMillis()/1000;
//			String apiToken = MD5.getMD5("VRR3rHcio7Bprr7H" + appSecret + currentTime);
            Map<String, String> headerMap = new HashMap<String, String>();
            String userToken = (String) SPUtils.get(context, "sign", "");
            headerMap.put("Cookie", userToken);
            headerMap.put("timeStamp", String.valueOf(timeStamp));
//            headerMap.put("timeStamp", String.valueOf(REQUEST_TIMESTAMP));
//			headerMap.put("api-token", apiToken);
//			headerMap.put("deviceId", CommonUtils.getDeviceId(context));
//			headerMap.put("X-Odoo-Db", (String)SPUtils.get(context,"X-Odoo-Db","LBZ20170607"));
            if (useUnLoginDB) {
                headerMap.put("X-Odoo-Db", Constant.UNLOGIN_DB);
            } else if (url.contains("/api/get/host")) {
                headerMap.put("X-Odoo-Db", Constant.UNLOGIN_DB);
            } else {
                if (SPUtils.isLogin(context) || url.contains("/gongfu/v2/authenticate") || SPUtils.isLoginConflict(context)
                        || url.contains("/gongfu/get_captcha") || url.contains("/gongfu/reset_password")) {
                    headerMap.put("X-Odoo-Db", (String) SPUtils.get(context, FILE_KEY_DB_NAME, ""));
                } else {
                    headerMap.put("X-Odoo-Db", Constant.UNLOGIN_DB);
                }
            }
            headerMap.put("phone_type", android.os.Build.MODEL);
            headerMap.put("system_version", android.os.Build.VERSION.RELEASE);
            headerMap.put("app_version", CommonUtils.getVersionName(context));
            headerMap.put("signal", MobileUtil.getNetWorkStatus(context));
            LogUtils.e("Headers:" + headerMap.toString());
            return headerMap;
        }
    }

    public String getHost(String bizName) {
        if (bizName.contains("/api/get/host")) {
            return Constant.UNLOGIN_URL;
        }
        if (SPUtils.isLogin(context) || bizName.contains("/gongfu/v2/authenticate")
                || SPUtils.isLoginConflict(context)
                || bizName.contains("/api/user/agree_item_time")
                || bizName.contains("/gongfu/get_captcha")) {
            return (String) SPUtils.get(context, FILE_KEY_HOST, "");
        } else {
            return Constant.UNLOGIN_URL;
        }
    }

    private byte[] encodeParameter(String params, String paramsEncoding) {
        try {
            if (params == null) {
                return null;
            }
            return params.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    private class RequestErrorListener implements ErrorListener {
        private int what;
        private String url;
        private String bodyParamStr;

        public RequestErrorListener(int what, String url, String bodyParamStr) {
            this.what = what;
            this.url = url;
            this.bodyParamStr = bodyParamStr;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            dismissProgressDialog(what);
            LogUtils.e(error.getMessage(), error.getCause());
            String errorMsg = context.getResources().getString(R.string.network_error);
            if (newWorkCallBack != null) {
                BaseEntity baseEntity = new BaseEntity();
                baseEntity.setMsg(error.getMessage());
                if (errorMsg.contains("Session Expired")) {
                    errorMsg = context.getString(R.string.session_expired);
                }
                try{
                    newWorkCallBack.onFailure(errorMsg, (T) baseEntity, what);
                    UmengUtil.reportError(context, url + "\n" + "参数: " + bodyParamStr + "\n" + error.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    private class RequestSuccessListener<M extends BaseEntity> implements Listener<T> {
        private int what;
        private String url;
        private Class targerClass;
        private String bodyParamStr;

        public RequestSuccessListener(int what, Class targerClass, String url, String bodyParamStr) {
            this.what = what;
            this.url = url;
            this.targerClass = targerClass;
            this.bodyParamStr = bodyParamStr;
        }

        @Override
        public void onResponse(T response) {
            dismissProgressDialog(what);
            if (response.getResult() != null) {
                String repCode = response.getResult().getState();
                if ("A0006".equals(repCode)) {
                    if (newWorkCallBack != null) {
                        try{
                            newWorkCallBack.onSuccess(response, what);
                        }catch (Exception e){
                            UmengUtil.reportError(context, url + "\n" + e.toString());
                            e.printStackTrace();
                        }
                    }
                } else {
                    cellBackError(response, what, url, bodyParamStr);
                }
            } else {
                cellBackError(response, what, url, bodyParamStr);
            }
        }
    }

    private void cellBackError(T response, int what, String url, String paramsMap) {
        if (newWorkCallBack != null) {
            String errorMsg = "";
            if (response.getResult() != null) {
                errorMsg = response.getResult().getError();
            } else {
                errorMsg = response.getError().getMessage();
                //sessino失效
                if (100 == response.getError().getCode()) {
                    SPUtils.loginOut(context);
                    SelfOrderTimeStatisticsUtil.clear();
//					ToastUtil.show(context,"登陆过期，请重新登陆");
                }
            }
//            接口有时候会返回null，防止闪退
            if(errorMsg == null){
                errorMsg = "";
            }
            if (errorMsg.contains("Session Expired")) {
                errorMsg = context.getString(R.string.session_expired);
            }
            try{
                newWorkCallBack.onFailure(errorMsg, response, what);
                if (response.getError() != null) {
                    String uMengErrorString = ObjectTransformUtil.toString(response.getError().getData());
                    UmengUtil.reportError(context, url + "\n" + "参数: " + paramsMap.toString() + "\n" + uMengErrorString);
                } else {
                    UmengUtil.reportError(context, url + "\n" + "参数: " + paramsMap.toString() + "\n" + errorMsg);
                }
            }catch (Exception e){
                UmengUtil.reportError(context, url + "\n" +  e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * 隐藏加载对话框
     */
    private void dismissProgressDialog(int where) {
        if (requestStack.size() > 1) {
            requestStack.remove(where);
        } else {
            if (baseActivity != null) {
                baseActivity.dismissIProgressDialog();
            }
            requestStack.clear();
        }
    }


    /**
     * 网络帮助类回调接口
     */
    public interface NetWorkCallBack<T extends BaseEntity> {
        /**
         * 可根据需求重写该方法，优先调用该方法解析
         * 该方法在线程中调用不要作ui操作
         *
         * @param where
         */
        T onParse(int where, Class<?> targerClass, String result);

        /**
         * 成功返回结果
         *
         * @param result
         * @param where
         */
        void onSuccess(T result, int where);

        /**
         * 网络连接错误 或服务器返回错误结果时回调改方法
         *
         * @param result
         * @param where
         */
        void onFailure(String errMsg, T result, int where);
    }

    /**
     * 解析类具体的实现接口
     */
    public interface PaseInterface<T extends BaseEntity> {
        /**
         * 具体实现解析
         *
         * @param targerClass 要解析的class
         * @param resultStr   返回字符串
         * @return
         */
        T paseResult(Class<?> targerClass, String resultStr);
    }
}
