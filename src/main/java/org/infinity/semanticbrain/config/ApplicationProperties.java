package org.infinity.semanticbrain.config;

import lombok.Data;
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
@Data
public class ApplicationProperties {
    private final Directory            directory            = new Directory();
    private final Swagger              swagger              = new Swagger();
    private final AopLogging           aopLogging           = new AopLogging();
    private final ServiceMetrics       serviceMetrics       = new ServiceMetrics();
    private final Zookeeper            zookeeper            = new Zookeeper();
    private final Redis                redis                = new Redis();
    private final Ribbon               ribbon               = new Ribbon();
    private final HttpClientConnection httpClientConnection = new HttpClientConnection();
    private final Url                  url                  = new Url();
    private final DialogContext        dialogContext        = new DialogContext();
    private final SemanticFilter       semanticFilter       = new SemanticFilter();

    @Data
    public static class Directory {
        private String config    = "classpath:config";
        private String templates = "classpath:templates";
        private String data      = "classpath:data";
        private String appsFile;
    }

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
    public static class AopLogging {
        private boolean enabled;
    }

    @Data
    public static class ServiceMetrics {
        private boolean enabled;
    }

    @Data
    public static class Dubbo {
        private String adminUrl;
        private String monitorUrl;
    }

    @Data
    public static class Zookeeper {
        private String adminUrl;
    }

    @Data
    public static class Redis {
        private String adminUrl;
    }

    @Data
    public static class Ribbon {
        private String[] displayOnActiveProfiles;
    }

    @Data
    public static class HttpClientConnection {
        private int globalRetryCount;
        private int globalReadTimeoutInSeconds;
    }

    @Data
    public static class Url {
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
