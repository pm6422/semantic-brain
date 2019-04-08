package org.infinity.semanticbrain.service;

import org.infinity.semanticbrain.domain.MatchRule;

import java.util.List;

public interface MatchRuleService {
    List<MatchRule> find(String skillCode);
}
