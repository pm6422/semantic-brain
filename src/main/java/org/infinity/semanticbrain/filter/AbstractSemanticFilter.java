package org.infinity.semanticbrain.filter;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractSemanticFilter implements SemanticFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSemanticFilter.class);

    @Override
    public Output doFilter(final Input input, final Output output, final Output lastOutput) {
        LOGGER.debug("Filter by {}", this.getName());
        Output result = new Output();
        Output candidate = this.recognize(input, lastOutput);
        if (output.isRecognized() && candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            BeanUtils.copyProperties(candidate, output);
            result = output;
        }
        return result;
    }

    @Override
    public Output doFilter(final Input input, final Output output, final Output lastOutput, CountDownLatch countDownLatch) {
        LOGGER.debug("Filter by {}", this.getName());
        Output result = new Output();
        Output candidate = this.recognize(input, lastOutput);
        if (output.isRecognized() && candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            BeanUtils.copyProperties(candidate, output);
            result = output;
        }
        this.countDown(countDownLatch);
        return result;
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
