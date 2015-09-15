/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.wedge.movecar.voip;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hisun.phone.core.voice.CCPCall;
import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.Device.CallType;
import com.hisun.phone.core.voice.DeviceListener;
import com.hisun.phone.core.voice.listener.OnVoIPListener;
import com.hisun.phone.core.voice.model.setup.UserAgentConfig;
import com.hisun.phone.core.voice.util.Log4Util;
import com.wedge.movecar.CarApplication;

import java.util.HashMap;
import java.util.Map;

import static com.hisun.phone.core.voice.util.UserAgentUtils.getOSVersion;
import static com.hisun.phone.core.voice.util.UserAgentUtils.getVendor;

/**
 * VOIP Helper for Activity, it already has been did something important jobs
 * that activity just show state of ui by handler.
 * <p/>
 * Before running the demo, you should be become a developer by CCP web site so that
 * you can get the main account and token, otherwise also see test info.
 *
 * @version 1.0.0
 */
public class CCPHelper implements CCPCall.InitListener, DeviceListener, OnVoIPListener {

    public static final String DEMO_TAG = "EUExCCP";

    public static final String VALUE_DIAL_MODE_FREE = "voip_talk";
    public static final String VALUE_DIAL_MODE_DIRECT = "direct_talk";
    public static final String VALUE_DIAL_MODE_BACK = "back_talk";
    public final static String VALUE_DIAL_MODE_VEDIO = "vedio_talk";
    public static final String VALUE_DIAL_MODE = "mode";

    public final static String VALUE_DIAL_SOURCE_PHONE = "srcPhone";
    public final static String VALUE_DIAL_VOIP_INPUT = "VoIPInput";

    public static final int WHAT_ON_RECEIVE_SYSTEM_EVENTS = 0x208C;
    public static final int WHAT_ON_CALLVIDEO_RATIO_CHANGED = 0x2032;

    private static String KEY_IP;
    private static String KEY_PORT;
    private static String KEY_SID;
    private static String KEY_PWD;
    private static String KEY_SUBID;
    private static String KEY_SUBPWD;

    private static Context context;
    private static boolean isInit = false;

    public boolean getIsInit() {
        return isInit;
    }

    private Device device;
    private RegistCallBack mCallback;

    private static CCPHelper sInstance;

    /**
     * <p>Title: getInstance</p>
     * <p>Description: </p>
     *
     * @return
     */
    public static CCPHelper getInstance(Context mContext) {
        if (sInstance == null) {
            sInstance = new CCPHelper(mContext);
        }
        return sInstance;
    }

    public static CCPHelper getInstance() {
        if (sInstance == null) {
            sInstance = new CCPHelper(CarApplication.getInstance());
        }
        return sInstance;
    }

    public CCPHelper(Context mContext) {
        this.context = mContext;
    }


    public void initSDK(String key_ip, String key_port, String key_sid, String key_pwd, String key_subid, String key_subpwd) {
        this.KEY_IP = key_ip;
        this.KEY_PORT = key_port;
        this.KEY_SID = key_sid;
        this.KEY_PWD = key_pwd;
        this.KEY_SUBID = key_subid;
        this.KEY_SUBPWD = key_subpwd;

        CCPCall.init(context, this);
        this.isInit = true;
    }

    public Device getDevice() {
        return device;
    }

    /**
     * <p>Title: createDevice</p>
     * <p>Description: modify by version 3.5</p>
     *
     * @throws Exception
     */
    private void createDevice() throws Exception {
        // 封装参数
        Map<String, String> params = new HashMap<String, String>();
        // * REST服务器地址
        params.put(UserAgentConfig.KEY_IP, KEY_IP);
        // * REST服务器端口
        params.put(UserAgentConfig.KEY_PORT, KEY_PORT);
        // * VOIP账号 , 可以填入CCP网站Demo管理中的测试VOIP账号信息
        params.put(UserAgentConfig.KEY_SID, KEY_SID);
        // * VOIP账号密码, 可以填入CCP网站Demo管理中的测试VOIP账号密码
        params.put(UserAgentConfig.KEY_PWD, KEY_PWD);
        // * 子账号, 可以填入CCP网站Demo管理中的测试子账号信息
        params.put(UserAgentConfig.KEY_SUBID, KEY_SUBID);
        // * 子账号密码, 可以填入CCP网站Demo管理中的测试子账号密码
        params.put(UserAgentConfig.KEY_SUBPWD, KEY_SUBPWD);
        // User-Agent
        String ua = "Android;"
                + getOSVersion() + ";"
                + com.hisun.phone.core.voice.Build.SDK_VERSION + ";"
                + com.hisun.phone.core.voice.Build.LIBVERSION.FULL_VERSION + ";"
                + getVendor() + "-" + getDevice() + ";";
        ua = ua + getDeviceNO() + ";" + System.currentTimeMillis() + ";";

        Log4Util.d("EUExCCP", ua);

        params.put(UserAgentConfig.KEY_UA, ua);

        // 创建Device
        device = CCPCall.createDevice(this /* DeviceListener */, params);
        // 设置当呼入请求到达时, 唤起的界面
        Intent intent = new Intent(context, CallInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        device.setIncomingIntent(pendingIntent);

        device.setOnVoIPListener(this);
        Log4Util.d(DEMO_TAG, "[onInitialized] sdk init success. done");
    }

    /**
     * 获取设备NO
     *
     * @return
     */
    private String getDeviceNO() {
        Log4Util.d("EUExCCP", "getDevicENO");
        if (!TextUtils.isEmpty(getDeviceId())) {
            return getDeviceId();
        }

        if (!TextUtils.isEmpty(getMacAddress())) {
            return getMacAddress();
        }
        return " ";
    }

    /**
     * 获取设备ID
     *
     * @return
     */
    private String getDeviceId() {
        Log4Util.d("EUExCCP", "getDeviceId");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getDeviceId();
        }

        return null;

    }

    /**
     * 获取设备MAC
     *
     * @return
     */
    private String getMacAddress() {
        // start get mac address
        Log4Util.d("EUExCCP", "getMacAddress");
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMan != null) {
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                // 48位，如FA:34:7C:6D:E4:D7
                return wifiInf.getMacAddress();
            }
        }
        return null;
    }


    /**
     * @param mode
     */
    public void setAudioMode(int mode) {
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(mode);
        }
    }

    /**
     * handler 转换消息id
     */
    public static final int WHAT_ON_CONNECT = 0x2000;
    public static final int WHAT_ON_DISCONNECT = 0x2001;
    public static final int WHAT_INIT_SUCCESS = 0x0000;
    public static final int WHAT_INIT_ERROR = 0x200A;
    public static final int WHAT_ON_CALL_ALERTING = 0x2002;
    public static final int WHAT_ON_CALL_ANSWERED = 0x2003;
    public static final int WHAT_ON_CALL_PAUSED = 0x2004;
    public static final int WHAT_ON_CALL_PAUSED_REMOTE = 0x2005;
    public static final int WHAT_ON_CALL_RELEASED = 0x2006;
    public static final int WHAT_ON_CALL_PROCEEDING = 0x2007;
    public static final int WHAT_ON_CALL_TRANSFERED = 0x2008;
    public static final int WHAT_ON_CALL_MAKECALL_FAILED = 0x2009;
    public static final int WHAT_ON_CALL_BACKING = 0x201B;

    /**
     * handler for update activity
     */
    private Handler handler;

    /**
     * set handler.
     *
     * @param handler activity handler
     */
    public void setHandler(final Handler handler) {
        this.handler = handler;
    }

    long t = 0;
    private long currentTime = 0;

    /**
     * send object to activity by handler.
     *
     * @param what message id of handler
     * @param obj  message of handler
     */
    private void sendTarget(int what, Object obj) {
        t = System.currentTimeMillis();
        // for kinds of mobile phones
        while (handler == null && (System.currentTimeMillis() - t < 3200)) {
            Log4Util.d(DEMO_TAG, "[VoiceHelper] handler is null, activity maybe destory, wait...");
            try {
                Thread.sleep(80L);
            } catch (InterruptedException e) {
            }
        }

        if (handler == null) {
            Log4Util.d(DEMO_TAG, "[VoiceHelper] handler is null, need adapter it.");
            return;
        }

        Message msg = Message.obtain(handler);
        msg.what = what;
        msg.obj = obj;
        msg.sendToTarget();
    }

    @Override
    public void onInitialized() {
        try {
            createDevice();
            if (mCallback != null) {
                mCallback.onRegistResult(WHAT_INIT_SUCCESS, "SDK初始化成功 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
            onError(e);
        }
    }

    @Override
    public void onError(Exception e) {
        Log4Util.d(DEMO_TAG, "[onError] " + "SDK init error , " + e.getMessage());
        if (mCallback != null) {
            mCallback.onRegistResult(WHAT_INIT_ERROR, "SDK初始化错误, " + e.getMessage());
        }
        CCPCall.shutdown();
    }

    @Override
    public void onReceiveEvents(CCPEvents ccpEvents) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnect(Reason reason) {

    }

    @Override
    public void onFirewallPolicyEnabled() {

    }

    @Override
    public void onCallProceeding(String callid) {
        sendTarget(WHAT_ON_CALL_PROCEEDING, callid);
    }

    @Override
    public void onCallAlerting(String callid) {
        sendTarget(WHAT_ON_CALL_ALERTING, callid);
    }

    @Override
    public void onCallReleased(String callid) {
        sendTarget(WHAT_ON_CALL_RELEASED, callid);
    }

    @Override
    public void onCallAnswered(String callid) {
        sendTarget(WHAT_ON_CALL_ANSWERED, callid);
    }

    @Override
    public void onCallPaused(String callid) {

    }

    @Override
    public void onCallPausedByRemote(String callid) {

    }

    @Override
    public void onMakeCallFailed(String callid, Reason reason) {
        Bundle b = new Bundle();
        b.putString(Device.CALLID, callid);
        b.putSerializable(Device.REASON, reason);
        sendTarget(WHAT_ON_CALL_MAKECALL_FAILED, b);
    }

    @Override
    public void onCallTransfered(String callid, String destionation) {

        Bundle b = new Bundle();
        b.putString(Device.CALLID, callid);
        b.putString(Device.DESTIONATION, destionation);
        sendTarget(WHAT_ON_CALL_TRANSFERED, b);
    }

    @Override
    public void onCallback(int status, String self, String dest) {
        Bundle b = new Bundle();
        b.putInt(Device.CBSTATE, status);
        b.putString(Device.SELFPHONE, self);
        b.putString(Device.DESTPHONE, dest);
        sendTarget(WHAT_ON_CALL_BACKING, b);
    }

    /**
     * @param s
     * @param i
     * @deprecated
     */
    @Override
    public void onCallMediaUpdateRequest(String s, int i) {

    }

    @Override
    public void onSwitchCallMediaTypeRequest(String s, CallType callType) {

    }

    /**
     * @param s
     * @param i
     * @deprecated
     */
    @Override
    public void onCallMediaUpdateResponse(String s, int i) {

    }

    @Override
    public void onSwitchCallMediaTypeResponse(String s, CallType callType) {

    }

    /**
     * @param s
     * @param s1
     * @deprecated
     */
    @Override
    public void onCallVideoRatioChanged(String s, String s1) {

    }

    @Override
    public void onCallVideoRatioChanged(String s, int i, int i1) {

    }

    /**
     * @param s
     * @param i
     * @deprecated
     */
    @Override
    public void onCallMediaInitFailed(String s, int i) {

    }

    @Override
    public void onCallMediaInitFailed(String s, CallType callType) {

    }

    @Override
    public void onTransferStateSucceed(String s, boolean b) {

    }

    public void release() {
        this.context = null;
        this.device = null;
        this.handler = null;

        sInstance = null;
    }


    /**
     * @param rcb
     */
    public void setRegistCallback(RegistCallBack rcb) {
        this.mCallback = rcb;
    }

    /**
     * @author Jorstin Chan
     * @version 3.6
     * @ClassName: RegistCallBack.java
     * @Description: TODO
     * @date 2013-12-12
     */
    public interface RegistCallBack {
        /**
         * WHAT_INIT_ERROR
         * call back when regist over.
         *
         * @param reason {@link WHAT_INIT_ERROR} {@link WHAT_ON_CONNECT} {@link WHAT_ON_DISCONNECT}
         * @param msg    regist failed message
         * @see CCPHelper#WHAT_ON_CONNECT
         * @see CCPHelper#WHAT_INIT_ERROR
         * @see CCPHelper#WHAT_ON_DISCONNECT
         */
        void onRegistResult(int reason, String msg);
    }
}
