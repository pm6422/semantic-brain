package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

public interface RecognizeIntentService {
    Output matchSlotVal(Input input, Output lastOutput);
}
