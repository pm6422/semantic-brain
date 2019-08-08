package org.infinity.semanticbrain.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.stream.IntStream;

public class BloomFilterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BloomFilterTest.class);

    @Test
    public void testPerformance() {
        int size = 1_000_000;
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size);
        IntStream.range(0, size).forEach(i -> bloomFilter.put(i));

        StopWatch watch = new StopWatch();
        watch.start();
        //判断这一百万个数中是否包含29999这个数
        LOGGER.debug("Hit the value: {}", bloomFilter.mightContain(29999));

        watch.stop();
        LOGGER.debug("Elapsed: {} ms", watch.getTotalTimeMillis());
    }
}
