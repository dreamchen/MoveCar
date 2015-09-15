package com.wedge.movecar.voip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisun.phone.core.voice.Device.CallType;
import com.hisun.phone.core.voice.DeviceListener;
import com.hisun.phone.core.voice.model.CallStatisticsInfo;
import com.hisun.phone.core.voice.model.NetworkStatistic;
import com.hisun.phone.core.voice.util.Log4Util;
import com.wedge.movecar.R;

/**
 * Created by chenerlei on 15/2/9.
 */
public class CallOutActivity extends CCPBaseActivity implements View.OnClickListener {

    // 话筒调节控制区
    private LinearLayout mCallAudio;
    // 键盘
    private ImageView mDiaerpadBtn;
    // 键盘区
    private LinearLayout mDiaerpad;

    // 挂机按钮
    private ImageView mVHangUp;
    // 动态状态显示区
    private TextView mCallStateTips;
    private Chronometer mChronometer;
    // 号码显示区
    private TextView mVtalkName;
    private TextView mVtalkNumber;
    // private TextView mCallStatus;
    // 号码
    private String mNickName;
    private String mPhoneNumber;
    // com.wedge.voip 账号
    private String mVoipAccount;
    // 通话类型，直拨，落地, 回拨
    private String mType = "";

    private Button mPause;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_call_interface);
        isIncomingCall = false;
        mCallType = CallType.VOICE;

        initProwerManager();
        enterIncallMode();
        initResourceRefs();
        initialize();
    }


    /**
     * Initialize all UI elements from resources.
     */
    private void initResourceRefs() {
        mCallTransferBtn = (ImageView) findViewById(R.id.layout_callin_transfer);
        mCallTransferBtn.setEnabled(false);
        mCallAudio = (LinearLayout) findViewById(R.id.layout_call_audio);
        mCallMute = (ImageView) findViewById(R.id.layout_callin_mute);
        mCallHandFree = (ImageView) findViewById(R.id.layout_callin_handfree);
        mVHangUp = (ImageButton) findViewById(R.id.layout_call_reject);
        mCallStateTips = (TextView) findViewById(R.id.layout_callin_duration);


        // call time
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mVtalkName = (TextView) findViewById(R.id.layout_callin_name);
        mVtalkNumber = (TextView) findViewById(R.id.layout_callin_number);

        // 键盘按钮
        mDiaerpadBtn = (ImageView) findViewById(R.id.layout_callin_diaerpad);
        mDiaerpad = (LinearLayout) findViewById(R.id.layout_diaerpad);

        mCallTransferBtn.setOnClickListener(this);
        mDiaerpadBtn.setOnClickListener(this);
        mCallMute.setOnClickListener(this);
        mCallHandFree.setOnClickListener(this);
        mVHangUp.setOnClickListener(this);

//        setupKeypad();
//        mDmfInput = (EditText) findViewById(R.id.dial_input_numer_TXT);

        // mCallStatus = (TextView) findViewById(R.id.call_status);

        mPause = (Button) findViewById(R.id.pause);
        mPause.setOnClickListener(this);
    }

    /**
     * Read parameters or previously saved state of this activity.
     */
    private void initialize() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                finish();
                return;
            }

            mCurrentCallId = bundle.getString("mCurrentCallId");
            mNickName = bundle.getString("mNickName");
            mType = bundle.getString(CCPHelper.VALUE_DIAL_MODE);
            if (TextUtils.isEmpty(mNickName)) {
                mVtalkName.setText(getString(R.string.voip_unknown_user));
            } else {
                mVtalkName.setText(mNickName);
            }

            if (mType.equals(CCPHelper.VALUE_DIAL_MODE_FREE)) {
                // voip免费通话时显示voip账号
                mVoipAccount = bundle.getString(CCPHelper.VALUE_DIAL_VOIP_INPUT);
                if (mVoipAccount == null) {
                    finish();
                    return;
                }
                mVtalkNumber.setText(mVoipAccount);
            } else {
                // 直拨及回拨显示号码
                mPhoneNumber = bundle.getString(CCPHelper.VALUE_DIAL_VOIP_INPUT);
                mVtalkNumber.setText(mPhoneNumber);
            }
        }
    }

    boolean isCallPause = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pause:
                if (checkeDeviceHelper() && !TextUtils.isEmpty(mCurrentCallId)) {
                    if (isCallPause) {
                        getDeviceHelper().resumeCall(mCurrentCallId);
                        isCallPause = false;

                    } else {

                        getDeviceHelper().pauseCall(mCurrentCallId);
                        isCallPause = true;
                    }

                    mPause.setText(isCallPause ? "resume" : "pause");

                }
                break;
//            // keypad
//            case R.id.zero: {
//                keyPressed(KeyEvent.KEYCODE_0);
//                return;
//            }
//            case R.id.one: {
//                keyPressed(KeyEvent.KEYCODE_1);
//                return;
//            }
//            case R.id.two: {
//                keyPressed(KeyEvent.KEYCODE_2);
//                return;
//            }
//            case R.id.three: {
//                keyPressed(KeyEvent.KEYCODE_3);
//                return;
//            }
//            case R.id.four: {
//                keyPressed(KeyEvent.KEYCODE_4);
//                return;
//            }
//            case R.id.five: {
//                keyPressed(KeyEvent.KEYCODE_5);
//                return;
//            }
//            case R.id.six: {
//                keyPressed(KeyEvent.KEYCODE_6);
//                return;
//            }
//            case R.id.seven: {
//                keyPressed(KeyEvent.KEYCODE_7);
//                return;
//            }
//            case R.id.eight: {
//                keyPressed(KeyEvent.KEYCODE_8);
//                return;
//            }
//            case R.id.nine: {
//                keyPressed(KeyEvent.KEYCODE_9);
//                return;
//            }
//            case R.id.star: {
//                keyPressed(KeyEvent.KEYCODE_STAR);
//                return;
//            }
//            case R.id.pound: {
//                keyPressed(KeyEvent.KEYCODE_POUND);
//                return;
//            }
//
//            // keybad end ...
            case R.id.layout_call_reject:
                doHandUpReleaseCall();
                break;
            case R.id.layout_callin_mute:
                // 设置静音
                setMuteUI();
                break;
            case R.id.layout_callin_handfree:
                // 设置免提
                sethandfreeUI();
                break;
//
//            case R.id.layout_callin_diaerpad:
//
//                // 设置键盘
//                setDialerpadUI();
//                break;
//            case R.id.layout_callin_transfer: // select com.wedge.voip ...
//                Intent intent = new Intent(this, GetNumberToTransferActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_CALL_TRANSFER);
//
//                break;
            default:
                break;
        }
    }

    @Override
    protected void doHandUpReleaseCall() {
        super.doHandUpReleaseCall();
        // 挂断电话
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] Voip talk hand up, CurrentCallId " + mCurrentCallId);
        try {
            if (mCurrentCallId != null && checkeDeviceHelper()) {
                getDeviceHelper().releaseCall(mCurrentCallId);
//                stopVoiceRecording(mCurrentCallId);
            }

            // for XINWEI
            getBaseHandle().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (!isConnect) {
                        finish();
                    }

                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 延时关闭界面
     */
    final Runnable finish = new Runnable() {
        public void run() {
            finish();
        }
    };


    @Override
    protected void onCallAlerting(String callid) {
        super.onCallAlerting(callid);
        // 连接到对端用户，播放铃音
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip alerting!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
            mCallStateTips.setText(getString(R.string.voip_calling_wait));
        }
    }

    @Override
    protected void onCallProceeding(String callid) {
        super.onCallProceeding(callid);
        // 连接到服务器
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip on call proceeding!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
            mCallStateTips.setText(getString(R.string.voip_call_connect));
        }

    }

    @Override
    protected void onCallAnswered(String callid) {
        super.onCallAnswered(callid);
        // 对端应答
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip on call answered!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
            isConnect = true;
            mCallMute.setEnabled(true);
            initCallTools();
            // 2013/09/23
            // Show call state position is used to display the packet loss rate
            // and delay
//            mCallStateTips.setVisibility(View.GONE);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();

            mCallStateTips.setText("");
            if (getBaseHandle() != null) {
                getBaseHandle().sendMessage(getBaseHandle().obtainMessage(CCPBaseActivity.WHAT_ON_CODE_CALL_STATUS));
            }

//            startVoiceRecording(callid);
        }
    }

    @Override
    protected void onCallReleased(String callid) {
        super.onCallReleased(callid);
        // 远端挂断，本地挂断在onClick中处理
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip on call released!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
//            stopVoiceRecording(callid);
            finishCalling();
        }
    }

    @Override
    protected void onMakeCallFailed(String callid, DeviceListener.Reason reason) {
        super.onMakeCallFailed(callid, reason);
        // 发起通话失败
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip on call makecall failed!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
            finishCalling(reason);
        }
    }

    @Override
    protected void onCallback(int status, String self, String dest) {
        super.onCallback(status, self, dest);
        // 回拨通话回调
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallOutActivity] com.wedge.voip on callback status : " + status);
        if (status == 170010) {
            mCallStateTips.setText(getString(R.string.voip_call_back_connect));
        } else {
            getBaseHandle().postDelayed(finish, 5000);
            if (status == 0) {
                mCallStateTips.setText(getString(R.string.voip_call_back_success));
            } else if (status == 121002) {
                mCallStateTips.setText(getString(R.string.voip_call_fail_no_cash));
            } else {
                mCallStateTips.setText(getString(R.string.voip_call_fail));
            }
            mVHangUp.setClickable(false);
            mVHangUp.setBackgroundResource(R.mipmap.call_interface_non_red_button);
        }
    }


    public void initCallTools() {
        if (checkeDeviceHelper()) {
            try {
                isMute = getDeviceHelper().getMuteStatus();
                isHandsfree = getDeviceHelper().getLoudsSpeakerStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void handleNotifyMessage(Message msg) {
        super.handleNotifyMessage(msg);

        switch (msg.what) {
            case CCPBaseActivity.WHAT_ON_CODE_CALL_STATUS:

                if (!checkeDeviceHelper()) {
                    return;
                }

                if (!isConnect) {
                    return;
                }
                CallStatisticsInfo callStatistics = getDeviceHelper().getCallStatistics(CallType.VOICE);

                NetworkStatistic trafficStats = null;
                if (mCallType == CallType.VOICE) {
                    trafficStats = getDeviceHelper().getNetworkStatistic(mCurrentCallId);
                }

                if (callStatistics != null) {
                    int fractionLost = callStatistics.getFractionLost();
                    int rttMs = callStatistics.getRttMs();
                    if (mCallStateTips != null) {
                        if (trafficStats != null) {
                            mCallStateTips.setText(getString(R.string.str_call_traffic_status, rttMs, (fractionLost / 255), trafficStats.getTxBytes() / 1024,
                                    trafficStats.getRxBytes() / 1024));
                        } else {
                            mCallStateTips.setText(getString(R.string.str_call_status, rttMs, (fractionLost / 255)));
                        }
                    }
                }

                if (getBaseHandle() != null) {
                    Message callMessage = getBaseHandle().obtainMessage(CCPBaseActivity.WHAT_ON_CODE_CALL_STATUS);
                    getBaseHandle().sendMessageDelayed(callMessage, 4000);
                }
                break;

            // This call may be redundant, but to ensure compatibility system event
            // more,
            // not only is the system call
            case CCPHelper.WHAT_ON_RECEIVE_SYSTEM_EVENTS:

                doHandUpReleaseCall();
                break;

            default:
                break;
        }
    }

    /**
     * 根据状态,修改按钮属性及关闭操作
     *
     * @param reason
     */
    private void finishCalling(DeviceListener.Reason reason) {
        try {
            isConnect = false;
            mChronometer.stop();
            mChronometer.setVisibility(View.GONE);
            mCallStateTips.setVisibility(View.VISIBLE);

            mCallHandFree.setClickable(false);
            mCallMute.setClickable(false);
//            mVHangUp.setClickable(false);
            mDiaerpadBtn.setClickable(false);
            mDiaerpadBtn.setImageResource(R.mipmap.call_interface_diaerpad);
            mCallHandFree.setImageResource(R.mipmap.call_interface_hands_free);
            mCallMute.setImageResource(R.mipmap.call_interface_mute);
            mVHangUp.setBackgroundResource(R.mipmap.call_interface_non_red_button);
            getBaseHandle().postDelayed(finish, 3000);
            // 处理通话结束状态
            if (reason == DeviceListener.Reason.DECLINED || reason == DeviceListener.Reason.BUSY) {
                mCallStateTips.setText(getString(R.string.voip_calling_refuse));
                getBaseHandle().removeCallbacks(finish);
            } else {
                if (reason == DeviceListener.Reason.CALLMISSED) {
                    mCallStateTips.setText(getString(R.string.voip_calling_timeout));
                } else if (reason == DeviceListener.Reason.MAINACCOUNTPAYMENT) {
                    mCallStateTips.setText(getString(R.string.voip_call_fail_no_cash));
                } else if (reason == DeviceListener.Reason.UNKNOWN) {
                    mCallStateTips.setText(getString(R.string.voip_calling_finish));
                } else if (reason == DeviceListener.Reason.NOTRESPONSE) {
                    mCallStateTips.setText(getString(R.string.voip_call_fail));
                } else if (reason == DeviceListener.Reason.VERSIONNOTSUPPORT) {
                    mCallStateTips.setText(getString(R.string.str_voip_not_support));
                } else if (reason == DeviceListener.Reason.OTHERVERSIONNOTSUPPORT) {
                    mCallStateTips.setText(getString(R.string.str_other_voip_not_support));
                } else {

                    ThirdSquareError(reason);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Increased Voip P2P, direct error code:
     * <p>
     * Title: ThirdSquareError
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param reason
     */
    private void ThirdSquareError(DeviceListener.Reason reason) {
        if (reason == DeviceListener.Reason.AUTHADDRESSFAILED) {// -- 700 第三方鉴权地址连接失败
            mCallStateTips.setText(getString(R.string.voip_call_fail_connection_failed_auth));
        } else if (reason == DeviceListener.Reason.MAINACCOUNTPAYMENT) {// -- 701 第三方主账号余额不足

            mCallStateTips.setText(getString(R.string.voip_call_fail_no_pay_account));
        } else if (reason == DeviceListener.Reason.MAINACCOUNTINVALID) { // -- 702 第三方应用ID未找到

            mCallStateTips.setText(getString(R.string.voip_call_fail_not_find_appid));
        } else if (reason == DeviceListener.Reason.CALLERSAMECALLED) {// -- 704
            // 第三方应用未上线限制呼叫已配置测试号码

            mCallStateTips.setText(getString(R.string.voip_call_fail_not_online_only_call));
        } else if (reason == DeviceListener.Reason.SUBACCOUNTPAYMENT) {// -- 705 第三方鉴权失败，子账号余额

            mCallStateTips.setText(getString(R.string.voip_call_auth_failed));
        } else {
            mCallStateTips.setText(getString(R.string.voip_calling_network_instability));
        }
    }

    /**
     * 用于挂断时修改按钮属性及关闭操作
     */
    private void finishCalling() {
        try {
            if (isConnect) {
                // set Chronometer view gone..
                isConnect = false;
                mChronometer.stop();
                mChronometer.setVisibility(View.GONE);
                // 接通后关闭
                mCallStateTips.setVisibility(View.VISIBLE);
                mCallStateTips.setText(R.string.voip_calling_finish);
                getBaseHandle().postDelayed(finish, 3000);
            } else {
                // 未接通，直接关闭
                finish();
            }
            mCallHandFree.setClickable(false);
            mCallMute.setClickable(false);
            mVHangUp.setClickable(false);
            mDiaerpadBtn.setClickable(false);
            mDiaerpadBtn.setImageResource(R.mipmap.call_interface_diaerpad);
            mCallHandFree.setImageResource(R.mipmap.call_interface_hands_free);
            mCallMute.setImageResource(R.mipmap.call_interface_mute);
            mVHangUp.setBackgroundResource(R.mipmap.call_interface_non_red_button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}