package org.infinity.semanticbrain.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Entity class for device
 * Note: Serialization friendly class
 */
public class Device implements Serializable {

    private static final long         serialVersionUID = 6996205094278193728L;
    // 公司ID
    private              String       companyId;
    // 型号ID
    private              String       modelId;
    // 用户ID，同一个设备可以绑定多个用户
    private              List<String> userIds;
    // 设备ID
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