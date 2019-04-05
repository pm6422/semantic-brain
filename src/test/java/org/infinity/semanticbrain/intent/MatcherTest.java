package org.infinity.semanticbrain.intent;

import org.apache.commons.lang3.RandomStringUtils;
import org.infinity.semanticbrain.dialog.entity.*;
import org.infinity.semanticbrain.dialog.intent.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MatcherTest {

    @Autowired
    private Matcher matcher;

    @Before
    public void setUp() {

    }

    @Test
    public void testExtractSlot() {
        Input input = new Input();
        input.setOriginalText("爸爸妈妈爸爸妈妈哥哥弟弟");
        input.setPreprocessedText("爸爸妈妈爸爸妈妈哥哥弟弟");
        input.setRequestId(RandomStringUtils.randomNumeric(5));

        Device device = new Device();
        device.setCompanyId("company1");
        device.setModelId("model1");
        device.addUserId("user1");
        input.setDevice(device);

        Output output = matcher.matchSlotVal(input, null);
        System.out.println(output);
    }

    @Test
    public void testParseInputTexts() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Matcher.class.getDeclaredMethod("parseInputTexts", String.class, List.class);
        method.setAccessible(true);
        List<MatchedSlot> matchedSlots = new ArrayList<>();
        matchedSlots.add(MatchedSlot.of(2, "北京", 2, 4));
        matchedSlots.add(MatchedSlot.of(3, "北京", 2, 4));
        matchedSlots.add(MatchedSlot.of(2, "上海", 5, 7));
        matchedSlots.add(MatchedSlot.of(3, "上海", 5, 7));
        List<ParsedInputText> results = (List<ParsedInputText>) method.invoke(matcher, "订从北京到上海的机票", matchedSlots);
        Assert.assertEquals(8, results.size());
    }
}
