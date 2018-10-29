package org.infinity.semanticbrain.entity;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Intention implements Serializable {

    private static final long   serialVersionUID = -1L;
    private              String commandCode;
    private              String commandName;
    private              String skillCode;
    private              String skillName;

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

    public boolean isRecognized() {
        return isNotEmpty(commandCode);
    }
}
