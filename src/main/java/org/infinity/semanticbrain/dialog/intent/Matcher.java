package org.infinity.semanticbrain.dialog.intent;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.MatchedSlot;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.entity.ParsedInputText;
import org.infinity.semanticbrain.service.SlotValService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trie4j.patricia.PatriciaTrie;

import java.util.*;

@Component
public class Matcher {
    private static final Logger         LOGGER = LoggerFactory.getLogger(Matcher.class);
    @Autowired
    private              SlotValService slotValService;

    public Output matchSlotVal(Input input, Output lastOutput) {
        Output output = new Output();
        List<MatchedSlot> slots = this.extractSlot(input.getPreprocessedText());
        if (CollectionUtils.isEmpty(slots)) {
            return output;
        }
        List<ParsedInputText> parsedInputTexts = this.parseInputTexts(input.getPreprocessedText(), slots);

        return null;
    }

    /**
     * 使用槽位值前缀树对输入文本进行槽位提取并获得槽位在文本中的位置
     * 注意：返回结果保证没有重复
     *
     * @param inputText 用户输入文本
     * @return 提取的槽位结果，包含词和词在输入文本中的位置
     */
    private List<MatchedSlot> extractSlot(String inputText) {
        PatriciaTrie trie = slotValService.getSlotValTrie();
        List<MatchedSlot> matchedSlots = new ArrayList<MatchedSlot>();
        Multimap<String, Integer> slotValCodeMap = slotValService.getValCodeMap();
        for (int i = 0; i < inputText.length(); i++) {
            // 以用户输入文本的首字母+i位置开始循环的进行连续匹配槽位值
            Iterator<String> it = trie.commonPrefixSearch(inputText.substring(i)).iterator();
            while (it.hasNext()) {
                String slotVal = it.next();
                Collection<Integer> slotCodes = slotValCodeMap.get(slotVal);
                for (Integer slotCode : slotCodes) {
                    MatchedSlot of = MatchedSlot.of(slotCode, slotVal, i, i + slotVal.length());
                    if (matchedSlots.contains(of)) {
                        throw new RuntimeException("Duplicated element bug found" + of);
                    } else {
                        matchedSlots.add(of);
                    }
                }
            }
        }
        return matchedSlots;
    }

    private List<ParsedInputText> parseInputTexts(String inputText, List<MatchedSlot> matchedSlots) {
        List<ParsedInputText> parsedInputTexts = new ArrayList<>();

        // 高效率全组合算法: http://mtnt2008.iteye.com/blog/715104
        // 生成关键字全组合，算法说明：当n大于2时，n个数的全组合一共有(2^n)-1种
        int bit = (0xFFFFFFFF >>> (32 - matchedSlots.size()));
        for (int i = 1; i <= bit; i++) {
            Set<MatchedSlot> slots = new HashSet<MatchedSlot>();
            for (int j = 0; j < matchedSlots.size(); j++) {
                if ((i << (31 - j)) >> 31 == -1) {
                    if (CollectionUtils.isEmpty(slots)) {
                        slots.add(matchedSlots.get(j));
                    } else {
                        boolean sameOrContainOrOverlap = false;
                        for (MatchedSlot slot : slots) {
                            // 参数值相同而参数代码不同则不可以放到同一个slots内
                            // 有参数值字符overlap情况的参数不可以放到同一个slots内，如：
                            // MatchedArg = { argCode: arg42, argValue: 我心, start: 0, end: 1 },
                            // MatchedArg = { argCode: arg387, argValue: 心情不好, start: 1, end: 4 }
                            if (// 相等
                                    slot.getValue().equals(matchedSlots.get(j).getValue())
                                            && slot.getStart() == matchedSlots.get(j).getStart()
                                            && slot.getEnd() == matchedSlots.get(j).getEnd()
                                            // 包含
                                            || !slot.getValue().equals(matchedSlots.get(j).getValue())
                                            && slot.getValue().contains(matchedSlots.get(j).getValue())
                                            && slot.getStart() <= matchedSlots.get(j).getStart()
                                            && slot.getEnd() >= matchedSlots.get(j).getEnd()
                                            // 包含
                                            || !slot.getValue().equals(matchedSlots.get(j).getValue())
                                            && matchedSlots.get(j).getValue().contains(slot.getValue())
                                            && matchedSlots.get(j).getStart() <= slot.getStart()
                                            && matchedSlots.get(j).getEnd() >= slot.getEnd()
                                            // 相交
                                            || slot.getStart() > matchedSlots.get(j).getStart()
                                            && slot.getEnd() > matchedSlots.get(j).getEnd()
                                            && slot.getStart() <= matchedSlots.get(j).getEnd()
                                            // 相交
                                            || slot.getStart() < matchedSlots.get(j).getStart()
                                            && slot.getEnd() < matchedSlots.get(j).getEnd()
                                            && slot.getEnd() >= matchedSlots.get(j).getStart()) {
                                sameOrContainOrOverlap = true;
                                break;
                            }
                        }
                        if (!sameOrContainOrOverlap) {
                            slots.add(matchedSlots.get(j));
                        }
                    }
                }
            }

            ParsedInputText of = ParsedInputText.of(inputText, slots);
            if (parsedInputTexts.contains(of)) {
//                throw new RuntimeException("Duplicated element bug found" + of);
            } else {
                parsedInputTexts.add(of);
            }
        }

        return parsedInputTexts;
    }
}
