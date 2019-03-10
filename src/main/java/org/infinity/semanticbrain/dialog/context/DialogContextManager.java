package org.infinity.semanticbrain.dialog.context;

import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.dialog.entity.DialogContext;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DialogContextManager {

    @Autowired
    private ApplicationProperties      applicationProperties;
    private Map<String, DialogContext> dialogContextMap = new ConcurrentHashMap<>();

    public Output getLastOutput(Input input) {
        DialogContext dialogContext = dialogContextMap.get(input.getDevice().getDeviceId());
        if (dialogContext == null) {
            return null;
        }
        return dialogContext.getLastOutput();
    }

    public void addOutput(Input input, Output output) {
        DialogContext dialogContext = dialogContextMap.get(input.getDevice().getDeviceId());
        if (dialogContext == null) {
            dialogContext = DialogContext.of(applicationProperties.getDialogContext().getLatestOutputSize(),
                    applicationProperties.getDialogContext().getKeepAliveSeconds(),
                    applicationProperties.getDialogContext().getSkillModeKeepAliveSeconds());
            dialogContextMap.put(input.getDevice().getDeviceId(), dialogContext);
        }
        dialogContext.addOutput(output);
    }
}
