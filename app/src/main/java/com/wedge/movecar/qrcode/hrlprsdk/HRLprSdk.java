package com.wedge.movecar.qrcode.hrlprsdk;

public class HRLprSdk {

	/*函 数 名：HRLprInit
	说    明：车牌识别初始化
	参数输出：无
	返 回 值：  
	-1  初始化失败
	1    初始化成功
	修改说明：
	*/
	public static native int HRLprInit();
	/*函 数  HRLprSetRecogRoi
	说    明：设置感兴趣区域
	参数输入：
		lTopLeftX，感兴趣区域矩形左上角点x坐标
		lTopLeftY，感兴趣区域矩形左上角点y坐标
		lBotRightX，感兴趣区域矩形右下角点x坐标
		lBotRightY，感兴趣区域矩形右下角点y坐标
		mode，感兴趣区域设置模式,默认0-设置识别区域，1-设置定位区域
	参数输出：无
	返 回 值：设置成功：1，失败：-1
	修改说明：
	*/
	public static native int HRLprSetRecogRoi(int lTopLeftX, int lTopLeftY, int lBotRightX, int lBotRightY, int mode);
	/*函 数 名：HRLprFree
	说    明：释放车牌识别
	参数输入：无
	参数输出：无
	返 回 值：
	修改说明：
	*/
	public static native void HRLprFree();
	/*函 数 名：HRLprSetCredit
	说    明：设置置信度
	参数输入：m_lCredit， 置信度0~1000
	参数输出：无
	返 回 值：成功：1，失败：-1
	修改说明：
	*/
	public static native int  HRLprSetCredit(int m_lCredit);
	/*函 数 名：HRLprSetProvince
	说    明：设置优先识别省份
	参数输入：cProvince， String cProvine = "京"
	参数输出：无
	返 回 值：成功：1，失败：-1
	修改说明：
	*/
	public static native int  HRLprSetProvince(String cProvince);
	/*函 数 HRLprProcEx
	说    明：基于图片路径的车牌识别处理函数
	参数输入：
		filePath, 图片的绝对路径, 支持JPEG和BMP两种数据格式
	参数输出：
	返 回 值：String， 有车牌: 颜色（2字节）+ 车牌结果（10字节） + 置信度（4), 遇到空位符号补'*'，全部为'*'表示未识别到车牌
	                               无车牌：null
	修改说明：
	*/
	public static native String HRLprProcEx(String filePath);
	
	/*函 数 名：HRIVLprProc
	说    明：基于视频的车牌识别处理函数
	参数输入：
		pImgBuffer，   RGB24,图像内存，排列形式BGRBGRBGR...
		m_lImgWidth， 图像宽度
		m_lImgHeight，图像高度
	参数输出：long
		pResult，     识别结果，参照 HRIVLpr_Result
	 返 回 值：成功：XX(颜色)XXXXXXXXXX(车牌结果)XXXX(可信度)，失败：null
	修改说明：
	*/
	public static native String HRIVLprProcVideo(byte[] pImgBuffer, int m_lImgWidth, int m_lImgHeight);
	
	static{
		System.loadLibrary("HRLprSdk");
	}
}
