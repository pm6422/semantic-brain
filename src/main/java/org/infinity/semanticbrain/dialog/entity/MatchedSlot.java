package org.infinity.semanticbrain.dialog.entity;

import java.util.Objects;

public class MatchedSlot {
    private int    code;
    private String value;
    private int    start;
    private int    end;

    public static MatchedSlot of(int code, String value, int start, int end) {
        MatchedSlot slot = new MatchedSlot();
        slot.setCode(code);
        slot.setValue(value);
        slot.setStart(start);
        slot.setEnd(end);
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchedSlot that = (MatchedSlot) o;
        return code == that.code &&
                start == that.start &&
                end == that.end &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, value, start, end);
    }

    @Override
    public String toString() {
        return "MatchedSlot{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
