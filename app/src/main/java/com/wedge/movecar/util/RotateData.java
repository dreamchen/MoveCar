package com.wedge.movecar.util;

public class RotateData {

	/** 
	 * 水平方向错切变换 
	 *  
	 * @param input - 输入像素数据 
	 * @param shear - 错切角度 
	 * @param width - 图像像素数据宽度 
	 * @param height - 图像像素数据高度 
	 * @return 
	 */  
	public static int[] xshear(int[] input, float shear, int width, int height) {  
	    int outw = (int)(Math.abs(shear) * height + width);  
	    int outh = height;  
	    int[] output = new int[height * outw];  
	      
	    // initialization - 初始化计算变量   
	    float skew = 0.0f;  
	    float skewi = 0.0f;  
	    float skewf = 0.0f;  
	    int index = 0;  
	    int outdex = 0;  
	    float leftred = 0.0f, leftgreen = 0.0f, leftblue = 0.0f;  
	    float oleftred = 0.0f, oleftgreen = 0.0f, oleftblue = 0.0f;  
	    int ta = 0, tr=0, tg = 0, tb = 0;  
	      
	    // 执行对每个像素的错切变换   
	    for(int row=0; row<height; row++) {  
	        // skew = shear * (height-1-row + 0.5f); big issue!! very difficulty to find it   
	        skew = shear * (row + 0.5f);  
	        skewi = (float)Math.floor(skew);  
	        skewf = skew - skewi;  
	        for(int col=0; col<width; col++) {  
	            index = row * width + col;  
	            ta = (input[index] >> 24) & 0xff;  
	               tr = (input[index] >> 16) & 0xff;  
	               tg = (input[index] >> 8) & 0xff;  
	               tb = input[index] & 0xff;  
	               if(tr == tg && tg == tb && tb == 0) {  
	                continue;  
	               }  
	               // calculate interpolation pixel value   
	            leftred = (skewf * tr);  
	            leftgreen = (skewf * tg);  
	            leftblue = (skewf * tb);  
	            // calculate the new pixel RGB value   
	            tr = (int)(tr - leftred + oleftred);  
	            tg = (int)(tg - leftgreen + oleftgreen);  
	            tb = (int)(tb - leftblue + oleftblue);  
	              
	            // fix issue, need to check boundary   
	            // computation the new pixel postion here!!   
	            outdex = (int)(row * outw + col + skewi);  
	            output[outdex] = (ta << 24) | (tr << 16) | (tg << 8) | tb;  
	              
	            // ready for next pixel.   
	            oleftred = leftred;  
	            oleftgreen = leftgreen;  
	            oleftblue = leftblue;  
	        }  
	    }  
	    return output;  
	}  
	
	public static  int[] yshear(int[] input, float shear, int width, int height)  {  
		int outh = (int)(shear * width + height);  
		int outw = width;  
	    int[] output = new int[outh * outw];  
	      
	    // initialization - 初始化计算变量   
	    float skew = 0.0f;  
	    float skewi = 0.0f;  
	    float skewf = 0.0f;  
	    int index = 0;  
	    int outdex = 0;  
	    float leftred = 0.0f, leftgreen = 0.0f, leftblue = 0.0f;  
	    float oleftred = 0.0f, oleftgreen = 0.0f, oleftblue = 0.0f;  
	    int ta = 0, tr=0, tg = 0, tb = 0;  
	      
	    for(int col = 0; col < width; col++) {  
	        // the trick is here!!, you can control the    
	        // anti-clockwise or clockwise   
	        skew = shear * (width-1-col + 0.5f);  
	        // skew = shear * (col + 0.5f);    
	        skewi = (float)Math.floor(skew);  
	        skewf = skew - skewi;  
	        for(int row = 0; row < height; row++) {  
	            index = row * width + col;  
	            ta = (input[index] >> 24) & 0xff;  
	               tr = (input[index] >> 16) & 0xff;  
	               tg = (input[index] >> 8) & 0xff;  
	               tb = input[index] & 0xff;  
	                 
	               // calculate interpolation pixel value   
	            leftred = (skewf * tr);  
	            leftgreen = (skewf * tg);  
	            leftblue = (skewf * tb);  
	            // calculate the new pixel RGB value   
	            tr = (int)(tr - leftred + oleftred);  
	            tg = (int)(tg - leftgreen + oleftgreen);  
	            tb = (int)(tb - leftblue + oleftblue);  
	              
	            // computation the new pixel postion here!!   
	            // outdex = (int)((height-row + skewi) * outw + col);   
	            outdex = (int)((row + skewi) * outw + col);  
	            output[outdex] = (ta << 24) | (tr << 16) | (tg << 8) | tb;  
	              
	            // ready for next pixel.   
	            oleftred = leftred;  
	            oleftgreen = leftgreen;  
	            oleftblue = leftblue;  
	        }  
	    }  
	    return output;  
	}  
}
