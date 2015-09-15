package com.wedge.movecar.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.util.Log4Util;
import com.wedge.movecar.CarApplication;
import com.wedge.movecar.R;
import com.wedge.movecar.common.CustomRequest;
import com.wedge.movecar.common.SharedPreUtil;
import com.wedge.movecar.entity.UserEntity;
import com.wedge.movecar.qrcode.camera.CameraManager;
import com.wedge.movecar.qrcode.decoding.BarcodeFormat;
import com.wedge.movecar.qrcode.decoding.CaptureActivityHandler;
import com.wedge.movecar.qrcode.decoding.InactivityTimer;
import com.wedge.movecar.qrcode.view.ViewMaskView;
import com.wedge.movecar.qrcode.view.ViewfinderView;
import com.wedge.movecar.util.BitmapUtil;
import com.wedge.movecar.util.DialogManage;
import com.wedge.movecar.util.FileUtil;
import com.wedge.movecar.util.TimeUtil;
import com.wedge.movecar.voip.CCPHelper;
import com.wedge.movecar.voip.CallOutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author yeungeek
 * @ClassName: CaptureActivity
 * @Description: TODO
 * @date 2013-4-28 下午12:59:44
 */
public class CaptureActivity extends Activity implements Callback {

    private RequestQueue mRequestQueue;
    private UserEntity selfEntity;
    private static String mCurrentCallId;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private ViewMaskView viewMaskView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.qr_code_scan);
        setContentView(R.layout.main);
        mRequestQueue = Volley.newRequestQueue(this);
        CameraManager.init(getApplication());
        initControl();

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    public void onBackPressed() {
        // super.onBackPressed();
        exit();
    }

    public void exit() {
        DialogManage.showAlertDialog(CaptureActivity.this, "确定退出吗？", "提示",
                "取消", null, "确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        onDestroy();
                        finish();
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                        System.exit(0);
                    }
                });
    }

    TextView showtext;
    private int count = 10;

    private void initControl() {

        TextView tvCode = (TextView) findViewById(R.id.tvCode);
        Object obj = SharedPreUtil.getInstance().get("userEntity");
        if (obj != null) {
            selfEntity = (UserEntity) obj;
            tvCode.setText(selfEntity.getRealName());

            Log.d("TAG---", CCPHelper.getInstance().getIsInit() + "");
            if (!CCPHelper.getInstance().getIsInit()) {
                getAppInfo();
            }

            Log.d("TAG+++", CCPHelper.getInstance().getIsInit() + "");
        }


        count = 10;
//        showtext = (TextView) findViewById(R.id.count);
//        showtext.setText("剩余识别次数:" + count);

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewMaskView = (ViewMaskView) findViewById(R.id.viewMask);
        // findViewById(R.id.get).setOnClickListener(new View.OnClickListener()
        // {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // handler.sendEmptyMessage(R.id.restart_preview);
        // }
        // });

        findViewById(R.id.btnReTake).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraManager.get().startPreview();
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        });

        findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraManager.get().stopPreview();
                if (selfEntity == null) {
                    Intent intent = new Intent(CaptureActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                TextView tvFirst = (TextView) findViewById(R.id.tvFirstCode);
                EditText edtLast = (EditText) findViewById(R.id.edtLastCode);
                final String carCode = tvFirst.getText().toString() + edtLast.getText().toString();
//                String carCode = edtLast.getText().toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("toName", carCode);

                Log.d("TAG::", carCode);
                CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, "http://123.56.44.249:8080/CallCar/module/yuntongxun/getUserByVO", params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG+", response.toString());
                                try {
                                    if ("success".equals(response.getString("status"))) {
                                        JSONObject jsonObj = response.getJSONObject("items");
                                        UserEntity userEntity = new UserEntity();
                                        userEntity.setId(jsonObj.getLong("id"));
                                        userEntity.setName(jsonObj.getString("name"));
                                        userEntity.setRealName(jsonObj.getString("realName"));
                                        userEntity.setPassword(jsonObj.getString("password"));
                                        userEntity.setMobileNumber(jsonObj.getString("mobileNumber"));
                                        userEntity.setAppId(jsonObj.getString("appId"));
                                        userEntity.setSubSid(jsonObj.getString("subSid"));
                                        userEntity.setSubToken(jsonObj.getString("subToken"));
                                        userEntity.setVoipAccount(jsonObj.getString("voipAccount"));
                                        userEntity.setVoipPwd(jsonObj.getString("voipPwd"));
                                        voipTalk(userEntity.getVoipAccount(), userEntity.getRealName());
                                    } else {
                                        DialogManage.showAlertDialog(CaptureActivity.this, "该用户尚未注册，无法进行呼叫", "提示",
                                                "取消", null, "确定", new OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // TODO Auto-generated method stub
                                                    }
                                                });
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogManage.showAlertDialog(CaptureActivity.this, error.getMessage(), "提示",
                                "取消", null, "确定", new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }
                                });
                        Log.e("TAG-", error.getMessage(), error);
                    }
                });

                mRequestQueue.add(jsonObjectRequest);
            }
        });

        ToggleButton tgb = (ToggleButton)findViewById(R.id.toggleButton);
        tgb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CameraManager.get().stopPreview();
                    viewMaskView.showMask();
                    findViewById(R.id.btnReTake).setEnabled(false);
                    findViewById(R.id.btnCall).setEnabled(false);
                    findViewById(R.id.edtLastCode).setEnabled(false);
                    findViewById(R.id.tvFirstCode).setEnabled(false);
                    findViewById(R.id.tvCodeTitle).setEnabled(false);
                    findViewById(R.id.tvCode).setEnabled(false);
                    findViewById(R.id.textView).setEnabled(false);
                    findViewById(R.id.textView2).setEnabled(false);
                }else{
                    CameraManager.get().startPreview();
                    viewMaskView.closeMask();;
                    findViewById(R.id.btnReTake).setEnabled(true);
                    findViewById(R.id.btnCall).setEnabled(true);
                    findViewById(R.id.edtLastCode).setEnabled(true);
                    findViewById(R.id.tvFirstCode).setEnabled(true);
                    findViewById(R.id.tvCodeTitle).setEnabled(true);
                    findViewById(R.id.tvCode).setEnabled(true);
                    findViewById(R.id.textView).setEnabled(true);
                    findViewById(R.id.textView2).setEnabled(true);
                    drawViewfinder();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        CarApplication.getInstance().onFree();
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(String string, final Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Log.e("识别信息", string);
        //#前的数据
        String savename = string;
        if (savename.indexOf("#") > -1) {
            savename = string.substring(0, string.indexOf("#"));
        }
        savename = savename + "_" + TimeUtil.createdSaveTimeDate();
        // 【保存图片】
        String savePath = CarApplication.getInstance().getAppStoreDirectory()
                + savename + ".jpg";
        FileUtil.saveFile(BitmapUtil.compressImageByte(barcode), savePath);

        // 【展示】
        ImageView show = new ImageView(this);
        show.setImageBitmap(barcode);
        String code = string.split("\\#\\#")[0];
        String persent = string.split("\\#\\#")[1];

//        if(Integer.parseInt(persent)<920){
//            handler.sendEmptyMessage(R.id.restart_preview);
//            return ;
//        }

        TextView tvFirst = (TextView) findViewById(R.id.tvFirstCode);
        EditText edtLast = (EditText) findViewById(R.id.edtLastCode);

        tvFirst.setText(code.substring(1, 2));
        edtLast.setText(code.substring(2, code.length()));

        CameraManager.get().stopPreview();

//        DialogManage.showAlert(this, string, show, "关闭软件", "继续",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        exit();
//                    }
//                }, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        handler.sendEmptyMessage(R.id.restart_preview);
//
//                        count--;
//                        showtext.setText("剩余识别次数:" + count);
//
//                        if (count < 0) {
//                            showtext.setText("剩余识别次数已完！");
//                        }
//
//                    }
//                });

        // String savePath = CarApplication.getInstance()
        // .getAppStoreDirectory()
        // + TimeUtil.createdSaveTimeDate()
        // + ".jpg";
        // // 存储
        // FileUtil.saveFile(
        // BitmapUtil.compressImageByte(result),
        // savePath);
        //
        // CarApplication.getInstance().HRLCar(savePath);

        // final String resultString = result.getText();
        // Intent resultIntent = new Intent();
        // Bundle bundle = new Bundle();
        // bundle.putString("result", resultString);
        // resultIntent.putExtras(bundle);
        // this.setResult(RESULT_OK, resultIntent);

        // finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {

            CameraManager.get().openDriver(surfaceHolder);

            Rect frame = CameraManager.get().getFramingRect();
            if(frame == null){
                return ;
            }

            final ImageView light = (ImageView) findViewById(R.id.light);
            light.setX(frame.left + 20);
            light.setY(frame.top + 30);

            light.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (CameraManager.get().isLight()) {
                        CameraManager.get().closeLight();
                        light.setImageResource(R.mipmap.icon_base_up_58);
                    } else {
                        CameraManager.get().openLight();
                        light.setImageResource(R.mipmap.icon_base_down_58);
                    }

                }
            });

            // CameraManager.get().openLight(); //开闪光灯
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 扫描正确后的震动声音,如果感觉apk大了,可以删除
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * CCP 免费电话
     *
     * @param parm
     */
    public void voipTalk(String phoneNumber, String nickName) {
        if (selfEntity == null) {
            Log.d("CCP:", "userEntity is null");
        }
        Log.d("CCP:", nickName + "--" + phoneNumber);
        // 设置主叫手机号(被叫界面显示)
        CCPHelper.getInstance().getDevice().setSelfPhoneNumber(selfEntity.getVoipAccount());
        // 设置主叫昵称(被叫界面显示)
        CCPHelper.getInstance().getDevice().setSelfName(selfEntity.getRealName());

        Log.d("CCP:", selfEntity.getRealName() + "~~" + selfEntity.getVoipAccount());
        // 发起一次VoIP免费通话
        // 参数为通话类型和对方的VoIP帐号
        mCurrentCallId = CCPHelper.getInstance().getDevice().makeCall(Device.CallType.VOICE, phoneNumber);
        Log4Util.d("EUExCCP", mCurrentCallId);

        // 设置当呼入请求到达时, 唤起的界面
        // 此处设置当接收到呼入连接时,启动CallInActivity(替换自己创建的呼入Activity)界面
        Intent intent = new Intent(this, CallOutActivity.class);
        intent.putExtra("mNickName", nickName);
        intent.putExtra(CCPHelper.VALUE_DIAL_VOIP_INPUT, phoneNumber);
        intent.putExtra(CCPHelper.VALUE_DIAL_MODE,
                CCPHelper.VALUE_DIAL_MODE_FREE);
        intent.putExtra("mCurrentCallId", mCurrentCallId);
        startActivity(intent);
    }


    public void getAppInfo() {

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest("http://123.56.44.249:8080/CallCar/module/yuntongxun/getAppInfo?appId=" + selfEntity.getAppId(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG+", response.toString());
                        try {
                            if ("success".equals(response.getString("status"))) {
                                JSONObject jsonObj = response.getJSONObject("items");
                                String ip = jsonObj.getString("restUrl");
                                String port = jsonObj.getString("restPort");
                                initCCP(ip, port);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG-", error.getMessage(), error);
            }
        });
        mRequestQueue.add(jsonObjectRequest2);
    }

    /**
     * CCP 初始化
     *
     * @param parm
     */
    public void initCCP(String ip, String port) {
        Log.d("EUExCCP", "init is begin");
        CCPHelper.getInstance().initSDK(ip, port, selfEntity.getVoipAccount(), selfEntity.getVoipPwd(), selfEntity.getSubSid(), selfEntity.getSubToken());
        Log.d("EUExCCP", "parm is loaded!");
        Log.d("EUExCCP", "init");
    }
}
