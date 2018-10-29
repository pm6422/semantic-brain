package org.infinity.semanticbrain.filter;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractSemanticFilter implements SemanticFilter {

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput) {
        Output candidate = this.recognize(input, lastOutput);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result of the highest score
            BeanUtils.copyProperties(candidate, output);
        }
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput, CountDownLatch countDownLatch) {
        Output candidate = this.recognize(input, lastOutput);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result of the highest score
            BeanUtils.copyProperties(candidate, output);
        }
        this.countDown(countDownLatch);
    }

    protected abstract Output recognize(final Input input, final Output lastOutput);

    protected void countDown(CountDownLatch countDownLatch) {
        Assert.notNull(countDownLatch, "countDownLatch must NOT be null.");
        if (this.getType().equals(TYPE.TYPE_IMMEDIATE_TERMINATE)) {
            // Decrease count to 0 in one step
            while (countDownLatch.getCount() != 0) {
                countDownLatch.countDown();
            }
        } else {
            countDownLatch.countDown();
        }
    }
}
