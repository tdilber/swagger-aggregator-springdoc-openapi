package com.beyt.doc.aggregator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Configuration
@ConditionalOnProperty("springdoc.grouping.enable")
public class RestTemplateConfig {

    @Bean
    public RestTemplate restClient(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                log.debug("Rest Template Error : {} Status : {}", clientHttpResponse.getStatusText(), clientHttpResponse.getStatusCode());
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                log.warn("Rest Template Error : {} Status : {}", clientHttpResponse.getStatusText(), clientHttpResponse.getStatusCode());
            }
        });
        return restTemplate;
    }
}
