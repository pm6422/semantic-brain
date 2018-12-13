package org.infinity.semanticbrain.filter.impl;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.filter.AbstractSemanticFilter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
    protected Output recognize(Input input, final Output lastOutput) {
        Output output = new Output();
        try {
            TimeUnit.MINUTES.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }
}
