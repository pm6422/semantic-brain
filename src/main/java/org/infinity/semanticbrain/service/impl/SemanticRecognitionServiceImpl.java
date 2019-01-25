package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.entity.Input;
import org.infinity.semanticbrain.entity.Output;
import org.infinity.semanticbrain.filter.SemanticFilter;
import org.infinity.semanticbrain.filter.SemanticFilterFactory;
import org.infinity.semanticbrain.filter.SemanticRecognitionFilterChain;
import org.infinity.semanticbrain.filter.SemanticRecognitionFilterConfig;
import org.infinity.semanticbrain.service.SemanticRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SemanticRecognitionServiceImpl implements SemanticRecognitionService, ApplicationContextAware, InitializingBean, DisposableBean {

    private static final Logger                                LOGGER             = LoggerFactory.getLogger(SemanticRecognitionServiceImpl.class);
    @Autowired
    private              ApplicationProperties                 applicationProperties;
    private              ApplicationContext                    applicationContext;
    private              List<SemanticRecognitionFilterConfig> filterChainConfigs = new ArrayList<>();
    private              Map<String, SemanticFilter>           semanticFilterMap  = new HashMap<>();
    private volatile     boolean                               logIntentionResult = false;
    private              ThreadPoolExecutor                    threadPool;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationProperties.getSemanticFilter().getSeq().forEach(filterNames -> {
            List<SemanticFilter> filterBeans = new ArrayList<>();
            filterNames.forEach(filterName -> {
                SemanticFilter filterBean = (SemanticFilter) applicationContext.getBean(filterName);
                filterBeans.add(filterBean);
            });
            filterChainConfigs.add(SemanticRecognitionFilterConfig.of(filterBeans));
        });

        semanticFilterMap = applicationContext.getBeansOfType(SemanticFilter.class);
        int cpuCores = Runtime.getRuntime().availableProcessors() - 3;
        threadPool = new ThreadPoolExecutor(cpuCores, cpuCores, 1, TimeUnit.SECONDS, new LinkedBlockingQueue(15), new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    public void destroy() throws Exception {
        threadPool.shutdown();
        while (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
            LOGGER.debug("Thread is still running!");
        }
        LOGGER.info("All threads are terminated!");
    }

    @Override
    public Output recognize(Input input) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Output output = new Output();
        try {
            SemanticRecognitionFilterChain filterChain = SemanticFilterFactory.createFilterChain(filterChainConfigs, semanticFilterMap, threadPool);
            this.processInput(input);
            Output lastOutput = this.getLastOutput(input);
            filterChain.doFilter(input, output, lastOutput);
            this.processOutput(output);
        } catch (Exception e) {
        }
        stopWatch.stop();
        output.setElapsed(stopWatch.getTotalTimeMillis());
        return output;
    }

    private void processInput(Input input) {
    }

    private void traditionalToSimplified(Input input) {

    }

    private void removePunctuation(Input input) {

    }

    private void removeModalParticles(Input input) {

    }

    private void processSynonymousSentence(Input input) {

    }

    private void processOutput(Output output) {
    }

    private Output getLastOutput(Input input) {
        return null;
    }

    @Override
    public void setLogIntentionResult(boolean logIntentionResult) {
        this.logIntentionResult = logIntentionResult;
    }
}
