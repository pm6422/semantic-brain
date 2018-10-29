package org.infinity.semanticbrain.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 设备信息类
 * Note: Serialization friendly class
 */
public class Device implements Serializable {

    private static final long         serialVersionUID = 1L;
    // 公司ID
    private              String       appId;
    // 型号ID
    private              String       robotId;
    // 用户ID，同一个设备可以绑定多个用户
    private              List<String> userIds;
    // 设备ID
    private              String       deviceId;

    public Device() {
    }

    public Device(List<String> userIds) {
        this.userIds = userIds;
    }

    public Device(List<String> userIds, String robotId) {
        this.userIds = userIds;
        this.robotId = robotId;
    }

    public Device(String appId, String robotId, List<String> userIds, String deviceId) {
        super();
        this.appId = appId;
        this.robotId = robotId;
        this.userIds = userIds;
        this.deviceId = deviceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "UserInfo [appId=" + appId + ", robotId=" + robotId + ", userIds=" + userIds + ", deviceId=" + deviceId
                + "]";
    }
}