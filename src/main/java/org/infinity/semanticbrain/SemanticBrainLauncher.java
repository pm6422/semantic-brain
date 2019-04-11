package org.infinity.semanticbrain;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.infinity.semanticbrain.config.ApplicationConstants;
import org.infinity.semanticbrain.config.ApplicationProperties;
import org.infinity.semanticbrain.utils.NetworkIpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SemanticBrainLauncher {

    private static final Logger      LOGGER = LoggerFactory.getLogger(SemanticBrainLauncher.class);
    @Autowired
    private              Environment env;

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(SemanticBrainLauncher.class);
        Environment env = app.run(args).getEnvironment();
        printServerInfo(env);
    }

    private static void printServerInfo(Environment env) throws IOException {
        String appBanner = StreamUtils.copyToString(new ClassPathResource("config/banner-app.txt").getInputStream(),
                Charset.defaultCharset());
        LOGGER.info(appBanner, env.getProperty("spring.application.name"),
                isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                env.getProperty("server.port"), defaultString(env.getProperty("server.context-path")),
                isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                NetworkIpUtils.INTERNET_IP, env.getProperty("server.port"),
                defaultString(env.getProperty("server.context-path")),
                org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()),
                env.getProperty("PID"), Charset.defaultCharset(), env.getProperty("LOG_PATH") + "-"
                        + DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()) + ".log");
    }

    @PostConstruct
    private void validateProfiles() {
        Assert.notEmpty(env.getActiveProfiles(), "No Spring profile configured.");
        Arrays.asList(env.getActiveProfiles()).stream()
                .filter(activeProfile -> !ArrayUtils.contains(ApplicationConstants.AVAILABLE_PROFILES, activeProfile))
                .findFirst().ifPresent((activeProfile) -> {
            LOGGER.error("Misconfigured application with an illegal profile '{}'!", activeProfile);
            System.exit(0);
        });
    }
}
