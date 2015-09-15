/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wedge.movecar.qrcode.decoding;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.wedge.movecar.activity.CaptureActivity;
import com.wedge.movecar.qrcode.camera.CameraManager;
import com.wedge.movecar.qrcode.camera.RGBLuminanceSource;
import com.wedge.movecar.CarApplication;
import com.wedge.movecar.R;
import com.wedge.movecar.util.ElonUitl;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final CaptureActivity activity;

	// private final MultiFormatReader multiFormatReader;

	DecodeHandler(CaptureActivity activity,
			Hashtable<DecodeHintType, Object> hints) {
		// multiFormatReader = new MultiFormatReader();
		// multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.decode:
			Log.d(TAG, "Got decode message");
			decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			Looper.myLooper().quit();
			break;
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();

		// =============YUV数据直接显示图像代码 开始===================

		// YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height,
		// null);
		// if (yuv != null) {
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// yuv.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
		// bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0,
		// stream.size());
		//
		// }

		// =============YUV数据直接显示图像代码 结束===================
		// =============原始代码开始===================
		//
		// // modify here 数据在这里修饰
		// // 【旋转90度】
		// int[] rotatedData = new int[data.length];
		// for (int y = 0; y < height; y++) {
		// for (int x = 0; x < width; x++)
		// rotatedData[x * height + height - y - 1] = rgb[x + y * width];
		// }
		// //【旋转90度】
		//
		// int tmp = width; // Here we are swapping, that's the difference to
		// #11
		// width = height;
		// height = tmp;
		//
		//

		// 【原始】
		// BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		// try {
		// rawResult = multiFormatReader.decodeWithState(bitmap);
		// } catch (ReaderException re) {
		// // continue
		// } finally {
		// multiFormatReader.reset();
		// }
		// 【原始结束】
		// =============原始代码结束===================

		// ===================新思路========================================
		// 【1】 先转换RGB8888代码 OK
		int rgb8888[] = new int[width * height];
		ElonUitl.decodeYUV420SPrgb888(rgb8888, data, width, height);
		// 【2】旋转90度 OK
		int[] rotatedData = new int[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = rgb8888[x + y
						* width];
		}
		// 【3】转换宽高
		int tmp = width; // Here we are swapping, that's the difference to
		width = height;
		height = tmp;
		// 【4】裁剪区域
		RGBLuminanceSource source = CameraManager.get()
				.buildRGBLuminanceSource(rotatedData, width, height);

		Bitmap bitmap = source.renderCroppedGreyscaleBitmap();

		if (bitmap == null)
			return;
		// 【5】获取裁剪数据
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		//【DEBUG-1】RGB8888转为RGB888
		byte[] d = ElonUitl.convertColorToByte(source.renderCroppedData());
		//【DEBUG-2】车牌识别
		String carmum = CarApplication.getInstance().HRLCar(d, w, h);
//		
//		StringBuffer sb = new StringBuffer();
//		
//		for(int i=-1;++i<d.length;){
//			sb.append((d[i]&0xff)+" ");
//		}
//		
//		//临时存储为txt
//		String savePathtxt = CarApplication.getInstance().getAppStoreDirectory()
//						+ carmum + ".txt";
//		FileUtil.saveFile(sb.toString().getBytes(), savePathtxt);
				
//		//【DEBUG-3】RGB888转为RGB8888
//		int out[] = ElonUitl.convertColorToInt(d);
//		//【DEBUG-4】重新生成图片 并显示
//		bitmap = Bitmap.createBitmap(out, w, h, Config.ARGB_8888);

		// 处理识别结果

		if (carmum.length() > 0 && !carmum.startsWith("*")) {
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + carmum);
			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_succeeded, carmum);
			Bundle bundle = new Bundle();
//			【DEBUG-5】
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, bitmap);
			message.setData(bundle);
			Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();
		} else {
			Message message = Message.obtain(activity.getHandler(),
					R.id.decode_failed);
			message.sendToTarget();
		}
	}

}
