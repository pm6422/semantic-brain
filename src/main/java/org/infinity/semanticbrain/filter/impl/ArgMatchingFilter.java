package org.infinity.semanticbrain.filter.impl;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Intention;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.filter.AbstractSemanticFilter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Component
public class ArgMatchingFilter extends AbstractSemanticFilter {

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
        TimeUnit.SECONDS.sleep(2L);
        output.addIntention(Intention.of("Music", "Music", "Music", "Music"));
        output.setScore(Output.TOP_SCORE);
        return output;
    }
}
