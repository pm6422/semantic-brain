package org.infinity.semanticbrain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.dialog.context.DialogContextManager;
import org.infinity.semanticbrain.dialog.entity.Device;
import org.infinity.semanticbrain.dialog.entity.Input;
import org.infinity.semanticbrain.dialog.entity.Output;
import org.infinity.semanticbrain.dialog.filter.RecognizeFilter;
import org.infinity.semanticbrain.dialog.filter.RecognizeFilterChain;
import org.infinity.semanticbrain.service.InputPreprocessService;
import org.infinity.semanticbrain.service.NluService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NluServiceImpl implements NluService, ApplicationContextAware, InitializingBean, DisposableBean {

    @Autowired
    private       ApplicationProperties        applicationProperties;
    @Autowired
    private       InputPreprocessService       inputPreprocessService;
    @Autowired
    private       DialogContextManager         dialogContextManager;
    private       ApplicationContext           applicationContext;
    private final List<List<RecognizeFilter>>  filtersChains     = new ArrayList<>();
    private       Map<String, RecognizeFilter> semanticFilterMap = new ConcurrentHashMap<>();
    @Autowired
    @Qualifier("nluThreadPool")
    private       ExecutorService              threadPool;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized void afterPropertiesSet() {
        applicationProperties.getSemanticFilter().getSeq().forEach(filterNames -> {
            List<RecognizeFilter> filterBeans = new ArrayList<>();
            filterNames.forEach(filterName -> {
                RecognizeFilter filterBean = (RecognizeFilter) applicationContext.getBean(filterName);
                filterBeans.add(filterBean);
            });
            filtersChains.add(filterBeans);
        });
        Assert.notEmpty(filtersChains, "Filters chains must NOT be empty!");
        semanticFilterMap = applicationContext.getBeansOfType(RecognizeFilter.class);
    }

    @Override
    public Output recognize(Input input, String skillCode) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Output checkedOutput = this.checkInput(input);
        if (checkedOutput != null) {
            return checkedOutput;
        }

        Output output = new Output();
        try {
            Output lastOutput = this.getLastOutput(input.getDevice());
            this.beforeProcess(input, lastOutput);
            List<String> skillCodes = new ArrayList<>();
            if (StringUtils.isNotEmpty(skillCode)) {
                skillCodes.add(skillCode);
            }
            RecognizeFilterChain.of(filtersChains, semanticFilterMap, threadPool).doFilter(input, output, lastOutput, skillCodes);
            this.afterProcess(input, output);
        } catch (Exception e) {
            log.error("Failed to recognize intention", e);
        }
        stopWatch.stop();
        output.setElapsed(stopWatch.getTotalTimeMillis());
        this.terminated(input, output);
        return output;
    }

    @Override
    public Output recognize(Input input) {
        return this.recognize(input, null);
    }

    @Override
    public Output recognize(Input input, String skillCode, boolean saveOutput) {
        Output output;
        try {
            output = this.recognize(input, skillCode);
            return output;
        } finally {
            if (saveOutput) {
                // Save output
            }
        }
    }

    private Output checkInput(Input input) {
        return null;
    }

    private Output getLastOutput(Device device) {
        return dialogContextManager.getLastAliveOutput(device);
    }

    private void beforeProcess(Input input, Output lastOutput) {
        inputPreprocessService.preprocess(input, lastOutput);
    }

    private void afterProcess(Input input, Output output) {
    }

    private void terminated(Input input, Output output) {
        dialogContextManager.broadcastAddOutput(input.getDevice(), output);
    }

    @Override
    public void destroy() throws Exception {
        threadPool.shutdown();
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            log.info("All threads are terminated!");
        }
    }
}
