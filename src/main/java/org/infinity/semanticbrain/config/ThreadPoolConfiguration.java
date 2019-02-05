package org.infinity.semanticbrain.config;

import org.apache.commons.lang3.StringUtils;
import org.infinity.semanticbrain.utils.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfiguration {
    @Value("${info.artifact.name}")
    private String appName;

    @Bean
    public ExecutorService nluThreadPool() {
        int cpuCores = Runtime.getRuntime().availableProcessors() - 3;
        return new ThreadPoolExecutor(cpuCores, cpuCores, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(10),
                new NamedThreadFactory(StringUtils.upperCase(appName) + "-NLU-POOL"), new ThreadPoolExecutor.DiscardPolicy());
    }
}
