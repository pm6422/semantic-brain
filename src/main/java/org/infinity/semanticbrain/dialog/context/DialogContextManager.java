package org.infinity.semanticbrain.dialog.context;

import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.dialog.entity.Device;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.BroadcastExecute;

@Component
public class DialogContextManager {

    @Autowired
    private ApplicationProperties      applicationProperties;
    private Map<String, DialogContext> dialogContextMap = new ConcurrentHashMap<>();

    /**
     * Get the last active output and return null if it is expired
     *
     * @return last active dialog context output
     */
    public Output getLastAliveOutput(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return null;
        }

        if (dialogContext.getLastOutput().expired() || dialogContext.getLastOutput().skillModeExpired()) {
            // Clear expired data
            dialogContextMap.remove(device.getDeviceId());
            return null;
        }

        return dialogContext.getLastOutput();
    }

    /**
     * Get the latest outputs
     *
     * @param device: device info
     * @return latest outputs
     */
    public List<Output> getLastOutputs(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return null;
        }
        return dialogContext.getLatestOutputs();
    }

    /**
     * Save this output to context
     *
     * @param device: device info
     * @param output: output info
     */
    @BroadcastExecute
    public void addOutput(Device device, Output output) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            dialogContext = DialogContext.of(applicationProperties.getDialogContext().getLatestOutputSize());
            dialogContextMap.put(device.getDeviceId(), dialogContext);
        }
        dialogContext.addOutput(output);
    }

    /**
     * Expire dialog context
     *
     * @param device: device info
     */
    @BroadcastExecute
    public void expire(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return;
        }
        // Clear expired data
        dialogContextMap.remove(device.getDeviceId());
    }

    /**
     * Remove expired dialog context every hour
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void removeExpired() {
        Iterator<Map.Entry<String, DialogContext>> iterator = dialogContextMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DialogContext> next = iterator.next();
            if (next.getValue().getLastOutput().expired() || next.getValue().getLastOutput().skillModeExpired()) {
                // Clear expired data
                iterator.remove();
            }
        }
    }
}
