package org.infinity.semanticbrain.intent;

import org.apache.commons.lang3.RandomStringUtils;
import org.infinity.semanticbrain.dialog.entity.Device;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.intent.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
