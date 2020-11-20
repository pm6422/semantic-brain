package org.infinity.semanticbrain.controller;

import org.apache.commons.lang3.StringUtils;
import org.infinity.semanticbrain.config.ApplicationProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HomeController {

    private final Environment           env;
    private final ApplicationProperties applicationProperties;

    public HomeController(Environment env, ApplicationProperties applicationProperties) {
        this.env = env;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Home page.
     */
    @GetMapping("/")
    public String home(HttpServletResponse response) throws IOException {
        if (applicationProperties.getSwagger().isEnabled()) {
            response.sendRedirect("swagger-ui.html");
        }
        return StringUtils.defaultString(env.getProperty("spring.application.name")).concat(" Home Page");
    }
}
