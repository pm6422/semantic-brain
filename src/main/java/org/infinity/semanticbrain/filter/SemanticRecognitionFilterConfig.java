package org.infinity.semanticbrain.filter;

import java.util.List;

public class SemanticRecognitionFilterConfig implements SemanticFilterConfig {

    private List<SemanticFilter> semanticFilters;

    public SemanticRecognitionFilterConfig(List<SemanticFilter> semanticFilters) {
        this.semanticFilters = semanticFilters;
    }

    public static SemanticRecognitionFilterConfig of(List<SemanticFilter> semanticFilter) {
        return new SemanticRecognitionFilterConfig(semanticFilter);
    }

    @Override
    public List<SemanticFilter> getFilters() {
        return semanticFilters;
    }

}
