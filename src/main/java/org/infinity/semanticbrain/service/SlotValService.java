package org.infinity.semanticbrain.service;

import com.google.common.collect.Multimap;
import org.trie4j.patricia.PatriciaTrie;

public interface SlotValService {
    PatriciaTrie getSlotValTrie();

    Multimap<Integer, String> getCodeValMap();

    Multimap<String, Integer> getValCodeMap();
}
