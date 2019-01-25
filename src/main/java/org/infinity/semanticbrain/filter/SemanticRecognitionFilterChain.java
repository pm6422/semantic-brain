package org.infinity.semanticbrain.filter;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.entity.ProcessFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

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
    /**
     * Thread pool
     */
    private              ThreadPoolExecutor                    threadPool;


    public SemanticRecognitionFilterChain(List<SemanticRecognitionFilterConfig> filterConfigs, Map<String, SemanticFilter> semanticFilterMap, ThreadPoolExecutor threadPool) {
        this.filterConfigs = filterConfigs;
        this.semanticFilterMap = semanticFilterMap;
        this.threadPool = threadPool;
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput) {
        try {
            internalDoFilter(input, output, lastOutput);
        } catch (InterruptedException e) {
            // Ignore InterruptedException
            // Cancelling thread task can cause InterruptedException
        }

        // Set value to output
        output.setInput(input);
        output.setTime(LocalDateTime.now());
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
                CountDownLatch countDownLatch = new CountDownLatch(filterCountInParallel);
                List<Output> candidateOutputs = new ArrayList<>(filterCountInParallel);
                List<FutureTask<Output>> tasks = new ArrayList<>(filterCountInParallel);
                List<ProcessFilter> filters = new ArrayList<>();
                for (int i = 0; i < filterCountInParallel; i++) {
                    SemanticFilter parallelFilter = parallelFilters.get(i);
                    if (this.enableFilter(lastOutput, parallelFilter)) {
                        // Execute by using thread pool
                        FutureTask<Output> task = new FutureTask<>(() -> {
                            checkActiveThread();
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            // Note: it maybe a bug for one output in multiple threads env.
                            parallelFilter.doFilter(input, output, lastOutput, countDownLatch);
                            stopWatch.stop();
                            filters.add(ProcessFilter.of(parallelFilter.getName(), stopWatch.getTotalTimeMillis()));
                            return output;
                        });
                        tasks.add(task);
                        threadPool.execute(task);
                    } else {
                        countDownLatch.countDown();
                    }
                }
                // Wait for all parallel threads being executed
                countDownLatch.await();

                for (FutureTask<Output> task : tasks) {
                    try {
                        // FutureTask can check the thread execution status
                        if (task.isDone() && task.get() != null && task.get().isRecognized()) {
                            // Store complete result
                            candidateOutputs.add(task.get());
                        } else {
                            // Terminal all undone parallel threads
                            task.cancel(true);
                        }
                    } catch (ExecutionException e) {
                        LOGGER.error(ExceptionUtils.getStackTrace(e));
                    }
                }

                // Get the result of the highest score
                Output maxScoreOutput = Collections.max(candidateOutputs, Comparator.comparing(Output::getScore));
                BeanUtils.copyProperties(maxScoreOutput, output);

                // Add filters
                output.getFilters().addAll(filters);
            }

            // Set index to the next
            pos++;

            // Decide whether to continue filter
            if (this.continueToFilter(output)) {
                // Continue to execute filter chain
                this.doFilter(input, output, lastOutput);
            }
        }
    }

    private void handleRunnableException(Exception e) {
        LOGGER.error(ExceptionUtils.getStackTrace(e));
    }

    private boolean enableFilter(final Output lastOutput, final SemanticFilter parallelFilter) {
        return lastOutput == null && !parallelFilter.isContextFilter() || lastOutput != null && parallelFilter.isContextFilter();
    }

    private boolean continueToFilter(final Output output) {
        SemanticFilter matchedFilter = semanticFilterMap.get(output.getMatchedFilter());
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
