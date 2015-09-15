package com.wedge.movecar;

import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.wedge.movecar.qrcode.hrlprsdk.HRLprSdk;
import com.wedge.movecar.common.SharedPreUtil;

public class CarApplication extends Application {

	public static CarApplication instance;

	public String getAppStoreDirectory() {
		// TODO Auto-generated method stub
		return getSdCardAbsolutePath();
	}

	public static boolean isDirectoryInit = false;

	public static void initDirectory() {

		File file = new File(getStoreDirectory());// 创建文件夹
		if (!file.exists()) {
			file.mkdir();
		}

		isDirectoryInit = true;
	}

	public static String getSdCardAbsolutePath() {

		if (!isDirectoryInit)
			initDirectory();

		return getStoreDirectory();
		// return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	private static String getStoreDirectory() {

		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" +"车牌识别" + "/";

	}

	HRLprSdk mLprSdk;

	public static CarApplication getInstance() {
		return (CarApplication) instance;

	}

	// ================================================

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
//		CrashHandler.getInstance().init(this);
		//

		SharedPreUtil.initSharedPreference(this);
		initCar();

	}

	private void initCar() {
		mLprSdk = new HRLprSdk();
		// 初始化，注意（整个程序只需要初始化一次即可）
		int bInit = mLprSdk.HRLprInit();
		System.out.println("bInit:" + bInit);

		// 设置识别优先省份（可选）
		int bRet = mLprSdk.HRLprSetProvince("京");
		System.out.println("bRet:" + bRet);
		// 限定区域
//		bRet = mLprSdk.HRLprSetRecogRoi(50, 50 , 50, 70, 0);
//		System.out.println("bRet:" + bRet);
		// 设置识别区域（全图识别可不设置）
		bRet = mLprSdk.HRLprSetCredit(920);
		System.out.println("bRet:" + bRet);
	}
	
//	private int error =  0 ; 

	public String HRLCar(String path) {
		String carnum = mLprSdk.HRLprProcEx(path);
		System.out.println("识别结果:" + carnum);
		// showToastLong("识别结果:" + carnum);
//		error++;
//		if(error>10){
//			onFree();
//			initCar();
//			error = 0;
//		}
		
		return carnum;
	}

	public String HRLCar(byte[] pImgBuffer, int m_lImgWidth, int m_lImgHeight) {
		String carnum = mLprSdk.HRIVLprProcVideo(pImgBuffer, m_lImgWidth,
				m_lImgHeight);
		System.out.println("识别结果:" + carnum);
		// showToastLong("识别结果:" + carnum);
		return carnum;
	}

	public void onFree() {
		// 释放车牌识别,注：整个程序只需要释放一次即可）
		if (mLprSdk != null)
			mLprSdk.HRLprFree();
	}

	// ===========================提示信息=====================================

	static Toast toast;

	static TextView tv = null;

	public static void showToast(Activity a, String str) {
		// Toast.makeText(a, str, Toast.LENGTH_SHORT).show();

		if (str == null)
			return;

		if (toast == null) {
			// View toastRoot = a.getLayoutInflater().inflate(R.layout.my_toast,
			// null);
			toast = new Toast(a.getApplicationContext());
			tv = new TextView(a.getApplicationContext());
			toast.setView(tv);
			toast.setGravity(Gravity.CENTER, 0, 200);
			tv.setTextColor(0xff9f7344);
			// tv.setBackgroundResource(R.drawable.aa_searchbox);
			tv.setPadding(10, 10, 10, 10);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(18);
			// tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
			tv.setText(str.trim());
			tv.requestLayout();
		} else {
			tv.setText(str.trim());
			tv.requestLayout();
		}
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showToastLong(Activity a, String str) {
		// Toast.makeText(a, str, Toast.LENGTH_LONG).show();

		if (str == null)
			return;

		if (toast == null) {
			// View toastRoot = a.getLayoutInflater().inflate(R.layout.my_toast,
			// null);
			toast = new Toast(a.getApplicationContext());
			tv = new TextView(a.getApplicationContext());
			toast.setView(tv);
			toast.setGravity(Gravity.CENTER, 0, 200);
			tv.setTextColor(0xff9f7344);
			// tv.setBackgroundResource(R.drawable.aa_searchbox);
			tv.setPadding(10, 10, 10, 10);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(18);
			// tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
			tv.setText(str.trim());
			tv.requestLayout();
		} else {
			tv.setText(str.trim());
			tv.requestLayout();
		}
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}

	public static Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case 0:
				Toast.makeText(getInstance(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getInstance(), (String) msg.obj,
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}

			return false;
		}
	});

	public static void showToast(String str) {
		// Toast.makeText(ctx.getApplicationContext(), str, Toast.LENGTH_SHORT)
		// .show();
		Message msg = new Message();
		msg.what = 0;
		msg.obj = str;
		handler.sendMessage(msg);
	}

	public static void showToastLong(String str) {
		Message msg = new Message();
		msg.what = 1;
		msg.obj = str;
		handler.sendMessage(msg);
		// Toast.makeText(ctx.getApplicationContext(), str, Toast.LENGTH_LONG)
		// .show();
	}

}
