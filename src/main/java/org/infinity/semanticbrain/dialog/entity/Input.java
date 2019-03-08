package org.infinity.semanticbrain.dialog.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Note: Serialization friendly class
 */
public class Input implements Serializable {

    private static final long   serialVersionUID = 1L;
    // Track the request for trace
    private              String requestId;
    // Original input text
    private              String text;
    // Device information
    private              Device device;

    public Input() {
    }

    public static Input of(String requestId, String text, Device device) {
        Input input = new Input();
        input.setRequestId(requestId);
        input.setText(text);
        input.setDevice(device);
        return input;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return Objects.equals(text, input.text) &&
                Objects.equals(device, input.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, device);
    }

    @Override
    public String toString() {
        return "Input{" +
                "requestId='" + requestId + '\'' +
                ", text='" + text + '\'' +
                ", device=" + device +
                '}';
    }
}
