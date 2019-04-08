package org.infinity.semanticbrain.dialog.filter;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

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
    private              ExecutorService                       threadPool;


    public SemanticRecognitionFilterChain(List<SemanticRecognitionFilterConfig> filterConfigs, Map<String, SemanticFilter> semanticFilterMap, ExecutorService threadPool) {
        this.filterConfigs = filterConfigs;
        this.semanticFilterMap = semanticFilterMap;
        this.threadPool = threadPool;
    }

    @Override
    public void doFilter(final Input input, final Output output, final Output lastOutput, List<String> skillCodes) {
        try {
            internalDoFilter(input, output, lastOutput, skillCodes);
        } catch (InterruptedException e) {
            // Ignore InterruptedException
            // Cancelling thread task can cause InterruptedException
        }

        // Set value to output
        output.setInput(input);
        output.setTime(Instant.now());
    }

    private void internalDoFilter(final Input input, final Output output, final Output lastOutput, List<String> skillCodes) throws InterruptedException {
        // Call the next filter
        if (pos < filterConfigs.size()) {
            SemanticRecognitionFilterConfig filterConfig = filterConfigs.get(pos);
            List<SemanticFilter> parallelFilters = filterConfig.getFilters();

            if (parallelFilters.size() == 1) {
                // Executing one filter
                parallelFilters.get(0).doFilter(input, output, lastOutput, skillCodes);
            } else {
                // Executing multiple filterConfigs concurrently
                int filterCountInParallel = parallelFilters.size();
                CountDownLatch countDownLatch = new CountDownLatch(filterCountInParallel);
                List<Output> candidateOutputs = new ArrayList<>(filterCountInParallel);
                List<Future<Output>> outputs = new ArrayList<>(filterCountInParallel);
                List<ProcessFilter> filters = new ArrayList<>();
                for (int i = 0; i < filterCountInParallel; i++) {
                    SemanticFilter parallelFilter = parallelFilters.get(i);
                    if (this.enableFilter(lastOutput, parallelFilter)) {
                        // Execute by using thread pool
                        Callable<Output> task = () -> {
                            checkActiveThread();
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();
                            // Note: it maybe a bug for one output in multiple threads env.
                            parallelFilter.doFilter(input, output, lastOutput, skillCodes, countDownLatch);
                            stopWatch.stop();
                            filters.add(ProcessFilter.of(parallelFilter.getName(), stopWatch.getTotalTimeMillis()));
                            return output;
                        };
                        outputs.add(threadPool.submit(task));
                    } else {
                        countDownLatch.countDown();
                    }
                }
                // Wait for all parallel threads being executed
                countDownLatch.await();

                for (Future<Output> task : outputs) {
                    try {
                        // FutureTask can check the thread execution status
                        if (task.isDone() && task.get() != null && task.get().recognized()) {
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
                this.doFilter(input, output, lastOutput, skillCodes);
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
        return !output.recognized() || output.recognized() && matchedFilter.getType().equals(SemanticFilter.TYPE.TYPE_SERIAL_COMPARING);
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
