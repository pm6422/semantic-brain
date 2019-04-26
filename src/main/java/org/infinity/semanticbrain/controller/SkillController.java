package org.infinity.semanticbrain.controller;

import io.swagger.annotations.*;
import org.infinity.semanticbrain.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController
@Api(tags = "技能信息")
public class SkillController {

    private static final Logger       LOGGER = LoggerFactory.getLogger(SkillController.class);
    @Autowired
    private              SkillService skillService;

    @ApiOperation(value = "根据技能ID删除技能信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"), @ApiResponse(code = SC_BAD_REQUEST, message = "技能不存在")})
    @GetMapping("/open-api/skill/skills/{id}")
    public void delete(@ApiParam(value = "技能ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete dict: {}", id);
        skillService.delete("dummy");
    }
}
