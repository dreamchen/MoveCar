package com.wedge.movecar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wedge.movecar.R;
import com.wedge.movecar.common.SharedPreUtil;
import com.wedge.movecar.entity.UserEntity;
import com.wedge.movecar.voip.CCPHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenerlei on 15/6/18.
 */
public class LoginActivity extends Activity {

    private RequestQueue mRequestQueue;
    private UserEntity userEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mRequestQueue = Volley.newRequestQueue(this);
        initParam();
    }

    private void initParam() {
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtName = (EditText) findViewById(R.id.edtName);
                EditText edtPwd = (EditText) findViewById(R.id.edtPwd);
                String url = "http://123.56.44.249:8080/CallCar/system/login/login?name=" + edtName.getText() + "&password=" + edtPwd.getText();
                Log.d("TAG:",url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.d("TAG+", response.toString());
                                try {
                                    if ("success".equals(response.getString("status"))) {
                                        JSONArray jsonArrayObj = response.getJSONArray("items");
                                        JSONObject jsonObj = jsonArrayObj.getJSONObject(0);
                                        userEntity = new UserEntity();
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
                                        SharedPreUtil.getInstance().put("userEntity", userEntity);
                                        getAppInfo();
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
                mRequestQueue.add(jsonObjectRequest);
            }
        });
    }

    public void getAppInfo() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://123.56.44.249:8080/CallCar/module/yuntongxun/getAppInfo?appId=" + userEntity.getAppId(), null,
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
        mRequestQueue.add(jsonObjectRequest);
    }

    /**
     * CCP 初始化
     *
     * @param parm
     */
    public void initCCP(String ip, String port) {
        Log.d("EUExCCP", "init is begin");
        CCPHelper.getInstance().initSDK(ip, port, userEntity.getVoipAccount(), userEntity.getVoipPwd(), userEntity.getSubSid(), userEntity.getSubToken());
        Log.d("EUExCCP", "parm is loaded!");
        Log.d("EUExCCP", "init");

        Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
        startActivity(intent);
    }
}