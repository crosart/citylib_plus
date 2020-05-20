package com.citylib.citylibservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("property")
@Data
public class PropertiesConfig {
    private int lastBooks;
    private int defaultPageSize;
}
