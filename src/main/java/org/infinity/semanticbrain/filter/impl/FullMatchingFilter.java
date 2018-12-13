package org.infinity.semanticbrain.filter.impl;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.filter.AbstractSemanticFilter;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Component
public class FullMatchingFilter extends AbstractSemanticFilter {

    @Override
    public boolean isContextFilter() {
        return false;
    }

    @Override
    public String getName() {
        return uncapitalize(this.getClass().getSimpleName());
    }

    @Override
    public TYPE getType() {
        return TYPE.TYPE_PARALLEL_COMPARING;
    }

    @Override
    protected Output recognize(Input input, final Output lastOutput) throws InterruptedException {
        Output output = new Output();
        // stimulate time consuming event
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 100000; j++) {
                if (j % 100000 == 0)
                    System.out.println(j);
            }
        }
        return output;
    }
}
