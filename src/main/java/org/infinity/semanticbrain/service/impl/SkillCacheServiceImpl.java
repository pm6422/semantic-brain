package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.service.SkillCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SkillCacheServiceImpl implements SkillCacheService, InitializingBean {

    private static final Logger              LOGGER                  = LoggerFactory.getLogger(SkillCacheServiceImpl.class);
    private              Map<String, String> enabledCodeNameMapCache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.refresh();
    }

    @Override
    public void refresh() {
        LOGGER.debug("");
    }

    @Override
    public void broadcastRefresh() {
        this.refresh();
    }
}
