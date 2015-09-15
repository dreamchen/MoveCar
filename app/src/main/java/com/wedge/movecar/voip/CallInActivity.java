package com.wedge.movecar.voip;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.Device.CallType;
import com.hisun.phone.core.voice.model.CallStatisticsInfo;
import com.hisun.phone.core.voice.model.NetworkStatistic;
import com.hisun.phone.core.voice.util.Log4Util;
import com.hisun.phone.core.voice.util.VoiceUtil;
import com.wedge.movecar.R;

/**
 * Created by chenerlei on 15/3/6.
 */
public class CallInActivity extends CCPBaseActivity implements View.OnClickListener {

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
    private TextView mCallStatus;
    // 号码
    private String mNickName;
    private String mPhoneNumber;

    // com.wedge.voip 账号
    private String mVoipAccount;

    Intent currIntent;

    // 透传号码参数
    private static final String KEY_TEL = "tel";
    // 透传名称参数
    private static final String KEY_NAME = "nickname";
    private static final int REQUEST_CODE_VOIP_CALL = 120;
    private static final int REQUEST_CODE_CALL_TRANSFER = 130;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isIncomingCall = true;
        initProwerManager();
        enterIncallMode();
        currIntent = getIntent();
        initialize(currIntent);
        initResourceRefs();
    }


    /**
     * Initialize all UI elements from resources.
     */
    private void initResourceRefs() {
        isConnect = false;
        setContentView(R.layout.layout_callin);

        mVtalkName = (TextView) findViewById(R.id.layout_callin_name);
        mVtalkNumber = (TextView) findViewById(R.id.layout_callin_number);
        ((ImageButton) findViewById(R.id.layout_callin_cancel)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.layout_callin_accept)).setOnClickListener(this);

        setDisplayNameNumber();
    }

    private void setDisplayNameNumber() {
        if (mCallType == Device.CallType.VOICE) {
            // viop call ...
            if (!TextUtils.isEmpty(mVoipAccount)) {
                mVtalkNumber.setText(mVoipAccount);
            }
            if(!TextUtils.isEmpty(mPhoneNumber)){

                mVtalkNumber.setText(mPhoneNumber);
            }
            if(!TextUtils.isEmpty(mNickName)){
                mVtalkName.setText(mNickName);
            }

        } else {
            // viop call ...
            if (!TextUtils.isEmpty(mPhoneNumber)) {
                mVtalkNumber.setText(mPhoneNumber);
                Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] mPhoneNumber " + mPhoneNumber);
            }
            if (!TextUtils.isEmpty(mNickName)) {
                mVtalkName.setText(mNickName);
                Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] VtalkName" + mVtalkName);
            } else {
                mVtalkName.setText(R.string.voip_unknown_user);
            }
        }
    }

    /**
     * Read parameters or previously saved state of this activity.
     */
    private void initialize(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            finish();
            return;
        }
        mVoipAccount = extras.getString(Device.CALLER);
        mCurrentCallId = extras.getString(Device.CALLID);
        mCallType = (CallType) extras.get(Device.CALLTYPE);
        // 传入数据是否有误
        if (mVoipAccount == null || mCurrentCallId == null) {
            finish();
            return;
        }
        // 透传信息
        String[] infos = extras.getStringArray(Device.REMOTE);
        if (infos != null && infos.length > 0) {
            for (String str : infos) {
                if (str.startsWith(KEY_TEL)) {
                    mPhoneNumber = VoiceUtil.getLastwords(str, "=");
                } else if (str.startsWith(KEY_NAME)) {
                    mNickName = VoiceUtil.getLastwords(str, "=");
                }
            }
        }

    }

    public void initCallHold() {
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] initCallHold.收到呼叫连接，初始化通话界面.");
        isConnect = true;
        setContentView(R.layout.layout_call_interface);
        mCallTransferBtn = (ImageView) findViewById(R.id.layout_callin_transfer);
        mCallTransferBtn.setOnClickListener(this);

        mCallStateTips = (TextView) findViewById(R.id.layout_callin_duration);
        mCallMute = (ImageView) findViewById(R.id.layout_callin_mute);
        mCallHandFree = (ImageView) findViewById(R.id.layout_callin_handfree);
        mVHangUp = (ImageView) findViewById(R.id.layout_call_reject);
        mVtalkName = (TextView) findViewById(R.id.layout_callin_name);
        //mCallStatus = (TextView) findViewById(R.id.call_status);


        mVtalkName.setVisibility(View.VISIBLE);
        //mCallStatus.setVisibility(View.VISIBLE);

        // 显示时间，隐藏状态
        // 2013/09/23
        // Show call state position is used to display the packet loss rate and
        // delay
        // mCallStateTips.setVisibility(View.GONE);

        // 键盘
        mDiaerpadBtn = (ImageView) findViewById(R.id.layout_callin_diaerpad);
        mDiaerpad = (LinearLayout) findViewById(R.id.layout_diaerpad);


//        setupKeypad();
//        mDmfInput = (EditText) findViewById(R.id.dial_input_numer_TXT);

        mDiaerpadBtn.setOnClickListener(this);
        mCallMute.setOnClickListener(this);
        mCallHandFree.setOnClickListener(this);
        mVHangUp.setOnClickListener(this);

        setDisplayNameNumber();
        initCallTools();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_select_voip: // select com.wedge.voip ...
//                Intent intent = new Intent(this, InviteInterPhoneActivity.class);
//                intent.putExtra("create_to", InviteInterPhoneActivity.CREATE_TO_VOIP_CALL);
//                startActivityForResult(intent, REQUEST_CODE_VOIP_CALL);
//
//                // keypad
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
////            }
//
            case R.id.layout_callin_accept:
//            case EUExUtil.getResIdID("video_botton_begin: // video ..
                // 接受呼叫
                // mTime = 0;
                try {
                    if (checkeDeviceHelper() && mCurrentCallId != null) {
                        getDeviceHelper().acceptCall(mCurrentCallId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] acceptCall...");
                break;
            case R.id.layout_callin_mute:
                // 设置静音
                setMuteUI();
                break;
            case R.id.layout_callin_handfree:
                // 设置免提
                sethandfreeUI();
                break;

            case R.id.layout_callin_diaerpad:

                // 设置键盘
//                setDialerpadUI();
                break;
            case R.id.layout_callin_cancel:
            case R.id.layout_call_reject:

                doHandUpReleaseCall();
                break;

        // video back ...
//            case R.id.video_stop:
//                mVideoStop.setEnabled(false);
//                doHandUpReleaseCall();
//
//                break;
//
//            case R.id.video_switch:
//                if (checkeDeviceHelper()) {
//                    CallType callType = getDeviceHelper().getCallType(mCurrentCallId);
//                    if (callType == Device.CallType.VOICE) {
//                        getDeviceHelper().updateCallType(mCurrentCallId, Device.CallType.VIDEO);
//                    } else {
//                        getDeviceHelper().updateCallType(mCurrentCallId, Device.CallType.VOICE);
//                    }
//                }
//
//                break;
//            case R.id.camera_switch:
//                // check for availability of multiple cameras
//                if (cameraInfos.length == 1) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setMessage(this.getString(R.string.camera_alert)).setNeutralButton(R.string.dialog_alert_close, null);
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                    return;
//                }
//                mCameraSwitch.setEnabled(false);
//
//                // OK, we have multiple cameras.
//                // Release this camera -> cameraCurrentlyLocked
//                cameraCurrentlyLocked = (cameraCurrentlyLocked + 1) % numberOfCameras;
//                comportCapbilityIndex(cameraInfos[cameraCurrentlyLocked].caps);
//
//                if (checkeDeviceHelper()) {
//
//                    getDeviceHelper().selectCamera(cameraCurrentlyLocked, mCameraCapbilityIndex, 15, Device.Rotate.Rotate_Auto, false);
//
//                    if (cameraCurrentlyLocked == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                        defaultCameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
//                        Toast.makeText(CallInActivity.this, R.string.camera_switch_front, Toast.LENGTH_SHORT).show();
//                    } else {
//                        defaultCameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
//                        Toast.makeText(CallInActivity.this, R.string.camera_switch_back, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                mCameraSwitch.setEnabled(true);
//                break;
//            case R.id.layout_callin_transfer: // select com.wedge.voip ...
//                Intent i = new Intent(this, GetNumberToTransferActivity.class);
//                startActivityForResult(i, REQUEST_CODE_CALL_TRANSFER);
//
//                break;
            default:
                break;
        }
    }

    @Override
    protected void doHandUpReleaseCall() {
        super.doHandUpReleaseCall();
        try {
            if (isConnect) {
                // 通话中挂断
                if (checkeDeviceHelper() && mCurrentCallId != null) {
                    getDeviceHelper().releaseCall(mCurrentCallId);
//                    stopVoiceRecording(mCurrentCallId);
                }
            } else {
                // 呼入拒绝
                if (checkeDeviceHelper() && mCurrentCallId != null) {
                    getDeviceHelper().rejectCall(mCurrentCallId, 6);
                }
                finish();
            }

            Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] call stop isConnect: " + isConnect);
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

    private void releaseCurrCall(boolean releaseAll) {
        currIntent = null;
        if (getBaseHandle() != null && releaseAll) {
            setBaseHandle(null);
        }
        mCallTransferBtn = null;
        mCallMute = null;
        mCallHandFree = null;
        mVHangUp = null;
        mCallStateTips = null;
        mVtalkName = null;
        mVtalkNumber = null;

        if (checkeDeviceHelper()) {
            if (isMute) {
                getDeviceHelper().setMute(!isMute);
            }
            if (isHandsfree) {
                getDeviceHelper().enableLoudsSpeaker(!isMute);
            }
            if (TextUtils.isEmpty(mCurrentCallId))
                stopVoiceRecording(mCurrentCallId);
        }
        mPhoneNumber = null;
        CCPHelper.getInstance().setAudioMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 用于挂断时修改按钮属性及关闭操作
     */
    private void finishCalling() {
        try {
            if (isConnect) {
                mChronometer.stop();
                mCallStateTips.setVisibility(View.VISIBLE);

                isConnect = false;
//                if (mCallType == CallType.VIDEO) {
//                    mVideoLayout.setVisibility(View.GONE);
//                    mVideoIcon.setVisibility(View.VISIBLE);
//                    mVideoTopTips.setVisibility(View.VISIBLE);
//                    mCameraSwitch.setVisibility(View.GONE);
//
//                    mLoaclVideoView.removeAllViews();
//                    mLoaclVideoView.setVisibility(View.GONE);
//
//					/*
//					 * if(mVideoStop.isEnabled()) {
//					 * mVideoTopTips.setText(getString
//					 * (R.string.str_video_call_end,
//					 * CCPConfig.VoIP_ID.substring(CCPConfig.VoIP_ID.length() -
//					 * 3, CCPConfig.VoIP_ID.length()))); } else {
//					 */
//                    mVideoTopTips.setText(R.string.voip_calling_finish);
//                    // }
//                    // bottom can't click ...
//                } else {
                mChronometer.setVisibility(View.GONE);
                mCallStateTips.setText(R.string.voip_calling_finish);
                mCallHandFree.setClickable(false);
                mCallMute.setClickable(false);
                // mCallTransferBtn.setClickable(false);
                mVHangUp.setClickable(false);
                mDiaerpadBtn.setClickable(false);
                mDiaerpadBtn.setImageResource(R.mipmap.call_interface_diaerpad);
                mCallHandFree.setImageResource(R.mipmap.call_interface_hands_free);
                mCallMute.setImageResource(R.mipmap.call_interface_mute);
                mVHangUp.setBackgroundResource(R.mipmap.call_interface_non_red_button);
//                }

                // 延时关闭
                getBaseHandle().postDelayed(finish, 3000);
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCallAnswered(String callid) {
        super.onCallAnswered(callid);
        // answer
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] com.wedge.voip on call answered!!");
        if (callid != null && callid.equals(mCurrentCallId)) {
            if (mCallType == CallType.VIDEO) {
//                initResVideoSuccess();
                //getDeviceHelper().enableLoudsSpeaker(true);
            } else {
                initialize(currIntent);
                // com.wedge.voip other ..
                initCallHold();
                isConnect = true;
            }

            mChronometer = (Chronometer) findViewById(R.id.chronometer);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();
            if (getBaseHandle() != null) {
                getBaseHandle().sendMessage(getBaseHandle().obtainMessage(CCPBaseActivity.WHAT_ON_CODE_CALL_STATUS));
            }

            //startVoiceRecording(callid);
        }
    }

    @Override
    protected void onCallReleased(String callid) {
        super.onCallReleased(callid);
        // 挂断
        Log4Util.d(CCPHelper.DEMO_TAG, "[CallInActivity] com.wedge.voip on call released!!");
        try {
            if (callid != null && callid.equals(mCurrentCallId)) {
//                stopVoiceRecording(callid);
                finishCalling();
                // finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

                CallStatisticsInfo callStatistics = getDeviceHelper().getCallStatistics(mCallType);
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
}