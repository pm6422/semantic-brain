package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;

public interface NluService {

    Output recognize(Input input);

    Output recognize(Input input, String skillCode);

    Output recognize(Input input, boolean logOutput);
}
