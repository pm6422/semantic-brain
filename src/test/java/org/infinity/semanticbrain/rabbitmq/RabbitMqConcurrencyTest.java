package org.infinity.semanticbrain.rabbitmq;//package org.infinity.semanticbrain.concurrency;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.infinity.semanticbrain.SemanticBrainLauncher;
import org.infinity.semanticbrain.component.RabbitMessageSender;
import org.infinity.semanticbrain.config.ApplicationConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SemanticBrainLauncher.class)
@ActiveProfiles(ApplicationConstants.SPRING_PROFILE_TEST)
public class RabbitMqConcurrencyTest {

    private static final Logger              LOGGER = LoggerFactory.getLogger(RabbitMqConcurrencyTest.class);
    @Autowired
    private              RabbitMessageSender rabbitMessageSender;

    public RabbitMqConcurrencyTest() {
        super();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testConcurrency() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        int requestCount = 50_000;
        int threadPoolSize = 20;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        IntStream.range(0, requestCount).forEach(i -> {
            threadPool.execute(() -> {
                LOGGER.debug("Active thread count: {}", Thread.activeCount());
                try {
                    rabbitMessageSender.publish(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        threadPool.shutdown();
        // Blocks until all tasks have completed execution after a shutdown request
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            watch.stop();
            LOGGER.debug("Total: {} s", watch.getTotalTimeMillis() / 1000);
            LOGGER.debug("Mean: {} ms", watch.getTotalTimeMillis() / requestCount);
            LOGGER.debug("TPS: {}", requestCount / (watch.getTotalTimeMillis() / 1000));
        }
    }
}
