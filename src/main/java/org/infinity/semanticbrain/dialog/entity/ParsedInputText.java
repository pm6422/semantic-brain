package org.infinity.semanticbrain.dialog.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ParsedInputText {

    private static final String            LEFT_BRACE  = "{";
    private static final String            RIGHT_BRACE = "}";
    private              String            slotFilledText; // 格式：买一张{21}到{22}的机票
    private              MatchedSlot       matchedSlot;// 单个元素变量存储不下升级使用数组存储，创建数组非常消耗时间与资源，只有元素数量大于1时才需要数组存储
    private              List<MatchedSlot> matchedSlots;// 单个元素变量存储不下升级使用数组存储，创建数组非常消耗时间与资源，只有元素数量大于1时才需要数组存储

    /**
     * @param inputText   输入文本
     * @param matchedSlot 匹配的槽位
     * @return ParsedInputText实例
     */
    public static ParsedInputText of(String inputText, MatchedSlot matchedSlot) {
        ParsedInputText parsedInputText = new ParsedInputText();
        parsedInputText.setMatchedSlot(matchedSlot);

        char[] inputChars = inputText.toCharArray();
        StringBuilder slotFilledText = new StringBuilder();
        boolean slotAdded = false;
        for (int i = 0; i < inputChars.length; ) {
            if (matchedSlot.getStart() > i || slotAdded) {
                slotFilledText.append(inputChars[i]);
                i++;
            } else {
                slotFilledText.append(LEFT_BRACE).append(matchedSlot.getCode()).append(RIGHT_BRACE);
                i += matchedSlot.getValue().length();
                slotAdded = true;
            }
        }

        parsedInputText.setSlotFilledText(slotFilledText.toString());
        return parsedInputText;
    }

    /**
     * @param inputText    输入文本
     * @param matchedSlots matchedSlot必须按照索引先后排序
     * @return ParsedInputText实例
     */
    public static ParsedInputText of(String inputText, List<MatchedSlot> matchedSlots) {
        ParsedInputText parsedInputText = new ParsedInputText();
        parsedInputText.setMatchedSlots(matchedSlots);
        char[] inputChars = inputText.toCharArray();
        StringBuilder slotFilledText = new StringBuilder();
        Iterator<MatchedSlot> matchedSlotIterator = matchedSlots.iterator();
        MatchedSlot matchedSlot = matchedSlotIterator.next();
        int maxStart = matchedSlot.getStart();
        for (int i = 0; i < inputChars.length; ) {
            if (matchedSlot == null || matchedSlot != null && matchedSlot.getStart() > i) {
                slotFilledText.append(inputChars[i]);
                i++;
            } else {
                slotFilledText.append(LEFT_BRACE).append(matchedSlot.getCode()).append(RIGHT_BRACE);
                i += matchedSlot.getValue().length();
                if (matchedSlotIterator.hasNext()) {
                    matchedSlot = matchedSlotIterator.next();
                    if (matchedSlot.getStart() < maxStart) {
                        throw new RuntimeException("matchedSlots must be sorted.");
                    }
                    maxStart = matchedSlot.getStart();
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

    public MatchedSlot getMatchedSlot() {
        return matchedSlot;
    }

    public void setMatchedSlot(MatchedSlot matchedSlot) {
        this.matchedSlot = matchedSlot;
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
        return Objects.equals(matchedSlot, that.matchedSlot) &&
                Objects.equals(matchedSlots, that.matchedSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchedSlot, matchedSlots);
    }

    @Override
    public String toString() {
        return "ParsedInputText{" +
                "slotFilledText='" + slotFilledText + '\'' +
                ", matchedSlot=" + matchedSlot +
                ", matchedSlots=" + matchedSlots +
                '}';
    }
}
