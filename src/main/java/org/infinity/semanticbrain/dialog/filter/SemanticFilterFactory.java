package org.infinity.semanticbrain.dialog.filter;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class SemanticFilterFactory {

    public static SemanticRecognitionFilterChain createFilterChain(List<SemanticRecognitionFilterConfig> filterChainConfigs,
                                                                   Map<String, SemanticFilter> semanticFilterMap,
                                                                   ExecutorService threadPoolExecutor) {
        Assert.notEmpty(filterChainConfigs, "Filter chains must NOT be empty.");
        SemanticRecognitionFilterChain filterChain = new SemanticRecognitionFilterChain(filterChainConfigs, semanticFilterMap, threadPoolExecutor);
        return filterChain;
    }
}
