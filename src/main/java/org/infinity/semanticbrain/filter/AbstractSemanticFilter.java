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
    public void doFilter(final Input input, final Output output, final Output lastOutput) throws InterruptedException {
        LOGGER.debug("Filtering by {}", this.getName());
        Output candidate = this.recognize(input, lastOutput);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            candidate.setMatchedFilter(this.getName());
            BeanUtils.copyProperties(candidate, output);
        }
        LOGGER.debug("Filtered by {}", this.getName());
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput, CountDownLatch countDownLatch) throws InterruptedException {
        LOGGER.debug("Filtering by {}", this.getName());
        Output candidate = this.recognize(input, lastOutput);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            candidate.setMatchedFilter(this.getName());
            BeanUtils.copyProperties(candidate, output);
        }
        LOGGER.debug("Filtered by {}", this.getName());
        this.countDown(candidate, countDownLatch);
    }

    private void countDown(final Output candidate, final CountDownLatch countDownLatch) {
        Assert.notNull(countDownLatch, "countDownLatch must NOT be null.");
        if (this.getType().equals(TYPE.TYPE_IMMEDIATE_TERMINATE) && candidate.isRecognized()) {
            // Decrease count to 0 in one step
            while (countDownLatch.getCount() != 0) {
                countDownLatch.countDown();
            }
        } else {
            countDownLatch.countDown();
        }
    }

    protected abstract Output recognize(final Input input, final Output lastOutput) throws InterruptedException;
}
