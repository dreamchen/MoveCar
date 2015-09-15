package com.wedge.movecar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class ElonUitl {
	
	
	
	/**
	 * 旋转90度
	 * @param src
	 * @param des
	 * @param width
	 * @param height
	 */
	public static void rotateYUV240SP(byte[] src,byte[] des,int width,int height)  
    {  
         
        int wh = width * height;  
        //旋转Y  
        int k = 0;  
        for(int i=0;i<width;i++) {    
                 for(int j=0;j<height;j++)     
                 {    
                            des[k] = src[width*j + i];                
                            k++;    
                 }    
         }    
        
         for(int i=0;i<width/2;i++) {    
                 for(int j=0;j<height/2;j++)     
                 {       
                   des[k] = src[wh+ width/2*j + i];        
                   des[k+width*height/4]=src[wh*5/4 + width/2*j + i];    
                   k++;    
                 }    
              }           
          
    }  
	
	private static int convertYUVtoRGB(int y, int u, int v) {
		int r, g, b;

		r = y + (int) 1.402f * v;
		g = y - (int) (0.344f * u + 0.714f * v);
		b = y + (int) 1.772f * u;
		r = r > 255 ? 255 : r < 0 ? 0 : r;
		g = g > 255 ? 255 : g < 0 ? 0 : g;
		b = b > 255 ? 255 : b < 0 ? 0 : b;
		return 0xff000000 | (b << 16) | (g << 8) | r;
	}

	static public Bitmap decodeYUV420SP4(byte[] data, int width, int height,
			int[] rgba, BitmapFactory.Options opts) {
		Bitmap bmp = null;
		final int frameSize = width * height;

		int size = width * height;
		int offset = size;
		// int[] pixels = new int[size];
		int u, v, y1, y2, y3, y4;

		// i percorre os Y and the final pixels
		// k percorre os pixles U e V
		for (int i = 0, k = 0; i < size; i += 2, k += 2) {
			y1 = data[i] & 0xff;
			y2 = data[i + 1] & 0xff;
			y3 = data[width + i] & 0xff;
			y4 = data[width + i + 1] & 0xff;

			u = data[offset + k] & 0xff;
			v = data[offset + k + 1] & 0xff;
			u = u - 128;
			v = v - 128;

			rgba[i] = convertYUVtoRGB(y1, u, v);
			rgba[i + 1] = convertYUVtoRGB(y2, u, v);
			rgba[width + i] = convertYUVtoRGB(y3, u, v);
			rgba[width + i + 1] = convertYUVtoRGB(y4, u, v);

			if (i != 0 && (i + 2) % width == 0)
				i += width;
		}

		bmp = Bitmap.createBitmap(rgba, width, height, Bitmap.Config.ARGB_8888);

		return bmp;
	}

	
	//============================================================
	
	/**
	 * Converts semi-planar YUV420 as generated for camera preview into RGB565
	 * format for use as an OpenGL ES texture. It assumes that both the input
	 * and output data are contiguous and start at zero.
	 * 
	 * @param yuvs the array of YUV420 semi-planar data
	 * @param rgbs an array into which the RGB565 data will be written
	 * @param width the number of pixels horizontally
	 * @param height the number of pixels vertically
	 */

	//we tackle the conversion two pixels at a time for greater speed
	private void toRGB565(byte[] yuvs, int width, int height, byte[] rgbs) {
	    //the end of the luminance data
	    final int lumEnd = width * height;
	    //points to the next luminance value pair
	    int lumPtr = 0;
	    //points to the next chromiance value pair
	    int chrPtr = lumEnd;
	    //points to the next byte output pair of RGB565 value
	    int outPtr = 0;
	    //the end of the current luminance scanline
	    int lineEnd = width;

	    while (true) {

	        //skip back to the start of the chromiance values when necessary
	        if (lumPtr == lineEnd) {
	            if (lumPtr == lumEnd) break; //we've reached the end
	            //division here is a bit expensive, but's only done once per scanline
	            chrPtr = lumEnd + ((lumPtr  >> 1) / width) * width;
	            lineEnd += width;
	        }

	        //read the luminance and chromiance values
	        final int Y1 = yuvs[lumPtr++] & 0xff; 
	        final int Y2 = yuvs[lumPtr++] & 0xff; 
	        final int Cr = (yuvs[chrPtr++] & 0xff) - 128; 
	        final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
	        int R, G, B;

	        //generate first RGB components
	        B = Y1 + ((454 * Cb) >> 8);
	        if(B < 0) B = 0; else if(B > 255) B = 255; 
	        G = Y1 - ((88 * Cb + 183 * Cr) >> 8); 
	        if(G < 0) G = 0; else if(G > 255) G = 255; 
	        R = Y1 + ((359 * Cr) >> 8); 
	        if(R < 0) R = 0; else if(R > 255) R = 255; 
	        //NOTE: this assume little-endian encoding
	        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
	        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));

	        //generate second RGB components
	        B = Y2 + ((454 * Cb) >> 8);
	        if(B < 0) B = 0; else if(B > 255) B = 255; 
	        G = Y2 - ((88 * Cb + 183 * Cr) >> 8); 
	        if(G < 0) G = 0; else if(G > 255) G = 255; 
	        R = Y2 + ((359 * Cr) >> 8); 
	        if(R < 0) R = 0; else if(R > 255) R = 255; 
	        //NOTE: this assume little-endian encoding
	        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
	        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));
	    }
	}
	
	/*
	 * 像素数组转化为RGB数组
	 */
	public static byte[] convertColorToByte(int color[]) {
		if (color == null) {
			return null;
		}
		//ARGB
		byte[] data = new byte[color.length * 3];
		for (int i = 0; i < color.length; i++) {
//			data[i * 3] = (byte) (color[i] >> 16 & 0xff);
//			data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
//			data[i * 3 + 2] = (byte) (color[i] & 0xff);
			
			data[i * 3] =  (byte) (color[i] & 0xff);
			data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
			data[i * 3 + 2] =(byte) (color[i] >> 16 & 0xff);
		}

		return data;

	}
	
	public static int[] convertColorToInt(byte color[]) {
		if (color == null) {
			return null;
		}
		if (color.length%3 != 0)
			throw new NullPointerException("buffer 'rgbBuf' is no 3");

		int[] data = new int[color.length / 3];
		for (int i = 0; i < data.length; i++) {
			data[i ] =  0xff000000 | (color[i*3] << 16)& 0xff0000 | (color[i*3+1] << 8)& 0xff00 | color[i*3+2]& 0xff;
			
		}

		return data;

	}
	
//	public static byte[] Bitmap2Bytes(Bitmap bm) {
//		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		         bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//		        return baos.toByteArray();
//		    }
	
//	Bitmap转换成Drawable
	// 1 Bitmap bm=xxx; //xxx根据你的情况获取
	// 2 BitmapDrawable bd= new BitmapDrawable(getResource(), bm);
	// 3 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
	

	

	/**
	 * yuv数据转rgb数据
	 * 
	 * 
	 * @param rgbBuf
	 * @param yuv420sp
	 * @param width
	 * @param height
	 */
	private static void decodeYUV420SP(byte[] rgbBuf, byte[] yuv420sp,
			int width, int height) {
		final int frameSize = width * height;
		if (rgbBuf == null)
			throw new NullPointerException("buffer 'rgbBuf' is null");
		if (rgbBuf.length < frameSize * 3)
			throw new IllegalArgumentException("buffer 'rgbBuf' size "
					+ rgbBuf.length + " < minimum " + frameSize * 3);

		if (yuv420sp == null)
			throw new NullPointerException("buffer 'yuv420sp' is null");

		if (yuv420sp.length < frameSize * 3 / 2)
			throw new IllegalArgumentException("buffer 'yuv420sp' size "
					+ yuv420sp.length + " < minimum " + frameSize * 3 / 2);

		int i = 0, y = 0;
		int uvp = 0, u = 0, v = 0;
		int y1192 = 0, r = 0, g = 0, b = 0;

		for (int j = 0, yp = 0; j < height; j++) {
			uvp = frameSize + (j >> 1) * width;
			u = 0;
			v = 0;
			for (i = 0; i < width; i++, yp++) {
				y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				y1192 = 1192 * y;
				r = (y1192 + 1634 * v);
				g = (y1192 - 833 * v - 400 * u);
				b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgbBuf[yp * 3] = (byte) (r >> 10);
				rgbBuf[yp * 3 + 1] = (byte) (g >> 10);
				rgbBuf[yp * 3 + 2] = (byte) (b >> 10);
			}
		}
	}
	
	public static void decodeYUV420SPrgb565(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}
	
	public static void decodeYUV420SPrgb888(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);

				
				
//				byte b_r = (byte) (r >> 10);
//				byte b_g = (byte) (g >> 10);
//				byte b_b = (byte) (b >> 10);
//				
//				rgb[yp]  =  0xff000000 | (b_r << 16)& 0xff0000 | (b_g << 8)& 0xff00 | b_b& 0xff;
				
				//				rgb[yp]  =  0xff000000 | (b << 16)& 0xff0000 | (g << 8)& 0xff00 | r& 0xff;
//				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
//						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}
}
