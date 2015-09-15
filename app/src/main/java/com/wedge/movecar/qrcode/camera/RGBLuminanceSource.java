/*
 * Copyright 2009 ZXing authors
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

package com.wedge.movecar.qrcode.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * This class is used to help decode images from files which arrive as RGB data
 * from an ARGB pixel array. It does not support rotation.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Betaminos
 */
public final class RGBLuminanceSource extends LuminanceSource {

	private final int[] luminances;
	private final int dataWidth;
	private final int dataHeight;
	private final int left;
	private final int top;

	public RGBLuminanceSource(int width, int height, int[] pixels) {
		super(width, height);
		luminances = pixels;
		dataWidth = width;
		dataHeight = height;
		left = 0;
		top = 0;

	}

	public RGBLuminanceSource(int[] pixels, int dataWidth, int dataHeight,
			int left, int top, int width, int height) {
		super(width, height);
		if (left + width > dataWidth || top + height > dataHeight) {
			throw new IllegalArgumentException(
					"Crop rectangle does not fit within image data.");
		}
		this.luminances = pixels;
		this.dataWidth = dataWidth;
		this.dataHeight = dataHeight;
		this.left = left;
		this.top = top;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException(
					"Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		int offset = (y + top) * dataWidth + left;
		System.arraycopy(luminances, offset, row, 0, width);
		return row;
	}

	public int[] renderCroppedData() {
		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];
		int[] yuv = luminances;
		int inputOffset = top * dataWidth + left;

		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[outputOffset + x] = yuv[inputOffset + x];
			}
			inputOffset += dataWidth;
		}

		// TODO Auto-generated method stub

		return pixels;
	}

	public Bitmap renderCroppedGreyscaleBitmap() {
		int width = getWidth();
		int height = getHeight();
		int[] pixels = renderCroppedData();

		Bitmap b = null;
		if (null != pixels) {
			// b = BitmapFactory.decodeByteArray(pixels, 0, pixels.length);//
			// data是字节数据，将其解析成位图
			// b = Bitmap.createBitmap(pixels, width, height, Config.RGB_565);
			b = Bitmap.createBitmap(pixels, width, height, Config.ARGB_8888);
		}

		if (b == null)
			return null;

		return b;
	}

	@Override
	public byte[] getMatrix() {

		return null;
	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

	@Override
	public LuminanceSource crop(int left, int top, int width, int height) {
		return new RGBLuminanceSource(luminances, dataWidth, dataHeight,
				this.left + left, this.top + top, width, height);
	}

}
