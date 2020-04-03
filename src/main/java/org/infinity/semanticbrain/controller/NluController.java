package org.infinity.semanticbrain.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.service.NluService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "语义处理")
@RestController
public class NluController {

    @Autowired
    private NluService nluService;

    @ApiOperation(value = "意图识别", notes = "deviceId取值规则：不指定deviceId时使用登录用户名, userIds多值时使用英文逗号分割")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "处理成功")})
    @GetMapping(value = "/api/recognize-intent")
    public ResponseEntity<Output> recognizeIntent() {
        Input input = new Input();
        return ResponseEntity.ok(nluService.recognize(input));
    }
}
