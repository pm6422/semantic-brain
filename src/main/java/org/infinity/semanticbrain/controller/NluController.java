package org.infinity.semanticbrain.controller;

import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.service.NluService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NluController {

    @Autowired
    private NluService nluService;

    @GetMapping(value = "/open-api/semantic-recognize")
    public ResponseEntity<Output> semanticRecognize() {
        Input input = new Input();
        return ResponseEntity.ok(nluService.recognize(input));
    }
}
