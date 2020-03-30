package org.infinity.semanticbrain.config;

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
public class ApplicationProperties {

    private final Directory            directory            = new Directory();
    private final Async                async                = new Async();
    private final Swagger              swagger              = new Swagger();
    private final Zookeeper            zookeeper            = new Zookeeper();
    private final Redis                redis                = new Redis();
    private final Ribbon               ribbon               = new Ribbon();
    private final HttpClientConnection httpClientConnection = new HttpClientConnection();
    private final Url                  url                  = new Url();
    private final DialogContext        dialogContext        = new DialogContext();
    private final SemanticFilter       semanticFilter       = new SemanticFilter();

    public Directory getDirectory() {
        return directory;
    }

    public Async getAsync() {
        return async;
    }

    public Swagger getSwagger() {
        return swagger;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public Redis getRedis() {
        return redis;
    }

    public Ribbon getRibbon() {
        return ribbon;
    }

    public HttpClientConnection getHttpClientConnection() {
        return httpClientConnection;
    }

    public Url getUrl() {
        return url;
    }

    public DialogContext getDialogContext() {
        return dialogContext;
    }

    public SemanticFilter getSemanticFilter() {
        return semanticFilter;
    }

    public static class Directory {
        private String config = "classpath:config";

        private String templates = "classpath:templates";

        private String data = "classpath:data";

        private String appsFile;

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public String getTemplates() {
            return templates;
        }

        public void setTemplates(String templates) {
            this.templates = templates;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getAppsFile() {
            return appsFile;
        }

        public void setAppsFile(String appsFile) {
            this.appsFile = appsFile;
        }
    }

    public static class Async {

        private int corePoolSize = 2;

        private int maxPoolSize = 50;

        private int queueCapacity = 10000;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }

    public static class Swagger {

        private static Api api = new Api();

        private static OpenApi openApi = new OpenApi();

        private String version;

        private String termsOfServiceUrl;

        private String contactName;

        private String contactUrl;

        private String contactEmail;

        private String license;

        private String licenseUrl;

        private String host;

        public Api getApi() {
            return api;
        }

        public OpenApi getOpenApi() {
            return openApi;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTermsOfServiceUrl() {
            return termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactUrl() {
            return contactUrl;
        }

        public void setContactUrl(String contactUrl) {
            this.contactUrl = contactUrl;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getLicenseUrl() {
            return licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public static class Api {
            private String title;

            private String description;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public static class OpenApi {
            private String title;

            private String description;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    public static class Dubbo {

        private String adminUrl;

        private String monitorUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }

        public String getMonitorUrl() {
            return monitorUrl;
        }

        public void setMonitorUrl(String monitorUrl) {
            this.monitorUrl = monitorUrl;
        }
    }

    public static class Zookeeper {

        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class Redis {
        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class Ribbon {

        private String[] displayOnActiveProfiles;

        public String[] getDisplayOnActiveProfiles() {
            return displayOnActiveProfiles;
        }

        public void setDisplayOnActiveProfiles(String[] displayOnActiveProfiles) {
            this.displayOnActiveProfiles = displayOnActiveProfiles;
        }
    }

    public static class HttpClientConnection {

        private int globalRetryCount;

        private int globalReadTimeoutInSeconds;

        public int getGlobalRetryCount() {
            return globalRetryCount;
        }

        public void setGlobalRetryCount(int globalRetryCount) {
            this.globalRetryCount = globalRetryCount;
        }

        public int getGlobalReadTimeoutInSeconds() {
            return globalReadTimeoutInSeconds;
        }

        public void setGlobalReadTimeoutInSeconds(int globalReadTimeoutInSeconds) {
            this.globalReadTimeoutInSeconds = globalReadTimeoutInSeconds;
        }
    }

    public static class Url {
    }

    public static class DialogContext {

        private int keepAliveSeconds;
        private int skillModeKeepAliveSeconds;
        private int latestOutputSize;

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getSkillModeKeepAliveSeconds() {
            return skillModeKeepAliveSeconds;
        }

        public void setSkillModeKeepAliveSeconds(int skillModeKeepAliveSeconds) {
            this.skillModeKeepAliveSeconds = skillModeKeepAliveSeconds;
        }

        public int getLatestOutputSize() {
            return latestOutputSize;
        }

        public void setLatestOutputSize(int latestOutputSize) {
            this.latestOutputSize = latestOutputSize;
        }
    }

    public static class SemanticFilter {
        private List<List<String>> seq;

        public List<List<String>> getSeq() {
            return seq;
        }

        public void setSeq(List<List<String>> seq) {
            this.seq = seq;
        }
    }
}
