package org.infinity.semanticbrain.dialog.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ParsedInputText {

    private static final String            LEFT_BRACE  = "{";
    private static final String            RIGHT_BRACE = "}";
    private              String            slotFilledText; // 格式：买一张{21}到{22}的机票
    private              List<MatchedSlot> matchedSlots;

    public static ParsedInputText of(String inputText, List<MatchedSlot> matchedSlots) {
        ParsedInputText parsedInputText = new ParsedInputText();
        parsedInputText.setMatchedSlots(matchedSlots);

        char[] inputChars = inputText.toCharArray();
        StringBuilder slotFilledText = new StringBuilder();
        Iterator<MatchedSlot> matchedSlotIterator = matchedSlots.iterator();
        MatchedSlot matchedSlot = matchedSlotIterator.next();
        for (int i = 0; i < inputChars.length; ) {
            if (matchedSlot == null || matchedSlot != null && matchedSlot.getStart() > i) {
                slotFilledText.append(inputChars[i]);
                i++;
            } else {
                slotFilledText.append(LEFT_BRACE).append(matchedSlot.getCode()).append(RIGHT_BRACE);
                i += matchedSlot.getValue().length();
                if (matchedSlotIterator.hasNext()) {
                    matchedSlot = matchedSlotIterator.next();
                } else {
                    matchedSlot = null;
                }
            }
        }

        parsedInputText.setSlotFilledText(slotFilledText.toString());
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
