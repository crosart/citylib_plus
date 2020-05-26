package com.citylib.citylibwebapp.config;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to allow OpenFeign to decode {@link org.springframework.data.domain.Page} objects.
 *
 * @author crosart
 */
@Configuration
public class FeignDecodeConfiguration {

    @Bean
    public Module pageJacksonModule() {
        return new PageJacksonModuleFixGH237();
    }

}
