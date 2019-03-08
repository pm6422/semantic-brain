package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel("意图结果")
public class Intent implements Serializable {
    private static final long serialVersionUID = 6996205094272356735L;

    // 控制类型值
    public static final String CONTROL_TYPE_CONFIRM       = "confirm";
    public static final String CONTROL_TYPE_CANCEL        = "cancel";
    public static final String CONTROL_TYPE_NEXT          = "next";
    public static final String CONTROL_TYPE_PREV          = "prev";
    public static final String CONTROL_TYPE_STOP          = "stop";
    public static final String CONTROL_TYPE_PAUSE         = "pause";
    public static final String CONTROL_TYPE_CONTINUE      = "continue";
    public static final String CONTROL_TYPE_SINGLE_REPEAT = "playSingleRepeat";

    // 回复类型值
    public static final String REPLY_TYPE_TASK_BASED_ANSWER = "taskBasedAnswer"; // 任务型答案类型
    public static final String REPLY_TYPE_QA_BASED_ANSWER   = "qaBasedAnswer"; // 问答型答案类型
    public static final String REPLY_TYPE_CHAT_BASED_ANSWER = "chatBasedAnswer"; // 闲聊型答案类型
    public static final String REPLY_TYPE_ASK               = "ask"; // 反问回复类型
    public static final String REPLY_TYPE_OPTIONS_ASK       = "optionsAsk"; // 选择性反问回复类型


    // 回复类型值
    public static final String COMMAND_SOURCE_SKILL_ASSOCIATION = "skillAssociation";
    public static final String COMMAND_SOURCE_USER_COMMAND      = "userCommand";

    @ApiModelProperty("技能代码")
    private String       skillCode     = "";
    @ApiModelProperty("技能名称")
    private String       skillName     = "";
    @ApiModelProperty("技能大分类")
    private String       skillCategory = "";
    @ApiModelProperty("技能小分类")
    private String       skillType     = "";
    @ApiModelProperty("命令代码")
    private String       commandCode   = "";
    @ApiModelProperty("命令名称")
    private String       commandName   = "";
    @ApiModelProperty("命令来源")
    private String       commandSource = "";// 取值范围见常量定义
    @ApiModelProperty("控制类型")
    private String       controlType   = "";// 控制类型的前提是要有上下文，而且本次意图解析结果需要保持上次命令，取值范围见常量定义
    @ApiModelProperty("匹配的规则，如：播放{Singer}的{Song}")
    private List<String> rules         = new ArrayList<>();
    @ApiModelProperty("槽位信息列表")
    private List<Slot>   slots         = new ArrayList<>();
    @ApiModelProperty("回复内容")
    private String       reply         = "";
    @ApiModelProperty("回复类型")
    private String       replyType     = "";// 取值范围见常量定义

    public Intent() {
    }

    public static Intent of(String skillCode, String skillName, String skillCategory, String skillType, String commandCode, String commandName, String commandSource,
                            String controlType, List<String> rules, List<Slot> slots, String reply, String replyType) {
        Intent intent = new Intent();
        intent.setSkillCode(skillCode);
        intent.setSkillName(skillName);
        intent.setSkillCategory(skillCategory);
        intent.setSkillType(skillType);
        intent.setCommandCode(commandCode);
        intent.setCommandName(commandName);
        intent.setCommandSource(commandSource);
        intent.setControlType(controlType);
        intent.setRules(rules);
        intent.setSlots(slots);
        intent.setReply(reply);
        intent.setReplyType(replyType);

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

    public String getCommandSource() {
        return commandSource;
    }

    public void setCommandSource(String commandSource) {
        this.commandSource = commandSource;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }


    /**
     * replyType == REPLY_TYPE_ASK时本方法才有用
     *
     * @return
     */
    public Slot findAskSlot() {
        return slots.stream().filter(x -> x.isAsked()).findFirst().orElse(null);
    }

    /**
     * replyType == REPLY_TYPE_OPTIONS_ASK时本方法才有用
     *
     * @return
     */
    public List<Slot> findOptionAskSlots() {
        return slots.stream().filter(x -> x.isOptionAsked()).collect(Collectors.toList());
    }

    public boolean isDetermined() {
        // 识别出命令代码并无反问槽位
        return StringUtils.isNotEmpty(commandCode) && this.findAskSlot() == null;
    }
}
