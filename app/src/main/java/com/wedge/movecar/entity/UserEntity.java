package com.wedge.movecar.entity;

import java.io.Serializable;

/**
 * Created by chenerlei on 15/4/28.
 */
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -1147439846593832705L;

    private Long id;
    private String name;
    private String realName;
    private String password;
    private String mobileNumber;
    private String appId;
    private String subSid;
    private String subToken;
    private String voipAccount;
    private String voipPwd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubSid() {
        return subSid;
    }

    public void setSubSid(String subSid) {
        this.subSid = subSid;
    }

    public String getSubToken() {
        return subToken;
    }

    public void setSubToken(String subToken) {
        this.subToken = subToken;
    }

    public String getVoipAccount() {
        return voipAccount;
    }

    public void setVoipAccount(String voipAccount) {
        this.voipAccount = voipAccount;
    }

    public String getVoipPwd() {
        return voipPwd;
    }

    public void setVoipPwd(String voipPwd) {
        this.voipPwd = voipPwd;
    }
}
