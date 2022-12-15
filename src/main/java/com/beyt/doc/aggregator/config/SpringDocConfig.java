package com.beyt.doc.aggregator.config;

import com.beyt.doc.aggregator.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Slf4j
@Configuration
@EnableConfigurationProperties(GroupedSwaggerConfig.class)
@ConditionalOnProperty("springdoc.grouping.enable")
public class SpringDocConfig {

    @Bean
    public SwaggerUiConfigProperties swaggerUiConfig(SwaggerUiConfigProperties config) {
        if (Objects.isNull(config.getUrl())) {
            config.setUrl(Constants.DOCUMENT_URL);
        }

        return config;
    }
}
