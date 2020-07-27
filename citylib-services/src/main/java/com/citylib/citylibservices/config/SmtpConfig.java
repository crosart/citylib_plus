package com.citylib.citylibservices.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties retrieving class.
 * <p>
 * Properties are served from environment variables through the citylib-batch.properties file with the "maconfig"
 * prefix, retrieved by the Spring Cloud Config Server (config-server service).
 * </p>
 *
 * @author crosart
 */
@Configuration
@ConfigurationProperties(prefix = "smtpconfig")
@Getter
@Setter
public class SmtpConfig {

    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    private String smtpPass;

}
