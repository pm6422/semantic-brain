package org.infinity.semanticbrain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static java.net.URLDecoder.decode;

/**
 * Web application configuration
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfigurer.class);

    @Override
    public void onStartup(ServletContext servletContext) {
        LOGGER.info("Configuring web application");
        LOGGER.info("Configured web application");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(WebServerFactory server) {
        setMimeMappings(server);
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
        setLocationForStaticAssets(server);
    }

    private void setMimeMappings(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory) {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
            mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
            mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            servletWebServer.setMimeMappings(mappings);
        }
    }

    private void setLocationForStaticAssets(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory) {
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            File root;
            String prefixPath = resolvePathPrefix();
            root = new File(prefixPath + "src/main/webapp/");
            if (root.exists() && root.isDirectory()) {
                servletWebServer.setDocumentRoot(root);
            }
        }
    }

    /**
     * Resolve path prefix to static resources.
     */
    private String resolvePathPrefix() {
        String fullExecutablePath;
        try {
            fullExecutablePath = decode(this.getClass().getResource("").getPath(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            /* try without decoding if this ever happens */
            fullExecutablePath = this.getClass().getResource("").getPath();
        }
        String rootPath = Paths.get(".").toUri().normalize().getPath();
        String extractedPath = fullExecutablePath.replace(rootPath, "");
        int extractionEndIndex = extractedPath.indexOf("target/");
        if (extractionEndIndex <= 0) {
            return "";
        }
        return extractedPath.substring(0, extractionEndIndex);
    }
}