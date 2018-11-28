package org.infinity.semanticbrain.filter;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public final class SemanticFilterFactory {

    private SemanticFilterFactory() {
        // Prevent instance creation. This is a utility class.
    }

    public static SemanticRecognitionFilterChain createFilterChain(List<SemanticRecognitionFilterConfig> filterChainConfigs, Map<String, SemanticFilter> semanticFilterMap, ThreadPoolExecutor threadPoolExecutor) {
        Assert.notEmpty(filterChainConfigs, "Filter chains must NOT be empty.");
        SemanticRecognitionFilterChain filterChain = new SemanticRecognitionFilterChain(filterChainConfigs, semanticFilterMap, threadPoolExecutor);
        return filterChain;
    }
}
