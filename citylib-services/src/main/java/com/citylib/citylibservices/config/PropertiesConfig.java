package com.citylib.citylibservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties retrieving class.
 * <p>
 * Properties are served from the citylib-services.properties file with the "property" prefix, retrieved by the
 * Spring Cloud Config Server (config-server service).
 * </p>
 *
 * @author crosart
 */
@Component
@ConfigurationProperties("property")
@Data
public class PropertiesConfig {
    private int lastBooks;
    private int defaultPageSize;
}
