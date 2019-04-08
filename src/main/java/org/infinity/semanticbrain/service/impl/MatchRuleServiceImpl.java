package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.domain.MatchRule;
import org.infinity.semanticbrain.service.MatchRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchRuleServiceImpl implements MatchRuleService {
    @Override
    public List<MatchRule> find(String skillCode) {
        return null;
    }
}
