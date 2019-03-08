package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

public interface SemanticRecognitionService {

    Output recognize(Input input);

    void setLogIntentionResult(boolean logIntentionResult);
}
