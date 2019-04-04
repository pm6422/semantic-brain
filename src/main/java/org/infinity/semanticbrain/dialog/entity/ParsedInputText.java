package org.infinity.semanticbrain.dialog.entity;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ParsedInputText {

    private static final String           LEFT_BRACE  = "{";
    private static final String           RIGHT_BRACE = "}";
    private              String           slotFilledText; // 格式：买一张{21}到{22}的机票
    private              Set<MatchedSlot> matchedSlots;

    public static ParsedInputText of(String inputText, Set<MatchedSlot> matchedSlots) {
        ParsedInputText parsedInputText = new ParsedInputText();
        Map<Integer, String> indexMap = new TreeMap<Integer, String>();// Using ordered map
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

    public Set<MatchedSlot> getMatchedSlots() {
        return matchedSlots;
    }

    public void setMatchedSlots(Set<MatchedSlot> matchedSlots) {
        this.matchedSlots = matchedSlots;
    }


    @Override
    public String toString() {
        return "ParsedInputText{" +
                "slotFilledText='" + slotFilledText + '\'' +
                ", matchedSlots=" + matchedSlots +
                '}';
    }
}
