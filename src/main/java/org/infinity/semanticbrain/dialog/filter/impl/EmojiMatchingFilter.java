package org.infinity.semanticbrain.dialog.filter.impl;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.filter.AbstractSemanticFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Component
public class EmojiMatchingFilter extends AbstractSemanticFilter {

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
        return TYPE.TYPE_IMMEDIATE_TERMINATE;
    }

    @Override
    protected Output recognize(Input input, final Output lastOutput, List<String> skillCodes) throws InterruptedException {
        Output output = new Output();
//        TimeUnit.SECONDS.sleep(1L);
//        output.addIntention(Intent.of("Emoji", "Emoji", "Emoji", "Emoji"));
//        output.setScore(Output.TOP_SCORE);
        return output;
    }
}
