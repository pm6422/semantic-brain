package org.infinity.semanticbrain.dialog.filter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class RecognizeFilterFactory {

    public static RecognizeFilterChain createFilterChain(List<List<RecognizeFilter>> filtersChains,
                                                         Map<String, RecognizeFilter> semanticFilterMap,
                                                         ExecutorService threadPoolExecutor) {
        return new RecognizeFilterChain(filtersChains, semanticFilterMap, threadPoolExecutor);
    }
}
