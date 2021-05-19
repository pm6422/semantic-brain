package org.infinity.semanticbrain.dialog.filter.impl;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.filter.AbstractRecognizeFilter;
import org.infinity.semanticbrain.dialog.intent.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Component
public class SlotValMatchingFilter extends AbstractRecognizeFilter {

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
    protected Output recognize(Input input, final Output lastOutput, List<String> skillCodes) throws InterruptedException {
        Output output = matcher.matchSlotVal(input, lastOutput, skillCodes);
        return output;
    }
}
