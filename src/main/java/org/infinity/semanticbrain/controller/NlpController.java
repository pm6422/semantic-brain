package org.infinity.semanticbrain.controller;

import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.service.SemanticRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NlpController {

    @Autowired
    private SemanticRecognitionService semanticRecognitionService;

    @RequestMapping(value = "/open-api/semantic-recognize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Output> semanticRecognize() {
        Input input = new Input();
        return ResponseEntity.ok(semanticRecognitionService.recognize(input));
    }
}
