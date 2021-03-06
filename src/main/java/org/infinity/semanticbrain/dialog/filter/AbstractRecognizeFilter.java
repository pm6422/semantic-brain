package org.infinity.semanticbrain.dialog.filter;

import lombok.extern.slf4j.Slf4j;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public abstract class AbstractRecognizeFilter implements RecognizeFilter {

    @Override
    public void doFilter(final Input input, final Output output,
                         final Output lastOutput, List<String> skillCodes) throws InterruptedException {
        log.debug("Filtering by {}", this.getName());
        Output candidate = this.recognize(input, lastOutput, skillCodes);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            candidate.setMatchedFilter(this.getName());
            BeanUtils.copyProperties(candidate, output);
        }
        log.debug("Filtered by {}", this.getName());
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput,
                         List<String> skillCodes, CountDownLatch countDownLatch) throws InterruptedException {
        log.debug("Filtering by {}", this.getName());
        Output candidate = this.recognize(input, lastOutput, skillCodes);
        if (candidate.getScore().compareTo(output.getScore()) > 0) {
            // Store the result with the higher score
            candidate.setMatchedFilter(this.getName());
            BeanUtils.copyProperties(candidate, output);
        }
        log.debug("Filtered by {}", this.getName());
        this.countDown(candidate, countDownLatch);
    }

    private void countDown(final Output candidate, final CountDownLatch countDownLatch) {
        Assert.notNull(countDownLatch, "countDownLatch must NOT be null!");
        if (this.getType().equals(TYPE.TYPE_IMMEDIATE_TERMINATE) && candidate.recognized()) {
            // Decrease count to 0 in one step
            while (countDownLatch.getCount() != 0) {
                countDownLatch.countDown();
            }
        } else {
            countDownLatch.countDown();
        }
    }

    protected abstract Output recognize(final Input input, final Output lastOutput, List<String> skillCodes) throws InterruptedException;
}
