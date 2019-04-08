package org.infinity.semanticbrain.domain;

import java.io.Serializable;

//@Document(collection = "MatchRule")
public class MatchRule implements Serializable {

    private static final long serialVersionUID = -1724006418619378L;

    private String skillCode;
    private String rule; // 格式: 订从{2}到{3}的机票
    private String regexRule; // 格式: 订从{(.+)}到{(.+)}的机票
    private float  score;
    private String type;

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRegexRule() {
        return regexRule;
    }

    public void setRegexRule(String regexRule) {
        this.regexRule = regexRule;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
