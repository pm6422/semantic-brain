package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Intent class
 * Note: Serialization friendly class
 */
public class Intent implements Serializable {
    private static final long serialVersionUID = 6996205094272356735L;

    public static final String CONTROL_TYPE_CONFIRM       = "confirm";
    public static final String CONTROL_TYPE_CANCEL        = "cancel";
    public static final String CONTROL_TYPE_NEXT          = "next";
    public static final String CONTROL_TYPE_PREV          = "prev";
    public static final String CONTROL_TYPE_STOP          = "stop";
    public static final String CONTROL_TYPE_PAUSE         = "pause";
    public static final String CONTROL_TYPE_CONTINUE      = "continue";
    public static final String CONTROL_TYPE_SINGLE_REPEAT = "playSingleRepeat";

    @ApiModelProperty("功能名称")
    private String skillCode;
    private String skillName;
    private String skillCategory;
    private String skillType;
    private String commandCode;
    private String commandName;
    // 控制类型的前提是要有上下文，而且本次意图解析结果需要保持上次命令，取值范围见常量定义
    private String controlType;

    public static Intent of(String skillCode, String skillName, String skillCategory, String skillType, String commandCode, String commandName) {
        Intent intent = new Intent();
        intent.setSkillCode(skillCode);
        intent.setSkillName(skillName);
        intent.setSkillCategory(skillCategory);
        intent.setSkillType(skillType);
        intent.setCommandCode(commandCode);
        intent.setCommandName(commandName);
        return intent;
    }

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }
}
