package org.infinity.semanticbrain.dialog.intent;

import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.MatchedSlot;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.entity.ParsedInputText;
import org.infinity.semanticbrain.service.SlotValService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trie4j.patricia.PatriciaTrie;

import java.util.*;

@Component
public class Matcher {
    @Autowired
    private SlotValService slotValService;

    public Output matchSlotVal(Input input, Output lastOutput) {
        PatriciaTrie trie = slotValService.getSlotValTrie();
        Multimap<String, Integer> slotValCodesMap = slotValService.getValCodesMap();
        List<MatchedSlot> matchedSlots = this.extractSlot(input.getPreprocessedText(), trie, slotValCodesMap);
        Set<ParsedInputText> parsedInputTexts = this.parseInputTexts(input.getPreprocessedText(), matchedSlots);

        return null;
    }

    /**
     * 使用槽位值前缀树对输入文本进行槽位提取并获得槽位在文本中的位置
     *
     * @param inputText 用户输入文本
     * @param trie      槽位值前缀树
     * @return 提取的槽位结果，包含词和词在输入文本中的位置
     */
    private List<MatchedSlot> extractSlot(String inputText, PatriciaTrie trie, Multimap<String, Integer> slotValCodesMap) {
        Set<MatchedSlot> matchedSlots = new HashSet<MatchedSlot>();
        for (int i = 0; i < inputText.length(); i++) {
            // 以用户输入文本的首字母+i位置开始循环的进行连续匹配槽位值
            Iterator<String> it = trie.commonPrefixSearch(inputText.substring(i)).iterator();
            while (it.hasNext()) {
                String argValue = it.next();
                Collection<Integer> argCodes = slotValCodesMap.get(argValue);
                for (Integer argCode : argCodes) {
                    matchedSlots.add(MatchedSlot.of(argCode, argValue, i, i + argValue.length() - 1));
                }
            }
        }
        return new ArrayList<MatchedSlot>(matchedSlots);
    }

    private Set<ParsedInputText> parseInputTexts(String inputText, List<MatchedSlot> matchedArgs) {
        Set<ParsedInputText> parsedInputTexts = new HashSet<ParsedInputText>();

        // 高效率全组合算法: http://mtnt2008.iteye.com/blog/715104
        // 生成关键字全组合，算法说明：当n大于2时，n个数的全组合一共有(2^n)-1种
        int bit = (0xFFFFFFFF >>> (32 - matchedArgs.size()));
        for (int i = 1; i <= bit; i++) {
            Set<MatchedSlot> matchedArgSet = new HashSet<MatchedSlot>();
            for (int j = 0; j < matchedArgs.size(); j++) {
                if ((i << (31 - j)) >> 31 == -1) {
                    if (CollectionUtils.isEmpty(matchedArgSet)) {
                        matchedArgSet.add(matchedArgs.get(j));
                    } else {
                        boolean sameOrContainOrOverlap = false;
                        for (MatchedSlot matchedArg : matchedArgSet) {
                            // 参数值相同而参数代码不同则不可以放到同一个matchedArgSet内
                            // 有参数值字符overlap情况的参数不可以放到同一个matchedArgSet内，如：
                            // MatchedArg = { argCode: arg42, argValue: 我心, startIndex: 0, endIndex: 1 },
                            // MatchedArg = { argCode: arg387, argValue: 心情不好, startIndex: 1, endIndex: 4 }
                            if (// 相等
                                    matchedArg.getValue().equals(matchedArgs.get(j).getValue())
                                            && matchedArg.getStartIndex() == matchedArgs.get(j).getStartIndex()
                                            && matchedArg.getEndIndex() == matchedArgs.get(j).getEndIndex()
                                            // 包含
                                            || !matchedArg.getValue().equals(matchedArgs.get(j).getValue())
                                            && matchedArg.getValue().contains(matchedArgs.get(j).getValue())
                                            && matchedArg.getStartIndex() <= matchedArgs.get(j).getStartIndex()
                                            && matchedArg.getEndIndex() >= matchedArgs.get(j).getEndIndex()
                                            // 包含
                                            || !matchedArg.getValue().equals(matchedArgs.get(j).getValue())
                                            && matchedArgs.get(j).getValue().contains(matchedArg.getValue())
                                            && matchedArgs.get(j).getStartIndex() <= matchedArg.getStartIndex()
                                            && matchedArgs.get(j).getEndIndex() >= matchedArg.getEndIndex()
                                            // 相交
                                            || matchedArg.getStartIndex() > matchedArgs.get(j).getStartIndex()
                                            && matchedArg.getEndIndex() > matchedArgs.get(j).getEndIndex()
                                            && matchedArg.getStartIndex() <= matchedArgs.get(j).getEndIndex()
                                            // 相交
                                            || matchedArg.getStartIndex() < matchedArgs.get(j).getStartIndex()
                                            && matchedArg.getEndIndex() < matchedArgs.get(j).getEndIndex()
                                            && matchedArg.getEndIndex() >= matchedArgs.get(j).getStartIndex()) {
                                sameOrContainOrOverlap = true;
                                break;
                            }
                        }
                        if (!sameOrContainOrOverlap) {
                            matchedArgSet.add(matchedArgs.get(j));
                        }
                    }
                }
            }

            parsedInputTexts.add(ParsedInputText.of(inputText, matchedArgSet));
        }

        return parsedInputTexts;
    }

}
