package org.infinity.semanticbrain.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import org.infinity.semanticbrain.service.SlotValService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.trie4j.patricia.PatriciaTrie;

import java.util.Map;
import java.util.Set;

@Service
public class SlotValServiceImpl implements SlotValService, InitializingBean {

    private static BiMap<Integer, String> codeValMap = HashBiMap.create();
    private        PatriciaTrie           trie;

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
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<Map.Entry<Integer, String>> entries = codeValMap.entrySet();
        for (Map.Entry<Integer, String> entry : entries) {
            trie.insert(entry.getValue());
        }
    }

    @Override
    public PatriciaTrie getSlotValTrie() {
        return trie;
    }

    @Override
    public Map<Integer, String> getCodeValMap() {
        return codeValMap;
    }

    @Override
    public Multimap<String, Integer> getValCodesMap() {
        Multimap<String, Integer> map = ArrayListMultimap.create();
        Set<Map.Entry<Integer, String>> entries = codeValMap.entrySet();
        for (Map.Entry<Integer, String> entry : entries) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }
}
