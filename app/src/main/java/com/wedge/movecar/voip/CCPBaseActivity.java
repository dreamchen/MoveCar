package com.wedge.movecar.voip;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.Device.CallType;
import com.hisun.phone.core.voice.DeviceListener;
import com.hisun.phone.core.voice.listener.OnVoIPListener;
import com.hisun.phone.core.voice.model.CloopenReason;
import com.hisun.phone.core.voice.util.Log4Util;
import com.wedge.movecar.R;

import java.lang.ref.WeakReference;

/**
 * Created by chenerlei on 15/3/9.
 */
public class CCPBaseActivity extends Activity implements OnVoIPListener.OnCallRecordListener {

    public static final int WHAT_ON_CODE_CALL_STATUS = 11;
    public static final int WHAT_ON_SHOW_LOCAL_SURFACEVIEW = 12;

    public static String mCurrentCallId;
    protected boolean isConnect = false;
    protected boolean isIncomingCall = false;
    protected boolean callRecordEnabled;
    // video
    protected CallType mCallType;

    public static ImageView mCallTransferBtn;

    // 静音按钮
    public ImageView mCallMute;
    // 免提按钮
    public ImageView mCallHandFree;
    // 是否静音
    public boolean isMute = false;
    // 是否免提
    public boolean isHandsfree = false;

    private BaseHandle mBaseHandle;

    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    private KeyguardManager mKeyguardManager = null;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseHandle = new BaseHandle(this);
        CCPHelper.getInstance().setHandler(mBaseHandle);
    }

    public void enterIncallMode() {
        if (!(mWakeLock.isHeld())) {
            // wake up screen
            // BUG java.lang.RuntimeException: WakeLock under-locked
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire();
        }
        mKeyguardLock = this.mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();
    }

    public void initProwerManager() {
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "CALL_ACTIVITY#" + super.getClass().getName());
        mKeyguardManager = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE));
    }

    public void releaseWakeLock() {
        try {
            if (this.mWakeLock.isHeld()) {
                if (this.mKeyguardLock != null) {
                    this.mKeyguardLock.reenableKeyguard();
                    this.mKeyguardLock = null;
                }
                this.mWakeLock.release();
            }
            return;
        } catch (Exception localException) {
            Log4Util.e("AndroidRuntime", localException.toString());
        }
    }


    /**
     * 直接拨打电话
     *
     * @param phoneNum
     */
    protected final void startCalling(String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    protected Device getDeviceHelper() {
        return CCPHelper.getInstance().getDevice();
    }

    /**
     *
     */
    protected boolean checkeDeviceHelper() {
        if (getDeviceHelper() == null) {
            return false;
        }
        return true;
    }

    public void showRequestErrorToast(CloopenReason reason) {
        if (reason != null && reason.isError()) {
            Toast.makeText(this, reason.getMessage() + "[" + reason.getReasonCode() + "]", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * <p>Title: doHandUpReleaseCall</p>
     * <p>Description: </p>
     */
    protected void doHandUpReleaseCall() {
    }


    public static final int WHAT_SHOW_PROGRESS = 0x101A;
    public static final int WHAT_CLOSE_PROGRESS = 0x101B;


    private ProgressDialog pVideoDialog = null;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_SHOW_PROGRESS) {
                pVideoDialog = new ProgressDialog(CCPBaseActivity.this);
                //pVideoDialog.setTitle(R.string.str_dialog_title);
//                pVideoDialog.setMessage(getString(R.string.str_dialog_message_default));
                pVideoDialog.setCanceledOnTouchOutside(false);
                String message = (String) msg.obj;
                if (!TextUtils.isEmpty(message))
                    pVideoDialog.setMessage(message);
                pVideoDialog.show();
                Log4Util.d(CCPHelper.DEMO_TAG, "dialog  show");
            } else if (msg.what == WHAT_CLOSE_PROGRESS) {
                if (pVideoDialog != null) {
                    pVideoDialog.dismiss();
                    pVideoDialog = null;
                }
                Log4Util.d(CCPHelper.DEMO_TAG, "dialog  dismiss");
            } else {
//                switch (msg.what) {
//                    case R.layout.ads_tops_view:
//                        if(msg.obj != null && msg.obj instanceof View) {
//                            removeNotificatoinView((View)msg.obj);
//                        }
//                        break;
//
//                    default:
//                        handleNotifyMessage(msg);
//                        break;
//                }
            }
        }

        ;
    };

    /**
     * 设置呼叫转接
     */
    public int setCallTransfer(String mCurrentCallId, String transToNumber) {
        if (checkeDeviceHelper()) {
            int transferCall = getDeviceHelper().transferCall(mCurrentCallId, transToNumber);
            if (transferCall == 0) {
                mCallMute.setImageResource(R.mipmap.call_interface_mute_on);
            } else {
                //呼叫转接失败
                Toast.makeText(getApplicationContext(), "转接失败 返回码" + transferCall, Toast.LENGTH_LONG).show();
            }
            return transferCall;
        }
        return -1;
    }

    /**
     * 设置静音
     */
    public void setMuteUI() {
        try {
            if (checkeDeviceHelper()) {
                getDeviceHelper().setMute(!isMute);
            }
            isMute = getDeviceHelper().getMuteStatus();

            if (isMute) {
                mCallMute.setImageResource(R.mipmap.call_interface_mute_on);
            } else {
                mCallMute.setImageResource(R.mipmap.call_interface_mute);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置免提
     */
    public void sethandfreeUI() {
        try {
            if (checkeDeviceHelper()) {
                getDeviceHelper().enableLoudsSpeaker(!isHandsfree);
                isHandsfree = getDeviceHelper().getLoudsSpeakerStatus();
            }

            if (isHandsfree) {
                mCallHandFree.setImageResource(R.mipmap.call_interface_hands_free_on);
            } else {
                mCallHandFree.setImageResource(R.mipmap.call_interface_hands_free);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    public void stopVoiceRecording(String callid) {
        if (getDeviceHelper() != null && callRecordEnabled) {
            getDeviceHelper().stopVoiceCallRecording(callid);
        }
    }

    @Override
    public void onCallRecordDone(String filePath) {
        Toast.makeText(getApplicationContext(), getString(R.string.str_call_record_success, filePath), Toast.LENGTH_LONG).show();
    }

    /**
     * The callback of calls recording throws an error, reporting events.
     *
     * @param reason recording status:
     *               0: Success
     *               -1: Recording failed then delete the recording file
     *               -2: Recording failed of write file but still to retain the record file.
     */
    @Override
    public void onCallRecordError(int reason) {
        switch (reason) {
            case -1:
                Toast.makeText(getApplicationContext(), getString(R.string.str_call_record_error_2, reason), Toast.LENGTH_LONG).show();
                break;
            case -2:
                Toast.makeText(getApplicationContext(), getString(R.string.str_call_record_error_1, reason), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), getString(R.string.str_call_record_error_0, reason), Toast.LENGTH_LONG).show();
                break;
        }
    }


    //    @SuppressLint("HandlerLeak")
    public BaseHandle getBaseHandle() {
        return mBaseHandle;
    }

    public void setBaseHandle(BaseHandle handler) {
        mBaseHandle = handler;
    }

    protected void onCallAlerting(String callid) {
    }

    protected void onCallAnswered(String callid) {
    }

    protected void onCallProceeding(String callid) {
    }

    protected void onCallReleased(String callid) {
    }

    @Deprecated
    protected void onCallVideoRatioChanged(String callid, String resolution) {
    }

    protected void onCallVideoRatioChanged(String callid, int width, int height) {
    }

    protected void onMakeCallFailed(String callid, DeviceListener.Reason reason) {
    }

    protected void onCallback(int status, String self, String dest) {
    }

    protected void handleNotifyMessage(Message msg) {
    }

    public static class BaseHandle extends Handler {
        WeakReference<CCPBaseActivity> mActivity;

        public BaseHandle(CCPBaseActivity activity) {
            mActivity = new WeakReference<CCPBaseActivity>(activity);
        }

        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CCPBaseActivity mBaseActivity = mActivity.get();

            if (mBaseActivity == null) {
                return;
            }
            String callid = null;
            DeviceListener.Reason reason = DeviceListener.Reason.UNKNOWN;
            Bundle b = null;
            // 获取通话ID
            if (msg.obj instanceof String) {
                callid = (String) msg.obj;
            } else if (msg.obj instanceof Bundle) {
                b = (Bundle) msg.obj;

                if (b.containsKey(Device.CALLID)) {
                    callid = b.getString(Device.CALLID);
                }

                if (b.containsKey(Device.REASON)) {
                    try {
                        reason = (DeviceListener.Reason) b.get(Device.REASON);
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), "b.get(Device.REASON)");
                    }
                }
            }
            switch (msg.what) {
                case CCPHelper.WHAT_ON_CALL_ALERTING:
                    mBaseActivity.onCallAlerting(callid);
                    break;
                case CCPHelper.WHAT_ON_CALL_PROCEEDING:
                    mBaseActivity.onCallProceeding(callid);
                    break;
                case CCPHelper.WHAT_ON_CALL_ANSWERED:
                    mBaseActivity.onCallAnswered(callid);
                    if (mCallTransferBtn != null)
                        mCallTransferBtn.setEnabled(true);
                    break;

                case CCPHelper.WHAT_ON_CALL_RELEASED:
                    mBaseActivity.onCallReleased(callid);
//				mCurrentCallId=null;
                    if (mCallTransferBtn != null)
                        mCallTransferBtn.setEnabled(false);
                    break;
                case CCPHelper.WHAT_ON_CALL_MAKECALL_FAILED:
                    mBaseActivity.onMakeCallFailed(callid, reason);
                    break;
                case WHAT_ON_CODE_CALL_STATUS:
                case CCPHelper.WHAT_ON_RECEIVE_SYSTEM_EVENTS:
                    mBaseActivity.handleNotifyMessage(msg);
                    break;
                case CCPHelper.WHAT_ON_CALLVIDEO_RATIO_CHANGED:
                    if (b.containsKey("width") && b.containsKey("height")) {
                        int width = b.getInt("width");
                        int height = b.getInt("height");
                        mBaseActivity.onCallVideoRatioChanged(callid, width, height);
                    }
                    break;
                case CCPHelper.WHAT_ON_CALL_BACKING:

                    if (b == null) {
                        return;
                    }
                    int status = -1;
                    if (b.containsKey(Device.CBSTATE)) {
                        status = b.getInt(Device.CBSTATE);
                    }
                    String self = "";
                    if (b.containsKey(Device.SELFPHONE)) {
                        self = b.getString(Device.SELFPHONE);
                    }
                    String dest = "";
                    if (b.containsKey(Device.DESTPHONE)) {
                        dest = b.getString(Device.DESTPHONE);
                    }
                    mBaseActivity.onCallback(status, self, dest);
                    break;
//                case CCPHelper.WHAT_ON_CALL_TRANSFERSTATESUCCEED:
//                    String callId = (String) msg.obj;
//                    if(mCurrentCallId.equals(callId)){
//                        Toast.makeText(mBaseActivity, "呼转成功！", 1).show();
//                    }
//                    break;

                default:
                    break;
            }
        }
    }
}