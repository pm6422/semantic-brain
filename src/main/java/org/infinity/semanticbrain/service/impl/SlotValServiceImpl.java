package org.infinity.semanticbrain.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.infinity.semanticbrain.service.SlotValService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.trie4j.patricia.PatriciaTrie;

import java.util.Collection;
import java.util.Map;

@Service
public class SlotValServiceImpl implements SlotValService, InitializingBean {

    private static Multimap<Integer, String> codeValMap = ArrayListMultimap.create();// Multimap为一个key多个value结构
    private        PatriciaTrie              trie;

    static {
        codeValMap.put(1, "爸爸");
        codeValMap.put(1, "爸");
        codeValMap.put(1, "妈妈");
        codeValMap.put(1, "哥哥");
        codeValMap.put(1, "弟弟");
        codeValMap.put(1, "姐姐");
        codeValMap.put(1, "妹妹");
        codeValMap.put(1, "叔叔");
        codeValMap.put(1, "阿姨");
        codeValMap.put(2, "上海");
        codeValMap.put(2, "北京");
        codeValMap.put(3, "上海");
        codeValMap.put(3, "北京");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        trie = new PatriciaTrie();
        Collection<Map.Entry<Integer, String>> entries = codeValMap.entries();
        for (Map.Entry<Integer, String> entry : entries) {
            trie.insert(entry.getValue());
        }
    }

    @Override
    public PatriciaTrie getSlotValTrie(String skillCode) {
        return trie;
    }

    @Override
    public Multimap<Integer, String> getCodeValMap() {
        return codeValMap;
    }

    @Override
    public Multimap<String, Integer> getValCodeMap(String skillCode) {
        Multimap<String, Integer> map = ArrayListMultimap.create();
        Collection<Map.Entry<Integer, String>> entries = codeValMap.entries();
        for (Map.Entry<Integer, String> entry : entries) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }
}
