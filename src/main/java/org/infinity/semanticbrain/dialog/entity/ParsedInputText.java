package org.infinity.semanticbrain.dialog.entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ParsedInputText {

    private static final String            LEFT_BRACE  = "{";
    private static final String            RIGHT_BRACE = "}";
    private              String            slotFilledText; // 格式：买一张{21}到{22}的机票
    private              List<MatchedSlot> matchedSlots;

    public static ParsedInputText of(String inputText, List<MatchedSlot> matchedSlots) {
        ParsedInputText parsedInputText = new ParsedInputText();
        Map<Integer, String> indexMap = new TreeMap<Integer, String>();// A map ordered by key
        char[] inputChars = inputText.toCharArray();
        for (int i = 0; i < inputChars.length; i++) {
            indexMap.put(i, String.valueOf(inputChars[i]));
        }
        for (MatchedSlot slot : matchedSlots) {
            for (int i = slot.getStart(); i < slot.getEnd(); i++) {
                // 删除slot值部分
                indexMap.remove(i);
            }
            // 添加slot代码部分
            indexMap.put(slot.getStart(), LEFT_BRACE + slot.getCode() + RIGHT_BRACE);
        }

        StringBuilder sb = new StringBuilder();
        indexMap.forEach((k, v) -> sb.append(v));
        parsedInputText.setSlotFilledText(sb.toString());
        parsedInputText.setMatchedSlots(matchedSlots);
        return parsedInputText;
    }

    public String getSlotFilledText() {
        return slotFilledText;
    }

    public void setSlotFilledText(String slotFilledText) {
        this.slotFilledText = slotFilledText;
    }

    public List<MatchedSlot> getMatchedSlots() {
        return matchedSlots;
    }

    public void setMatchedSlots(List<MatchedSlot> matchedSlots) {
        this.matchedSlots = matchedSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedInputText that = (ParsedInputText) o;
        return Objects.equals(slotFilledText, that.slotFilledText) &&
                Objects.equals(matchedSlots, that.matchedSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotFilledText, matchedSlots);
    }

    @Override
    public String toString() {
        return "ParsedInputText{" +
                "slotFilledText='" + slotFilledText + '\'' +
                ", matchedSlots=" + matchedSlots +
                '}';
    }
}
