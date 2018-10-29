package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;

public interface SemanticRecognitionService {

    Output recognize(Input input);

    void setLogIntentionResult(boolean logIntentionResult);
}
