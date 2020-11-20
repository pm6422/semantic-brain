package org.infinity.semanticbrain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.infinity.semanticbrain.config.ApplicationConstants;
import org.infinity.semanticbrain.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
public class SemanticBrainLauncher {

    private final Environment env;

    public SemanticBrainLauncher(Environment env) {
        this.env = env;
    }

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SemanticBrainLauncher.class, args);
    }

    @PostConstruct
    private void validateProfiles() {
        Assert.notEmpty(env.getActiveProfiles(), "No Spring profile configured.");
        Assert.isTrue(env.getActiveProfiles().length == 1, "Multiple profiles are not allowed!");
        Arrays.stream(env.getActiveProfiles())
                .filter(activeProfile -> !ArrayUtils.contains(ApplicationConstants.AVAILABLE_PROFILES, activeProfile))
                .findFirst().ifPresent((activeProfile) -> {
            log.error("Mis-configured application with an illegal profile '{}'!", activeProfile);
            System.exit(0);
        });
    }
}
