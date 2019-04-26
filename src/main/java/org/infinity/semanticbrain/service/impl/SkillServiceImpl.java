package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.service.SkillCacheService;
import org.infinity.semanticbrain.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillCacheService skillCacheService;

    @Override
    public void delete(String id) {
        skillCacheService.broadcastRefresh();
    }
}
