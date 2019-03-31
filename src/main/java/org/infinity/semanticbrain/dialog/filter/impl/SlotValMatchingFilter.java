package org.infinity.semanticbrain.dialog.filter.impl;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.filter.AbstractSemanticFilter;
import org.infinity.semanticbrain.dialog.intent.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Component
public class SlotValMatchingFilter extends AbstractSemanticFilter {

    @Autowired
    private Matcher matcher;

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
        TimeUnit.SECONDS.sleep(1L);
//        output.addIntent(Intent.of("Music", "Music", "Entertainment", "Music", "Music", "Music"));
        output.setScore(Output.TOP_SCORE);
        return output;
    }
}
