package org.infinity.semanticbrain.filter;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.entity.ProcessFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SemanticRecognitionFilterChain implements SemanticFilterChain {

    private static final Logger                                LOGGER            = LoggerFactory.getLogger(SemanticRecognitionFilterChain.class);
    /**
     * Filters
     */
    private              List<SemanticRecognitionFilterConfig> filterConfigs;
    /**
     * The map which is used to save semantic filterConfigs
     */
    private              Map<String, SemanticFilter>           semanticFilterMap = new HashMap<>();
    /**
     * The int which is used to maintain the current position in the filter chain
     */
    private              int                                   pos               = 0;


    public SemanticRecognitionFilterChain(List<SemanticRecognitionFilterConfig> filterConfigs, Map<String, SemanticFilter> semanticFilterMap) {
        this.filterConfigs = filterConfigs;
        this.semanticFilterMap = semanticFilterMap;
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput) throws InterruptedException {
        internalDoFilter(input, output, lastOutput);

        // Set value to output
        output.setInput(input);
        output.setTime(Instant.now());
    }

    private void internalDoFilter(final Input input, final Output output, final Output lastOutput) throws InterruptedException {
        // Call the next filter
        if (pos < filterConfigs.size()) {
            SemanticRecognitionFilterConfig filterConfig = filterConfigs.get(pos);
            List<SemanticFilter> parallelFilters = filterConfig.getFilters();

            if (parallelFilters.size() == 1) {
                // Executing one filter
                parallelFilters.get(0).doFilter(input, output, lastOutput);
            } else {
                // Executing multiple filterConfigs concurrently
                int filterCountInParallel = parallelFilters.size();
                ThreadPoolExecutor threadPoolInParallel = new ThreadPoolExecutor(filterCountInParallel, filterCountInParallel, 1, TimeUnit.SECONDS,
                        new LinkedBlockingQueue(15), new ThreadPoolExecutor.DiscardPolicy());
                CountDownLatch countDownLatch = new CountDownLatch(filterCountInParallel);
                List<Output> candidateOutputs = new ArrayList<>(filterCountInParallel);
                for (int i = 0; i < filterCountInParallel; i++) {
                    SemanticFilter parallelFilter = parallelFilters.get(i);
                    if (this.startToFilter(lastOutput, parallelFilter)) {
                        // Execute by using thread pool
                        threadPoolInParallel.submit(this.createWrappedRunnable(() -> {
                            checkActiveThread();
                            Output threadOutput = new Output();
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            parallelFilter.doFilter(input, threadOutput, lastOutput, countDownLatch);
                            stopWatch.stop();
                            threadOutput.getFilters().add(new ProcessFilter(parallelFilter.getName(), stopWatch.getTotalTimeMillis()));
                            candidateOutputs.add(threadOutput);
                        }));
                    } else {
                        countDownLatch.countDown();
                    }
                }
                // Wait for all child thread being executed
                countDownLatch.await();

                // Try to shutdown all submitted task thread after getting candidate outputs, but do NOT guarantee all the threads can be shutdown
                // TODO: check the logic
                threadPoolInParallel.shutdownNow();

                // Get the result of the highest score
                Output maxScoreOutput = Collections.max(candidateOutputs, Comparator.comparing(Output::getScore));
                BeanUtils.copyProperties(maxScoreOutput, output);
            }

            // Set index to the next
            pos++;

            if (this.continueToFilter(output)) {
                // Continue to execute filter chain
                this.doFilter(input, output, lastOutput);
            }
        }
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    LOGGER.debug("Interrupted task");
                } else {
                    handleRunnableException(e);
                }
            }
        };
    }

    private void handleRunnableException(Exception e) {
        LOGGER.error(ExceptionUtils.getStackTrace(e));
    }

    private boolean startToFilter(final Output lastOutput, SemanticFilter parallelFilter) {
        return lastOutput == null && !parallelFilter.isContextFilter() || lastOutput != null && parallelFilter.isContextFilter();
    }

    private boolean continueToFilter(final Output output) {
        SemanticFilter matchedFilter = semanticFilterMap.get(output.getLastFilter());
        return !output.isRecognized() || output.isRecognized() && matchedFilter.getType().equals(SemanticFilter.TYPE.TYPE_SERIAL_COMPARING);
    }

    private void checkActiveThread() {
        LOGGER.debug("Active thread count: {}", Thread.activeCount());
        if (Thread.activeCount() == 0) {
            limitRate();
        }
    }

    private void limitRate() {
        LOGGER.error("Active thread is exhausted.");
        // Do some other rate limit
    }
}
