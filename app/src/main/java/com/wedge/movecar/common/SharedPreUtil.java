package com.wedge.movecar.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.StreamCorruptedException;

/**
 * Created by chenerlei on 15/4/28.
 */
public class SharedPreUtil {

    private static SharedPreUtil s_SharedPreUtil;

    private SharedPreferences msp;

    // 初始化，一般在应用启动之后就要初始化
    public static synchronized void initSharedPreference(Context context) {
        if (s_SharedPreUtil == null) {
            s_SharedPreUtil = new SharedPreUtil(context);
        }
    }

    /**
     * 获取唯一的instance
     *
     * @return
     */
    public static synchronized SharedPreUtil getInstance() {
        return s_SharedPreUtil;
    }

    public SharedPreUtil(Context context) {
        msp = context.getSharedPreferences("SharedPreUtil",
                Context.MODE_PRIVATE | Context.MODE_APPEND);
    }

    public SharedPreferences getSharedPref() {
        return msp;
    }


    public synchronized void put(String key, Object obj) {

        SharedPreferences.Editor editor = msp.edit();

        String str = "";
        try {
            str = SerializableUtil.obj2Str(obj);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        editor.putString(key, str);
        editor.commit();
    }

    public synchronized Object get(String key) {

        Object obj = null;
        //获取序列化的数据
        String str = msp.getString(key, "");

        try {
            obj = SerializableUtil.str2Obj(str);
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }

    public synchronized void delete(String key) {
        SharedPreferences.Editor editor = msp.edit();
        editor.putString(key, "");

        editor.commit();
    }
}
