package org.infinity.semanticbrain.dialog.intent;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.semanticbrain.dialog.entity.*;
import org.infinity.semanticbrain.domain.MatchRule;
import org.infinity.semanticbrain.service.MatchRuleService;
import org.infinity.semanticbrain.service.SlotValService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.trie4j.patricia.PatriciaTrie;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiPredicate;

@Component
public class Matcher {
    private static final Logger                                LOGGER             = LoggerFactory.getLogger(Matcher.class);
    @Autowired
    private              SlotValService                        slotValService;
    @Autowired
    private              MatchRuleService                      matchRuleService;
    // 非同一个slotCode且参数错位的情况下才可以放到同一个slots组合内
    private              BiPredicate<MatchedSlot, MatchedSlot> invalidCombination = (s1, s2) -> s1.getCode() == s2.getCode() || !(s1.getEnd() <= s2.getStart() || s1.getStart() >= s2.getEnd());

    public Output matchSlotVal(Input input, Output lastOutput, List<String> skillCodes) {
        Output output = new Output();
        for (String skillCode : skillCodes) {
            PatriciaTrie slotTrie = slotValService.getSlotValTrie(skillCode);
            Multimap<String, Integer> slotValCodeMap = slotValService.getValCodeMap(skillCode);
            if (slotTrie == null) {
                continue;
            }

            List<MatchedSlot> slots = this.extractSlot(input.getPreprocessedText(), slotTrie, slotValCodeMap);
            if (CollectionUtils.isEmpty(slots)) {
                return output;
            }
            Set<ParsedInputText> parsedInputTexts = this.parseInputTexts(input.getPreprocessedText(), slots);
//            this.matchRules(skillCode, input, lastOutput, parsedInputTexts);
        }


        return output;
    }

    /**
     * 使用槽位值前缀树对输入文本进行槽位提取并获得槽位在文本中的位置
     * 注意：返回结果保证没有重复
     *
     * @param inputText 用户输入文本
     * @return 提取的槽位结果，包含词和词在输入文本中的位置
     */
    private List<MatchedSlot> extractSlot(String inputText, PatriciaTrie slotTrie, Multimap<String, Integer> slotValCodeMap) {
        List<MatchedSlot> matchedSlots = new ArrayList<MatchedSlot>();
        for (int i = 0; i < inputText.length(); i++) {
            // 以用户输入文本的首字母+i位置开始循环的进行连续匹配槽位值
            Iterator<String> it = slotTrie.commonPrefixSearch(inputText.substring(i)).iterator();
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

    /**
     * 高效率全组合算法: https://www.jianshu.com/p/a6e3c980e932
     * 生成关键字全组合，算法说明：当n大于2时，n个数的全组合一共有(2^n)-1种，另外还需要去掉非法组合
     *
     * @param inputText    输入文本
     * @param matchedSlots 提取的槽位
     * @return 转换的输入文本
     */
    private Set<ParsedInputText> parseInputTexts(String inputText, List<MatchedSlot> matchedSlots) {
        Set<ParsedInputText> parsedInputTexts = new HashSet<>();
        StopWatch watch = new StopWatch();
        watch.start();

        int count = (0xFFFFFFFF >>> (32 - matchedSlots.size()));//(2^n)-1
        LOGGER.debug("Combination count: {}", count);
        for (int i = 1; i <= count; i++) {
            MatchedSlot slot = null;// 单个元素变量存储不下升级使用数组存储，创建数组非常消耗时间与资源，只有元素数量大于1时才需要数组存储
            List<MatchedSlot> slots = null;

            for (int j = 0; j < matchedSlots.size(); j++) {
                if ((i << (31 - j)) >> 31 == -1) {
                    if (slot == null && CollectionUtils.isEmpty(slots)) {
                        slot = matchedSlots.get(j);
                    } else {
                        boolean valid = true;
                        if (slot != null) {
                            if (invalidCombination.test(matchedSlots.get(j), slot)) {
                                valid = false;
                            }
                        } else {
                            for (MatchedSlot s : slots) {
                                if (invalidCombination.test(matchedSlots.get(j), s)) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                        if (valid) {
                            if (slots == null) {
                                slots = new ArrayList<MatchedSlot>();
                            }
                            if (slot != null) {
                                slots.add(slot);
                                slot = null;
                            }
                            // 数组内保证不会有重复数据
                            slots.add(matchedSlots.get(j));
                        }
                    }
                }
            }

            // ArrayList.contains性能太差
            // Refer https://blog.csdn.net/liu_005/article/details/80850171
            if (slot != null) {
                parsedInputTexts.add(ParsedInputText.of(inputText, slot));
            } else {
                parsedInputTexts.add(ParsedInputText.of(inputText, slots));
            }
        }
        watch.stop();
        LOGGER.debug("parseInputTexts execution: {} ms", watch.getTotalTimeMillis());
        return parsedInputTexts;
    }

    private Output matchRules(String skillCode, Input input, Output lastOutput, Set<ParsedInputText> parsedInputTexts) {
        Output higherScoreOutput = null;
        BigDecimal higherScore = new BigDecimal(0);
        List<ParsedInputText> highScoreParsedInputTexts = new ArrayList<ParsedInputText>();
        List<MatchRule> highScoreRules = new ArrayList<MatchRule>();
        for (ParsedInputText parsedInputText : parsedInputTexts) {
            List<MatchRule> matchRules = matchRuleService.find(skillCode);
            if (CollectionUtils.isEmpty(matchRules)) {
                continue;
            }
            for (MatchRule matchRule : matchRules) {
                if (parsedInputText.getSlotFilledText().length() < matchRule.getRule().length()) {
                    // 用户输入文本长度如果小于句式长度肯定不会匹配上
                    continue;
                }
                if (parsedInputText.getSlotFilledText().equals(matchRule.getRule())) {
                    // 用户输入文本完全匹配则获得最高分
                    higherScore = Output.TOP_SCORE;
                    highScoreParsedInputTexts.add(parsedInputText);
                    highScoreRules.add(matchRule);
                }
            }

        }
        if (CollectionUtils.isNotEmpty(highScoreParsedInputTexts)) {
            List<Intent> intents = new ArrayList<>();
            for (int i = 0; i < highScoreParsedInputTexts.size(); i++) {
                List<Slot> slots = new ArrayList<>();
                highScoreParsedInputTexts.get(i).getMatchedSlots().forEach(matchedSlot -> {
                    slots.add(Slot.of(matchedSlot.getCode(), "", matchedSlot.getValue(), "", true, true, 0, false));
                });
                intents.add(Intent.of(skillCode, "", "", "", "", highScoreRules.get(i).getRule(), slots, "", ""));
            }
            higherScoreOutput = new Output(input, intents, higherScore);
        }
        return lastOutput;
    }
}
