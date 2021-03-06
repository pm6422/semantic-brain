package org.infinity.semanticbrain.intent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.RandomStringUtils;
import org.infinity.semanticbrain.dialog.entity.*;
import org.infinity.semanticbrain.dialog.intent.Matcher;
import org.infinity.semanticbrain.service.impl.SlotValServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.trie4j.patricia.PatriciaTrie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(MockitoJUnitRunner.class)
public class MatcherTest {

    private static final Logger             LOGGER = LoggerFactory.getLogger(MatcherTest.class);
    @Mock
    private              SlotValServiceImpl slotValService;
    @InjectMocks
    private              Matcher            matcher;

    @Before
    public void setUp() {

    }

    @Test
    public void testExtractSlot1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Multimap<String, Integer> slotValCodeMap = ArrayListMultimap.create();
        slotValCodeMap.put("爸爸", 1);
        slotValCodeMap.put("爸", 1);
        slotValCodeMap.put("妈妈", 1);
        slotValCodeMap.put("哥哥", 1);
        slotValCodeMap.put("弟弟", 1);
        slotValCodeMap.put("姐姐", 1);
        slotValCodeMap.put("妹妹", 1);
        slotValCodeMap.put("叔叔", 1);
        slotValCodeMap.put("阿姨", 1);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : slotValCodeMap.entries()) {
            slotTrie.insert(entry.getKey());
        }

        Method method = Matcher.class.getDeclaredMethod("extractSlot", String.class, PatriciaTrie.class, Multimap.class);
        method.setAccessible(true);
        List<MatchedSlot> results = (List<MatchedSlot>) method.invoke(matcher, "爸爸妈妈爸爸妈妈哥哥弟弟", slotTrie, slotValCodeMap);
        Assert.assertEquals(10, results.size());
    }

    @Test
    public void testExtractSlot2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("上海", 2);
        map.put("北京", 2);
        map.put("上海", 3);
        map.put("北京", 3);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : map.entries()) {
            slotTrie.insert(entry.getKey());
        }

        Method method = Matcher.class.getDeclaredMethod("extractSlot", String.class, PatriciaTrie.class, Multimap.class);
        method.setAccessible(true);
        List<MatchedSlot> results = (List<MatchedSlot>) method.invoke(matcher, "订从北京到上海的机票", slotTrie, map);
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void testExtractSlot3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Multimap<String, Integer> slotValCodeMap = ArrayListMultimap.create();
        slotValCodeMap.put("论语", 15);
        slotValCodeMap.put("论语修身篇", 16);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : slotValCodeMap.entries()) {
            slotTrie.insert(entry.getKey());
        }

        Method method = Matcher.class.getDeclaredMethod("extractSlot", String.class, PatriciaTrie.class, Multimap.class);
        method.setAccessible(true);
        List<MatchedSlot> results = (List<MatchedSlot>) method.invoke(matcher, "播放论语修身篇", slotTrie, slotValCodeMap);
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void testParseInputTexts1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = new ArrayList<>();
        matchedSlots1.add(MatchedSlot.of(1, "爸", 0, 1));
        matchedSlots1.add(MatchedSlot.of(3, "爸爸", 0, 2));
        matchedSlots1.add(MatchedSlot.of(2, "爸", 1, 2));
        matchedSlots1.add(MatchedSlot.of(3, "妈妈", 2, 4));
        matchedSlots1.add(MatchedSlot.of(1, "爸", 4, 5));
        matchedSlots1.add(MatchedSlot.of(3, "爸爸", 4, 6));
        matchedSlots1.add(MatchedSlot.of(1, "爸", 5, 6));
        matchedSlots1.add(MatchedSlot.of(3, "妈妈", 6, 8));
        matchedSlots1.add(MatchedSlot.of(3, "哥哥", 8, 10));
        matchedSlots1.add(MatchedSlot.of(3, "弟弟", 10, 12));

        Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, "爸爸妈妈爸爸妈妈哥哥弟弟", matchedSlots1);
        Assert.assertEquals(46, results1.size());
    }

    @Test
    public void testParseInputTexts2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        Multimap<String, Integer> slotValCodeMap = ArrayListMultimap.create();
        slotValCodeMap.put("爸爸", 1);
        slotValCodeMap.put("爸", 1);
        slotValCodeMap.put("妈妈", 1);
        slotValCodeMap.put("妈", 1);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : slotValCodeMap.entries()) {
            slotTrie.insert(entry.getKey());
        }

        String inputText = "爸爸妈妈爸爸妈妈爸爸妈妈";
        Method extractSlotMethod = Matcher.class.getDeclaredMethod("extractSlot", String.class, PatriciaTrie.class, Multimap.class);
        extractSlotMethod.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = (List<MatchedSlot>) extractSlotMethod.invoke(matcher, inputText, slotTrie, slotValCodeMap);

        StopWatch watch = new StopWatch();
        watch.start();
        int requestCount = 50;
        int threadPoolSize = 1;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        IntStream.range(0, requestCount).forEach(i -> {
            threadPool.execute(() -> {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, inputText, matchedSlots1);
                    stopWatch.stop();
                    LOGGER.debug("parseInputTexts execution: {} ms", stopWatch.getTotalTimeMillis());
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

    @Test
    public void testParseInputTexts3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        Multimap<String, Integer> slotValCodeMap = ArrayListMultimap.create();
        slotValCodeMap.put("爸爸", 1);
        slotValCodeMap.put("爸", 1);
        slotValCodeMap.put("妈妈", 1);
        slotValCodeMap.put("妈", 1);
        slotValCodeMap.put("奶奶", 1);
        slotValCodeMap.put("奶", 1);
        slotValCodeMap.put("弟弟", 1);
        slotValCodeMap.put("弟", 1);
        slotValCodeMap.put("哥哥", 1);
        slotValCodeMap.put("哥", 1);
        slotValCodeMap.put("姐姐", 1);
        slotValCodeMap.put("姐", 1);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : slotValCodeMap.entries()) {
            slotTrie.insert(entry.getKey());
        }

        String inputText = "小爱同学爸爸的爸爸的妈妈的奶奶的弟弟的姐姐的哥哥是谁";
        Method extractSlotMethod = Matcher.class.getDeclaredMethod("extractSlot", String.class, PatriciaTrie.class, Multimap.class);
        extractSlotMethod.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = (List<MatchedSlot>) extractSlotMethod.invoke(matcher, inputText, slotTrie, slotValCodeMap);

        StopWatch watch = new StopWatch();
        watch.start();
        int requestCount = 50;
        int threadPoolSize = 1;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        IntStream.range(0, requestCount).forEach(i -> {
            threadPool.execute(() -> {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, inputText, matchedSlots1);
                    stopWatch.stop();
                    LOGGER.debug("parseInputTexts execution: {} ms", stopWatch.getTotalTimeMillis());
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

    @Test
    public void testParseInputTexts4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = new ArrayList<>();
        matchedSlots1.add(MatchedSlot.of(2, "北京", 2, 4));
        matchedSlots1.add(MatchedSlot.of(3, "北京", 2, 4));
        matchedSlots1.add(MatchedSlot.of(2, "上海", 5, 7));
        matchedSlots1.add(MatchedSlot.of(3, "上海", 5, 7));
        Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, "订从北京到上海的机票", matchedSlots1);
        Assert.assertEquals(6, results1.size());
    }

    @Test
    public void testParseInputTexts5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = new ArrayList<>();
        matchedSlots1.add(MatchedSlot.of(15, "论语", 2, 4));
        matchedSlots1.add(MatchedSlot.of(16, "论语修身篇", 2, 7));
        Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, "播放论语修身篇", matchedSlots1);
        Assert.assertEquals(2, results1.size());
        // MatchedSlot{code='15', value='论语', start=2, end=4}, MatchedSlot{code='16', value='论语修身篇', start=2, end=7} 这两个不可以放在一组
        Assert.assertNotNull(results1.stream().findFirst().get().getMatchedSlot());
    }

    @Test
    public void testParseInputTexts6() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = new ArrayList<>();
        matchedSlots1.add(MatchedSlot.of(42, "我心", 0, 2));
        matchedSlots1.add(MatchedSlot.of(42, "心情", 1, 3));
        matchedSlots1.add(MatchedSlot.of(42, "心情不好", 1, 5));
        matchedSlots1.add(MatchedSlot.of(387, "心情不好", 1, 5));
        matchedSlots1.add(MatchedSlot.of(42, "不好", 3, 5));
        matchedSlots1.add(MatchedSlot.of(348, "来首", 5, 7));
        Set<ParsedInputText> results1 = (Set<ParsedInputText>) method.invoke(matcher, "我心情不好来首歌", matchedSlots1);
        Assert.assertEquals(11, results1.size());
    }

    @Test
    public void testMatchSlotVal() {
        Multimap<String, Integer> slotValCodeMap = ArrayListMultimap.create();
        slotValCodeMap.put("爸爸", 1);
        slotValCodeMap.put("爸", 1);
        slotValCodeMap.put("妈妈", 1);
        slotValCodeMap.put("哥哥", 1);
        slotValCodeMap.put("弟弟", 1);
        slotValCodeMap.put("姐姐", 1);
        slotValCodeMap.put("妹妹", 1);
        slotValCodeMap.put("叔叔", 1);
        slotValCodeMap.put("阿姨", 1);
        Mockito.when(slotValService.getValCodeMap("dummy")).thenReturn(slotValCodeMap);

        PatriciaTrie slotTrie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : slotValCodeMap.entries()) {
            slotTrie.insert(entry.getKey());
        }
        Mockito.when(slotValService.getSlotValTrie("dummy")).thenReturn(slotTrie);

        Input input = new Input();
        input.setOriginalText("爸爸妈妈爸爸妈妈哥哥弟弟");
        input.setPreprocessedText("爸爸妈妈爸爸妈妈哥哥弟弟");
        input.setRequestId(RandomStringUtils.randomNumeric(5));

        Device device = new Device();
        device.setCompanyId("company1");
        device.setModelId("model1");
        device.addUserId("user1");
        input.setDevice(device);

        Output output = matcher.matchSlotVal(input, null, Lists.newArrayList("dummy"));
        System.out.println(output);
    }

    @Test
    public void testCreateList() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int len = 18;
        int bit = (0xFFFFFFFF >>> (32 - len));
        LOGGER.debug("Bit: {}", bit);
        for (int i = 1; i <= bit; i++) {
            List<MatchedSlot> slots = new ArrayList<>();
            slots.add(MatchedSlot.of(0, "test", 1, 2));
        }
        stopWatch.stop();
        LOGGER.debug("Elapsed: {} ms", stopWatch.getTotalTimeMillis());
    }
}
