package org.infinity.semanticbrain.dialog.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class RecognizeFilterChain {

    /**
     * Filters chains
     */
    private final List<List<RecognizeFilter>>  filtersChains;
    /**
     * The map which is used to save semantic filterConfigs
     */
    private final Map<String, RecognizeFilter> semanticFilterMap;
    /**
     * Thread pool
     */
    private final ExecutorService              threadPool;
    /**
     * The int which is used to maintain the current position in the filter chain
     */
    private       int                          pos = 0;


    public RecognizeFilterChain(List<List<RecognizeFilter>> filtersChains,
                                Map<String, RecognizeFilter> semanticFilterMap,
                                ExecutorService threadPool) {
        this.filtersChains = filtersChains;
        this.semanticFilterMap = semanticFilterMap;
        this.threadPool = threadPool;
    }

    /**
     * FilterChain接口的doFilter方法用于把请求交给Filter链中的下一个Filter去处理
     *
     * @param input      input
     * @param output     output
     * @param lastOutput lastOutput
     * @param skillCodes skillCodes
     */
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

    private void internalDoFilter(final Input input, final Output output, final Output lastOutput,
                                  List<String> skillCodes) throws InterruptedException {
        // Call the next filter
        if (pos < filtersChains.size()) {
            List<RecognizeFilter> parallelFilters = filtersChains.get(pos);

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
                for (RecognizeFilter parallelFilter : parallelFilters) {
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
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }

                // Get the result of the highest score
                Output maxScoreOutput = Collections.max(candidateOutputs, Comparator.comparing(Output::getScore));
                // TODO: bug found
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
        log.error(ExceptionUtils.getStackTrace(e));
    }

    private boolean enableFilter(final Output lastOutput, final RecognizeFilter parallelFilter) {
        return lastOutput == null && !parallelFilter.isContextFilter() || lastOutput != null && parallelFilter.isContextFilter();
    }

    private boolean continueToFilter(final Output output) {
        RecognizeFilter matchedFilter = semanticFilterMap.get(output.getMatchedFilter());
        return !output.recognized() || output.recognized() && matchedFilter.getType().equals(RecognizeFilter.TYPE.TYPE_SERIAL_COMPARING);
    }

    private void checkActiveThread() {
        log.debug("Active thread count: {}", Thread.activeCount());
        if (Thread.activeCount() == 0) {
            limitRate();
        }
    }

    private void limitRate() {
        log.error("Active thread is exhausted.");
        // Do some other rate limit
    }
}
