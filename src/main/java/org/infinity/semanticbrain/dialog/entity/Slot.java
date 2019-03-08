package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

@ApiModel("意图识别槽位")
public class Slot implements Serializable {
    private static final long serialVersionUID = 6996205094272648344L;

    // 槽位类型值
    public static final String TYPE_AFFIRMATIVE = "affirmative";// 肯定类型
    public static final String TYPE_NEGATIVE    = "negative";// 否定类型，如："不要"买北京到上海的机票

    @ApiModelProperty("槽位代码")
    private String  code;
    @ApiModelProperty("槽位名称")
    private String  name;
    @ApiModelProperty("槽位值")
    private String  value;
    @ApiModelProperty("槽位类型")// 取值范围见常量定义
    private String  type;
    @ApiModelProperty("反问槽位优先级") // 优先级高的优先反问
    private int     askPrecedence;
    @ApiModelProperty("是否为必要槽位")
    private boolean required;

    public Slot() {
    }

    public static Slot of(String code, String name, String value, String type, int askPrecedence, boolean required) {
        Slot slot = new Slot();
        slot.setCode(code);
        slot.setName(name);
        slot.setValue(value);
        slot.setType(type);
        slot.setAskPrecedence(askPrecedence);
        slot.setRequired(required);
        return slot;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAskPrecedence() {
        return askPrecedence;
    }

    public void setAskPrecedence(int askPrecedence) {
        this.askPrecedence = askPrecedence;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return askPrecedence == slot.askPrecedence &&
                required == slot.required &&
                Objects.equals(code, slot.code) &&
                Objects.equals(name, slot.name) &&
                Objects.equals(value, slot.value) &&
                Objects.equals(type, slot.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, value, type, askPrecedence, required);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", askPrecedence=" + askPrecedence +
                ", required=" + required +
                '}';
    }
}
