package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel("输入设备")
public class Device implements Serializable {

    private static final long         serialVersionUID = 6996205094278193728L;
    @ApiModelProperty("公司ID")
    private              String       companyId;
    @ApiModelProperty("型号ID")
    private              String       modelId;
    @ApiModelProperty("用户IDs")
    private              List<String> userIds;// 同一个设备可以绑定多个用户
    @ApiModelProperty("设备ID")
    private              String       deviceId;

    public Device() {
    }

    public static Device of(List<String> userIds) {
        return of(null, null, userIds, null);
    }

    public static Device of(String modelId, List<String> userIds) {
        return of(null, modelId, userIds, null);
    }

    public static Device of(String companyId, String modelId, List<String> userIds, String deviceId) {
        Device device = new Device();
        device.setCompanyId(companyId);
        device.setModelId(modelId);
        device.setUserIds(userIds);
        device.setDeviceId(deviceId);
        return device;
    }

    public static Device of(String userId) {
        return of(null, null, userId, null);
    }

    public static Device of(String modelId, String userId) {
        return of(null, modelId, userId, null);
    }

    public static Device of(String companyId, String modelId, String userId, String deviceId) {
        Device device = new Device();
        device.setCompanyId(companyId);
        device.setModelId(modelId);
        device.addUserId(userId);
        device.setDeviceId(deviceId);
        return device;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void addUserId(String userId) {
        if (userIds == null) {
            userIds = new ArrayList<>();
        }
        userIds.add(userId);
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "UserInfo [companyId=" + companyId + ", modelId=" + modelId + ", userIds=" + userIds + ", deviceId=" + deviceId
                + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(companyId, device.companyId) &&
                Objects.equals(modelId, device.modelId) &&
                Objects.equals(userIds, device.userIds) &&
                Objects.equals(deviceId, device.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, modelId, userIds, deviceId);
    }
}