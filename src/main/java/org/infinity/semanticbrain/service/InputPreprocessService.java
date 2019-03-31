package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

public interface InputPreprocessService {
    void preprocess(Input input, Output lastOutput);
}
