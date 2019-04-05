package org.infinity.semanticbrain.intent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.infinity.semanticbrain.dialog.entity.MatchedSlot;
import org.infinity.semanticbrain.dialog.entity.ParsedInputText;
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
import org.trie4j.patricia.PatriciaTrie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MatcherTest {

    @Mock
    private SlotValServiceImpl slotValService;
    @InjectMocks
    private Matcher            matcher;

    @Before
    public void setUp() {

    }

    @Test
    public void testExtractSlot() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("爸爸", 1);
        map.put("爸", 1);
        map.put("妈妈", 1);
        map.put("哥哥", 1);
        map.put("弟弟", 1);
        map.put("姐姐", 1);
        map.put("妹妹", 1);
        map.put("叔叔", 1);
        map.put("阿姨", 1);
        Mockito.when(slotValService.getValCodeMap()).thenReturn(map);

        PatriciaTrie trie = new PatriciaTrie();
        for (Map.Entry<String, Integer> entry : map.entries()) {
            trie.insert(entry.getKey());
        }
        Mockito.when(slotValService.getSlotValTrie()).thenReturn(trie);

        Method method = Matcher.class.getDeclaredMethod("extractSlot", String.class);
        method.setAccessible(true);
        List<MatchedSlot> results = (List<MatchedSlot>) method.invoke(matcher, "爸爸妈妈爸爸妈妈哥哥弟弟");
        Assert.assertEquals(10, results.size());
    }

    @Test
    public void testParseInputTexts() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots1 = new ArrayList<>();
        matchedSlots1.add(MatchedSlot.of(2, "北京", 2, 4));
        matchedSlots1.add(MatchedSlot.of(3, "北京", 2, 4));
        matchedSlots1.add(MatchedSlot.of(2, "上海", 5, 7));
        matchedSlots1.add(MatchedSlot.of(3, "上海", 5, 7));
        List<ParsedInputText> results1 = (List<ParsedInputText>) method.invoke(matcher, "订从北京到上海的机票", matchedSlots1);
        Assert.assertEquals(8, results1.size());
    }

//    @Test
//    public void testMatchSlotVal() {
//        Input input = new Input();
//        input.setOriginalText("爸爸妈妈爸爸妈妈哥哥弟弟");
//        input.setPreprocessedText("爸爸妈妈爸爸妈妈哥哥弟弟");
//        input.setRequestId(RandomStringUtils.randomNumeric(5));
//
//        Device device = new Device();
//        device.setCompanyId("company1");
//        device.setModelId("model1");
//        device.addUserId("user1");
//        input.setDevice(device);
//
//        Output output = matcher.matchSlotVal(input, null);
//        System.out.println(output);
//    }
}
