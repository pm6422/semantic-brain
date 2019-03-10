package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

public interface NaturalLanguageUnderstandingService {

    Output recognize(Input input);

    Output recognize(Input input, String skillCode);

    void setLogIntentionResult(boolean logIntentionResult);
}
