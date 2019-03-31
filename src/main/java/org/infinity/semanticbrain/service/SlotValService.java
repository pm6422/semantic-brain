package org.infinity.semanticbrain.service;

import com.google.common.collect.Multimap;
import org.trie4j.patricia.PatriciaTrie;

import java.util.Map;

public interface SlotValService {
    PatriciaTrie getSlotValTrie();

    Map<Integer, String> getCodeValMap();

    Multimap<String, Integer> getValCodesMap();
}
