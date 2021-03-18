package org.infinity.semanticbrain.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Properties specific to Application.
 *
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Validated
@Getter
public class ApplicationProperties {
    private final Swagger        swagger        = new Swagger();
    private final Ribbon         ribbon         = new Ribbon();
    private final DialogContext  dialogContext  = new DialogContext();
    private final SemanticFilter semanticFilter = new SemanticFilter();

    @Data
    public static class Swagger {
        private       boolean enabled;
        private       String  version;
        private       String  termsOfServiceUrl;
        private       String  contactName;
        private       String  contactUrl;
        private       String  contactEmail;
        private       String  license;
        private       String  licenseUrl;
        private       String  host;
        private final Api     api     = new Api();
        private final OpenApi openApi = new OpenApi();

        @Data
        public static class Api {
            private String title;
            private String description;
        }

        @Data
        public static class OpenApi {
            private String title;
            private String description;
        }
    }

    @Data
    public static class Ribbon {
        private String[] displayOnActiveProfiles;
    }

    @Data
    public static class DialogContext {
        private int keepAliveSeconds;
        private int skillModeKeepAliveSeconds;
        private int latestOutputSize;
    }

    @Data
    public static class SemanticFilter {
        private List<List<String>> seq;
    }
}
