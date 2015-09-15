package com.wedge.movecar.voip;

/**
 * Created by chenerlei on 15/2/6.
 */
public class EUExCCP {//implements CCPHelper.RegistCallBack {

    private static String mCurrentCallId;

    private final static String FUNC_INITCCP_CALLBACK = "uexCCP.cbInitCCP";
    private final static String FUNC_VOIPTALK_CALLBACK = "uexCCP.cbVoipTalk";
    private final static String FUNC_DIRECTTALK_CALLBACK = "uexCCP.cbDirectTalk";
    private final static String FUNC_BACKTALK_CALLBACK = "uexCCP.cbBackTalk";
//
//    public EUExCCP(Context context, EBrowserView eBrowserView) {
//        super(context, eBrowserView);
//        CCPHelper.getInstance(mContext).setRegistCallback(this);
//    }
//
//    @Override
//    protected boolean clean() {
//        return false;
//    }
//
//
//    /**
//     * CCP 初始化
//     *
//     * @param parm
//     */
//    public void initCCP(String[] parm) {
//        Log.d("EUExCCP", "init is begin");
//        if (parm.length < 5) {
//            Toast.makeText(mContext, "CCP初始化参数有误", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
//
//        CCPHelper.getInstance(mContext).initSDK(parm[0], parm[1], parm[2], parm[3], parm[4], parm[5]);
//        Log4Util.d("EUExCCP", "parm is loaded!");
//        Log4Util.d("EUExCCP", "init");
//    }
//
//    /**
//     * CCP 免费电话
//     *
//     * @param parm
//     */
//    public void voipTalk(String[] parm) {
//        Log4Util.d("EUExCCP", "voipTalk begin");
//        if (parm.length < 4) {
//            Toast.makeText(mContext, "CCP免费电话参数有误", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
//        String phoneNumber = parm[0];
//        String nickName = parm[1];
//        // 设置主叫手机号(被叫界面显示)
//        CCPHelper.getInstance(mContext).getDevice().setSelfPhoneNumber(parm[2]);
//        // 设置主叫昵称(被叫界面显示)
//        CCPHelper.getInstance(mContext).getDevice().setSelfName(parm[3]);
//
//        // 发起一次VoIP免费通话
//        // 参数为通话类型和对方的VoIP帐号
//        mCurrentCallId = CCPHelper.getInstance(mContext).getDevice().makeCall(CallType.VOICE, phoneNumber);
//        Log4Util.d("EUExCCP", mCurrentCallId);
//        openActivity(CCPHelper.VALUE_DIAL_MODE_FREE, nickName, phoneNumber, mCurrentCallId);
//    }
//
//    /**
//     * CCP 直拨电话
//     *
//     * @param parm
//     */
//    public void direcTalk(String[] parm) {
//        Log4Util.d("EUExCCP", "directalk begin");
//        if (parm.length < 2) {
//            Toast.makeText(mContext, "CCP直拨电话参数有误", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
//        String phoneNumber = parm[0];
//        String nickName = parm[1];
//        // 发起一次直拨通话
//        // 参数为通话类型和对方的电话号码
//        mCurrentCallId = CCPHelper.getInstance(mContext).getDevice().makeCall(CallType.VOICE, VoiceUtil.getStandardMDN(phoneNumber));
//        openActivity(CCPHelper.VALUE_DIAL_MODE_DIRECT, nickName, phoneNumber, mCurrentCallId);
//    }
//
//    /**
//     * CCP 双向回拨
//     *
//     * @param parm
//     */
//    public void backTalk(String[] parm) {
//        Log4Util.d("EUExCCP", "backTalk begin");
//        if (parm.length < 4) {
//            Toast.makeText(mContext, "CCP双向回拨参数有误", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
//        String phoneNumber = parm[0];
//        String destSerNum = parm[1];
//        String src_phone = parm[2];
//        String srcSerNum = parm[3];
//        // 发起一次直拨通话
//        // 参数为通话类型和对方的电话号码
//        CCPHelper.getInstance(mContext).getDevice().makeCallback(src_phone, phoneNumber, destSerNum, srcSerNum);
//
//        Toast.makeText(mContext, "回拨电话已拨出，请注意接听！", Toast.LENGTH_LONG)
//                .show();
//    }
//
//
//    public void openActivity(String mType, String nickName, String phoneStr, String mCurrentCallId) {
//
////        final String nickName = parm[0];
////        final String phoneName = parm[1];
////        ((ActivityGroup) mContext).runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
//
//        // 设置当呼入请求到达时, 唤起的界面
//        // 此处设置当接收到呼入连接时,启动CallInActivity(替换自己创建的呼入Activity)界面
//        Intent intent = new Intent(mContext, CallOutActivity.class);
//        intent.putExtra("mNickName", nickName);
//        intent.putExtra(CCPHelper.VALUE_DIAL_VOIP_INPUT, phoneStr);
//        intent.putExtra(CCPHelper.VALUE_DIAL_MODE,
//                mType);
//        intent.putExtra("mCurrentCallId", mCurrentCallId);
//        startActivity(intent);
////            }
////        });
//    }
//
//    /**
//     * WHAT_INIT_ERROR
//     * call back when regist over.
//     *
//     * @param reason {@link WHAT_INIT_ERROR} {@link WHAT_ON_CONNECT} {@link WHAT_ON_DISCONNECT}
//     * @param msg    regist failed message
//     * @see com.wedge.CCPHelper#WHAT_ON_CONNECT
//     * @see com.wedge.CCPHelper#WHAT_INIT_ERROR
//     * @see com.wedge.CCPHelper#WHAT_ON_DISCONNECT
//     */
//    @Override
//    public void onRegistResult(int reason, String msg) {
//        Log4Util.d("CALLBACK reason:" + reason + "--MSG:" + msg);
//        String cbStr = "";
//        if (reason == CCPHelper.WHAT_INIT_SUCCESS) {
//            cbStr = "{\"status\":\"success\",\"msg\":\"" + msg + "\"}";
//            jsCallback(FUNC_INITCCP_CALLBACK, 0, 1, cbStr);
//        } else if (reason == CCPHelper.WHAT_INIT_ERROR) {
//            cbStr = "{\"status\":\"failed\",\"msg\":\"" + msg + "\"}";
//            jsCallback(FUNC_INITCCP_CALLBACK, 0, 1, cbStr);
//        } else {
//            cbStr = "{\"status\":\"unknown\",\"msg\":\"" + msg + "\"}";
//            jsCallback(FUNC_INITCCP_CALLBACK, 0, 1, cbStr);
//        }
//    }

}
