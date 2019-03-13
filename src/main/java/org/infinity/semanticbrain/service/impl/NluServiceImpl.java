package org.infinity.semanticbrain.service.impl;

import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.dialog.context.DialogContextManager;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.filter.SemanticFilter;
import org.infinity.semanticbrain.dialog.filter.SemanticFilterFactory;
import org.infinity.semanticbrain.dialog.filter.SemanticRecognitionFilterChain;
import org.infinity.semanticbrain.dialog.filter.SemanticRecognitionFilterConfig;
import org.infinity.semanticbrain.service.InputPreprocessService;
import org.infinity.semanticbrain.service.NluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NluServiceImpl implements NluService, ApplicationContextAware, InitializingBean, DisposableBean {

    private static final Logger                                LOGGER             = LoggerFactory.getLogger(NluServiceImpl.class);
    @Autowired
    private              ApplicationProperties                 applicationProperties;
    @Autowired
    private              InputPreprocessService                inputPreprocessService;
    @Autowired
    private              DialogContextManager                  dialogContextManager;
    private              ApplicationContext                    applicationContext;
    private              List<SemanticRecognitionFilterConfig> filterChainConfigs = new ArrayList<>();
    private              Map<String, SemanticFilter>           semanticFilterMap  = new HashMap<>();
    private volatile     boolean                               logIntentionResult = false;
    @Autowired
    @Qualifier("nluThreadPool")
    private              ExecutorService                       threadPool;

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
    }

    @Override
    public void destroy() throws Exception {
        threadPool.shutdown();
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            LOGGER.info("All threads are terminated!");
        }
    }

    @Override
    public Output recognize(Input input) {
        return this.recognize(input, null);
    }

    @Override
    public Output recognize(Input input, String skillCode) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Output output = new Output();
        try {
            this.beforeProcess(input);
            Output lastOutput = this.getLastOutput(input);
            SemanticRecognitionFilterChain filterChain = SemanticFilterFactory.createFilterChain(filterChainConfigs, semanticFilterMap, threadPool);
            filterChain.doFilter(input, output, lastOutput);
            this.afterProcess(output);
        } catch (Exception e) {
        }
        stopWatch.stop();
        output.setElapsed(stopWatch.getTotalTimeMillis());
        this.terminated(output);
        return output;
    }

    private void beforeProcess(Input input) {
        inputPreprocessService.preprocess(input);
    }

    private Output getLastOutput(Input input) {
        return dialogContextManager.getLastOutput(input);
    }

    private void afterProcess(Output output) {
    }

    private void terminated(Output output) {
    }

    @Override
    public void setLogIntentionResult(boolean logIntentionResult) {
        this.logIntentionResult = logIntentionResult;
    }
}
