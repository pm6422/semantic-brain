package org.infinity.semanticbrain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Application constants.
 */
public final class ApplicationConstants {

    private static final Logger   LOGGER                    = LoggerFactory.getLogger(ApplicationConstants.class);
    public static final  String   BASE_PACKAGE              = "org.infinity.semanticbrain";
    // Spring profile
    public static final  String   SPRING_PROFILES_ACTIVE    = "spring.profiles.active";
    public static final  String   SPRING_PROFILE_TEST       = "test";
    public static final  String   SPRING_PROFILE_PRODUCTION = "prod";
    // Spring profile used to disable swagger
    public static final  String   SPRING_PROFILE_NO_SWAGGER = "no-swagger";
    public static final  String[] AVAILABLE_PROFILES        = new String[]{SPRING_PROFILE_TEST, SPRING_PROFILE_PRODUCTION, SPRING_PROFILE_NO_SWAGGER};
    public static final  String   SYSTEM_ACCOUNT            = "system";
    public static final  String   SCHEDULE_LOG_PATTERN      = "########################Schedule executed: {}########################";
    public static final  Locale   SYSTEM_LOCALE             = Locale.SIMPLIFIED_CHINESE;
}
