package org.infinity.semanticbrain.entity;

import java.io.Serializable;

/**
 * Note: Serialization friendly class
 */
public class Input implements Serializable {

    private static final long   serialVersionUID = 1L;
    private              String text;
    private              Device device;

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
}
