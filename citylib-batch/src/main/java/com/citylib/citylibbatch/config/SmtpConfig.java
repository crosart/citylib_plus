package com.citylib.citylibbatch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "maconfig")
@Getter
@Setter
public class SmtpConfig {

    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    private String smtpPass;

}
