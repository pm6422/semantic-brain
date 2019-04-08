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
    public static final String TYPE_NEGATIVE    = "negative";// 否定类型，如：播放不是"刘德华"的歌

    @ApiModelProperty("槽位代码")
    private int     code;
    @ApiModelProperty("槽位名称")
    private String  name;
    @ApiModelProperty("槽位值")
    private String  value;
    @ApiModelProperty("槽位类型")// 取值范围见常量定义
    private String  type;
    @ApiModelProperty("是否为必要槽位")
    private boolean required;
    @ApiModelProperty("是否被反问")
    private boolean asked;
    @ApiModelProperty("反问槽位优先级") // 优先级高的优先反问
    private int     askPrecedence;
    @ApiModelProperty("是否为选择性反问")
    private boolean optionAsked;

    public Slot() {
    }

    public static Slot of(int code, String name, String value, String type, boolean required, boolean asked, int askPrecedence, boolean optionAsked) {
        Slot slot = new Slot();
        slot.setCode(code);
        slot.setName(name);
        slot.setValue(value);
        slot.setType(type);
        slot.setRequired(required);
        slot.setAsked(asked);
        slot.setAskPrecedence(askPrecedence);
        slot.setOptionAsked(optionAsked);

        return slot;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isAsked() {
        return asked;
    }

    public void setAsked(boolean asked) {
        this.asked = asked;
    }

    public int getAskPrecedence() {
        return askPrecedence;
    }

    public void setAskPrecedence(int askPrecedence) {
        this.askPrecedence = askPrecedence;
    }

    public boolean isOptionAsked() {
        return optionAsked;
    }

    public void setOptionAsked(boolean optionAsked) {
        this.optionAsked = optionAsked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return required == slot.required &&
                asked == slot.asked &&
                askPrecedence == slot.askPrecedence &&
                optionAsked == slot.optionAsked &&
                Objects.equals(code, slot.code) &&
                Objects.equals(name, slot.name) &&
                Objects.equals(value, slot.value) &&
                Objects.equals(type, slot.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, value, type, required, asked, askPrecedence, optionAsked);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", required=" + required +
                ", asked=" + asked +
                ", askPrecedence=" + askPrecedence +
                ", optionAsked=" + optionAsked +
                '}';
    }
}
