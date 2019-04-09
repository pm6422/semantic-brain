package org.infinity.semanticbrain.service;

import com.google.common.collect.Multimap;
import org.trie4j.patricia.PatriciaTrie;

public interface SlotValService {
    PatriciaTrie getSlotValTrie(String skillCode);

    Multimap<Integer, String> getCodeValMap();

    Multimap<String, Integer> getValCodeMap(String skillCode);
}
