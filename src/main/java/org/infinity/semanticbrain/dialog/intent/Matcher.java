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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class Matcher {
    private static final Logger           LOGGER = LoggerFactory.getLogger(Matcher.class);
    @Autowired
    private              SlotValService   slotValService;
    @Autowired
    private              MatchRuleService matchRuleService;

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
            List<ParsedInputText> parsedInputTexts = this.parseInputTexts(input.getPreprocessedText(), slots);
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
    private List<ParsedInputText> parseInputTexts(String inputText, List<MatchedSlot> matchedSlots) {
        List<ParsedInputText> parsedInputTexts = new ArrayList<>();
        int count = 0;
        StopWatch watch = new StopWatch();
        watch.start();

        int bit = (0xFFFFFFFF >>> (32 - matchedSlots.size()));
        for (int i = 1; i <= bit; i++) {
            List<MatchedSlot> slots = new ArrayList<>();
            for (int j = 0; j < matchedSlots.size(); j++) {
                count++;
                if ((i << (31 - j)) >> 31 == -1) {
                    if (CollectionUtils.isEmpty(slots)) {
                        slots.add(matchedSlots.get(j));
                    } else {
                        boolean valid = true;
                        for (MatchedSlot slot : slots) {
                            // 参数错位的情况下才可以放到同一个slots组合内
                            if (!(matchedSlots.get(j).getEnd() <= slot.getStart() || matchedSlots.get(j).getStart() >= slot.getEnd())) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            if (slots.contains(matchedSlots.get(j))) {
                                throw new RuntimeException("Duplicated element bug found" + matchedSlots.get(j));
                            } else {
                                slots.add(matchedSlots.get(j));
                            }
                        }
                    }
                }
            }

            ParsedInputText of = ParsedInputText.of(inputText, slots);
            if (!parsedInputTexts.contains(of)) {
                parsedInputTexts.add(of);
            }
        }
        watch.stop();
        LOGGER.debug("Combination loop count: {}", count);
        LOGGER.debug("parseInputTexts execution: {}ms", watch.getTotalTimeMillis());
        return parsedInputTexts;
    }

    private Output matchRules(String skillCode, Input input, Output lastOutput, List<ParsedInputText> parsedInputTexts) {
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
