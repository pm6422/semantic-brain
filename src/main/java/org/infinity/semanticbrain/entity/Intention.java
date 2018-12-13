package org.infinity.semanticbrain.entity;

import java.io.Serializable;

public class Intention implements Serializable {

    private static final long   serialVersionUID = -1L;
    private              String skillCode;
    private              String skillName;
    private              String commandCode;
    private              String commandName;


    public static Intention of(String skillCode, String skillName, String commandCode, String commandName) {
        Intention intention = new Intention();
        intention.setSkillCode(skillCode);
        intention.setSkillName(skillName);
        intention.setCommandCode(commandCode);
        intention.setCommandName(commandName);
        return intention;
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
}
