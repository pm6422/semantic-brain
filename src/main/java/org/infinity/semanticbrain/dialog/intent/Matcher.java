package org.infinity.semanticbrain.dialog.intent;

import com.google.common.collect.Multimap;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.service.SlotValService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trie4j.patricia.PatriciaTrie;

@Component
public class Matcher {
    @Autowired
    private SlotValService slotValService;

    public Output matchSlotVal(Input input, Output lastOutput) {
        PatriciaTrie trie = slotValService.getSlotValTrie();
        Multimap<String, Integer> valCodesMap = slotValService.getValCodesMap();



        return null;
    }
}
