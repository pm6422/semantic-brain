package org.infinity.semanticbrain.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BloomFilterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BloomFilterTest.class);

    @Test
    public void testPerformance() throws IOException {
        List<String> lines = IOUtils.readLines(new ClassPathResource("bloom-filter-dic.txt").getInputStream(), StandardCharsets.UTF_8);

        LOGGER.debug("Sample count: {}", lines.size());
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), lines.size());
        lines.forEach(line -> bloomFilter.put(line));

        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.debug("Hit 龟王: {}", bloomFilter.mightContain("龟王"));
        LOGGER.debug("Hit 龟王孙: {}", bloomFilter.mightContain("龟王孙"));

        watch.stop();
        LOGGER.debug("Elapsed: {} ms", watch.getTotalTimeMillis());
    }

    @Test
    public void misjudge() {
        int size = 1_000_000;
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size);

        IntStream.range(0, size).forEach(i -> bloomFilter.put(i));

        List<Integer> list = new ArrayList<Integer>(1000);
        StopWatch watch = new StopWatch();
        watch.start();
        //故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里
        for (int i = size + 10000; i < size + 20000; i++) {
            if (bloomFilter.mightContain(i)) {
                list.add(i);
            }
        }

        watch.stop();
        LOGGER.debug("Elapsed: {} ms", watch.getTotalTimeMillis());
        LOGGER.debug("误判数量: {}", list.size());
    }
}
