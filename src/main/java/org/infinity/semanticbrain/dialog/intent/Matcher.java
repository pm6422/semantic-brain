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
    // 非同一个code且参数错位的情况下才可以放到同一个slots组合内
    private              BiPredicate<MatchedSlot, MatchedSlot> invalidCombination = (s1, s2) -> s1.getCode() == s2.getCode() || !(s1.getEnd() <= s2.getStart() || s1.getStart() >= s2.getEnd());

    public Output matchSlotVal(Input input, Output lastOutput, List<String> skillCodes) {
        Output output = new Output();
        Map<List<MatchedSlot>, Set<ParsedInputText>> parsedInputTextCacheMap = new HashMap<>(); // 用于在不同skill之间缓存
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
            Set<ParsedInputText> parsedInputTexts = this.getCachedParseInputTexts(parsedInputTextCacheMap, input.getPreprocessedText(), slots);
            this.matchRules(skillCode, input, lastOutput, parsedInputTexts);
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
     * 当matchedSlots的size比较大的时候parseInputTexts的组合种类会非常大，因此会非常消耗时间。
     * 使用parsedInputTextCacheMap可以作为同一个解析的槽位列表在不同skill之间的缓存
     * 同一个matchedSlots对应同一个parsedInputTexts
     *
     * @param parsedInputTextCacheMap
     * @param inputText
     * @param matchedSlots
     * @return
     */
    private Set<ParsedInputText> getCachedParseInputTexts(Map<List<MatchedSlot>, Set<ParsedInputText>> parsedInputTextCacheMap, String inputText, List<MatchedSlot> matchedSlots) {
        // 只有大组合才需要缓存，如果将小组合放进缓存中会导致缓存超大
        if (matchedSlots.size() <= 4) {
            return this.parseInputTexts(inputText, matchedSlots);
        }

        // 先判断containKey，然后执行get方法时都会调用hashCode()，直接调用get()可以省略一次hashCode()的计算，可以节约计算资源
        Set<ParsedInputText> cachedParsedInputTexts = parsedInputTextCacheMap.get(matchedSlots);
        if (CollectionUtils.isNotEmpty(cachedParsedInputTexts)) {
            return cachedParsedInputTexts;
        }

        cachedParsedInputTexts = this.parseInputTexts(inputText, matchedSlots);
        parsedInputTextCacheMap.put(matchedSlots, cachedParsedInputTexts);
        return cachedParsedInputTexts;
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

        int count = (0xFFFFFFFF >>> (32 - matchedSlots.size()));//(2^n)-1
        LOGGER.debug("Slot combination: {}", count);
        for (int i = 1; i <= count; i++) {
            MatchedSlot slot = null;// 单个元素变量存储不下升级使用数组存储，创建数组非常消耗时间与资源，只有元素数量大于1时才需要数组存储
            List<MatchedSlot> slots = null;

            for (int j = 0; j < matchedSlots.size(); j++) {
                if ((i << (31 - j)) >> 31 == -1) {
                    if (slot == null && CollectionUtils.isEmpty(slots)) {
                        slot = matchedSlots.get(j);
                    } else {
                        boolean invalid;
                        if (slot != null) {
                            invalid = invalidCombination.test(matchedSlots.get(j), slot);
                        } else {
                            final int tempJ = j;
                            invalid = slots.stream().filter(s -> invalidCombination.test(matchedSlots.get(tempJ), s)).findAny().isPresent();
                        }
                        if (!invalid) {
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

            // ArrayList.contains性能太差，所以使用Set去重
            if (slot != null) {
                parsedInputTexts.add(ParsedInputText.of(inputText, slot));
            } else {
                parsedInputTexts.add(ParsedInputText.of(inputText, slots));
            }
        }
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
