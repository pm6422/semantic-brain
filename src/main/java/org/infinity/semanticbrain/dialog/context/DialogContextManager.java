package org.infinity.semanticbrain.dialog.context;

import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.dialog.entity.Device;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.BroadcastExecute;

@Component
public class DialogContextManager {

    @Autowired
    private ApplicationProperties      applicationProperties;
    private Map<String, DialogContext> dialogContextMap = new ConcurrentHashMap<>();

    public Output getLastAliveOutput(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return null;
        }
        return dialogContext.getAliveOutput();
    }

    public List<Output> getLastOutputs(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return null;
        }
        return dialogContext.getLatestOutputs();
    }

    @BroadcastExecute
    public void addOutput(Device device, Output output) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            dialogContext = DialogContext.of(applicationProperties.getDialogContext().getLatestOutputSize(),
                    applicationProperties.getDialogContext().getKeepAliveSeconds(),
                    applicationProperties.getDialogContext().getSkillModeKeepAliveSeconds());
            dialogContextMap.put(device.getDeviceId(), dialogContext);
        }
        dialogContext.addOutput(output);
    }

    public void expire(Device device) {
        DialogContext dialogContext = dialogContextMap.get(device.getDeviceId());
        if (dialogContext == null) {
            return;
        }
        // 设置上下文存活时间为0，代表上下文立即过期
        dialogContext.setKeepAliveSeconds(0);
    }
}
