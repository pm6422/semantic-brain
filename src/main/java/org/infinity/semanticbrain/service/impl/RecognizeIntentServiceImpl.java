package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.service.RecognizeIntentService;
import org.springframework.stereotype.Service;

@Service
public class RecognizeIntentServiceImpl implements RecognizeIntentService {
    @Override
    public Output matchSlotVal(Input input, Output lastOutput) {
        return null;
    }
}
