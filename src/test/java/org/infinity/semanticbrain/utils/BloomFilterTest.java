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
        // 误判对数量：330
        // 我们故意取10000个不在过滤器里的值，却还有330个被认为在过滤器里，这说明了误判率为0.03.即，在不做任何设置的情况下，默认的误判率为0.03。

        // BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, 0.01); //此时误判率为0.01
        // 误判率越低，则底层维护的数组越长，占用空间越大。因此，误判率实际取值，根据服务器所能够承受的负载来决定，不是拍脑袋瞎想的。
    }
}
