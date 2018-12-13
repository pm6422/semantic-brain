package org.infinity.semanticbrain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.service.SemanticRecognitionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SemanticRecognitionServiceImpTest {

    @Autowired
    private SemanticRecognitionService semanticRecognitionService;

    @Before
    public void setUp() {

    }


    @Test
    public void testRecognize() throws JsonProcessingException {
        Input input = new Input();
        input.setText("今天@#¥33");
        Output output = semanticRecognitionService.recognize(input);
//        Assert.assertTrue(output.isRecognized());
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Output:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output));
    }
}

