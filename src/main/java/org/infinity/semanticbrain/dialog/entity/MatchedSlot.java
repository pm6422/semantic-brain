package org.infinity.semanticbrain.dialog.entity;

import java.util.Objects;

public class MatchedSlot {
    private int    code;
    private String value;
    private int    startIndex;
    private int    endIndex;

    public static MatchedSlot of(int code, String value, int startIndex, int endIndex) {
        MatchedSlot slot = new MatchedSlot();
        slot.setCode(code);
        slot.setValue(value);
        slot.setStartIndex(startIndex);
        slot.setEndIndex(endIndex);
        return slot;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchedSlot that = (MatchedSlot) o;
        return code == that.code &&
                startIndex == that.startIndex &&
                endIndex == that.endIndex &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, value, startIndex, endIndex);
    }

    @Override
    public String toString() {
        return "MatchedSlot{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
