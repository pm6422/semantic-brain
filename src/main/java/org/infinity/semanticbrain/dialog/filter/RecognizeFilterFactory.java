package org.infinity.semanticbrain.dialog.filter;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class RecognizeFilterFactory {

    public static RecognizeFilterChain createFilterChain(List<RecognizeFilterConfig> filterChainConfigs,
                                                         Map<String, RecognizeFilter> semanticFilterMap,
                                                         ExecutorService threadPoolExecutor) {
        Assert.notEmpty(filterChainConfigs, "Filter chains must NOT be empty!");
        return new RecognizeFilterChain(filterChainConfigs, semanticFilterMap, threadPoolExecutor);
    }
}
