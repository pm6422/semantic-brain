package org.infinity.semanticbrain.concurrency;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.service.SemanticRecognitionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SemanticRecognitionConcurrencyTest {

    private static final Logger                     LOGGER = LoggerFactory.getLogger(SemanticRecognitionConcurrencyTest.class);
    @Autowired
    private              SemanticRecognitionService semanticRecognitionService;

    @Before
    public void setUp() {

    }

    /**
     * https://www.tuicool.com/articles/bANZVrq
     *
     * @throws Exception
     */
    @Test
    public void testConcurrency() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        int requestCount = 10000;
        int threadPoolSize = 20;
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        IntStream.range(0, requestCount).forEach(i -> {
            threadPool.execute(() -> {
                LOGGER.debug("Active thread count: {}", Thread.activeCount());
                try {
                    Input input = new Input();
                    semanticRecognitionService.recognize(input);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        countDownLatch.await();
        watch.stop();
        LOGGER.debug("Total: {} s", watch.getTotalTimeMillis() / 1000);
        LOGGER.debug("Mean: {} ms", watch.getTotalTimeMillis() / requestCount);
        LOGGER.debug("TPS: {}", requestCount / (watch.getTotalTimeMillis() / 1000));
    }
}
