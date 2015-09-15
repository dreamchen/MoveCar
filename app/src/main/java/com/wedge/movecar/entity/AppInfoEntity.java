package com.wedge.movecar.entity;

import java.io.Serializable;

/**
 * Created by chenerlei on 15/4/29.
 */
public class AppInfoEntity implements Serializable {
    private static final long serialVersionUID = 6298826450750487685L;

    private String id;
    private String name;
    private String mainSid;
    private String mainToken;
    private String restUrl;
    private String restPort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainSid() {
        return mainSid;
    }

    public void setMainSid(String mainSid) {
        this.mainSid = mainSid;
    }

    public String getMainToken() {
        return mainToken;
    }

    public void setMainToken(String mainToken) {
        this.mainToken = mainToken;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestPort() {
        return restPort;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }
}
