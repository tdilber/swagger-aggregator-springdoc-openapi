package com.beyt.doc.aggregator.service;


import com.beyt.doc.aggregator.config.GroupedSwaggerConfig;
import com.beyt.doc.aggregator.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("groupedSwaggerService")
@ConditionalOnProperty("springdoc.grouping.enable")
public class GroupedSwaggerService implements ApplicationRunner {
    private final RestTemplate restTemplate;
    private final GroupedSwaggerConfig config;

    private OpenAPI currentOpenAPI;


    public GroupedSwaggerService(RestTemplate restTemplate, GroupedSwaggerConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long period = Objects.requireNonNullElse(config.getRefreshTimeoutMs(), Constants.DEFAULT_REFRESH_TIMEOUT);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::prepareGroupedSwaggerYaml, 0L, period, TimeUnit.MILLISECONDS);
        log.info("Grouped Swagger Service started every {} ms!", period);
    }

    protected void prepareGroupedSwaggerYaml() {
        log.debug("Job Begin");
        OpenAPI updatingOpenApi = getBaseOrEmptyOpenApi();

        setDefaultValues(updatingOpenApi);

        if (Objects.isNull(config.getModules()) || config.getModules().isEmpty()) {
            this.currentOpenAPI = updatingOpenApi;
        }

        for (var module : config.getModules().values()) {
            OpenAPI openAPI = fetchRemoteOpenApi(module.getUrl());
            if (Objects.isNull(openAPI)) {
                log.debug("URL fetch failed : service : {} url : {} ", module.getDescription(), module.getUrl());
                continue;
            }
            log.debug("URL fetch successfully : service : {} url : {} ", module.getDescription(), module.getUrl());

            updateFields(updatingOpenApi, openAPI, module.getDescription());
        }

        this.currentOpenAPI = updatingOpenApi;
        log.debug("Job End");
    }

    protected static void setDefaultValues(OpenAPI updatingOpenApi) {
        if (Objects.isNull(updatingOpenApi.getServers())) {
            updatingOpenApi.setServers(new ArrayList<>());
        }

        if (Objects.isNull(updatingOpenApi.getPaths())) {
            updatingOpenApi.setPaths(new Paths());
        }

        if (Objects.isNull(updatingOpenApi.getTags())) {
            updatingOpenApi.setTags(new ArrayList<>());
        }

        if (Objects.isNull(updatingOpenApi.getComponents())) {
            updatingOpenApi.setComponents(new Components());
        }

        if (Objects.isNull(updatingOpenApi.getComponents().getSchemas())) {
            updatingOpenApi.getComponents().setSchemas(new HashMap<>());
        }
    }

    protected OpenAPI getBaseOrEmptyOpenApi() {
        OpenAPI updatingOpenApi = new OpenAPI();
        if (Objects.nonNull(config.getBaseModule()) && Strings.isNotBlank(config.getBaseModule().getDescription()) && Strings.isNotBlank(config.getBaseModule().getUrl())) {
            updatingOpenApi = fetchRemoteOpenApi(config.getBaseModule().getUrl());
            if (Objects.isNull(updatingOpenApi)) {
                log.debug("Base URL fetch failed : service : {} url : {} ", config.getBaseModule().getDescription(), config.getBaseModule().getUrl());
                updatingOpenApi = new OpenAPI();
            } else {
                log.debug("Base URL fetch successfully : service : {} url : {} ", config.getBaseModule().getDescription(), config.getBaseModule().getUrl());
                updatingOpenApi.setServers(Objects.requireNonNullElse(updatingOpenApi.getServers(), new ArrayList<Server>()).stream().peek(s -> s.setDescription(config.getBaseModule().getDescription())).collect(Collectors.toList()));
            }
        }
        return updatingOpenApi;
    }

    public OpenAPI getCurrentOpenAPI() {
        return currentOpenAPI;
    }

    protected OpenAPI fetchRemoteOpenApi(String url) {
        OpenAPI openAPI = null;
        try {
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            String body = forEntity.getBody();
            openAPI = Yaml.mapper().readValue(body, OpenAPI.class);
        } catch (JsonProcessingException ex) {
            log.warn("Yaml parse problem url : {}", url);
        } catch (Exception e) {
            return null;
        }

        return openAPI;
    }

    protected void updateFields(OpenAPI updatingOpenAPI, OpenAPI openAPI, String moduleName) {
        if (Objects.nonNull(openAPI.getTags())) {
            openAPI.getTags().forEach(updatingOpenAPI::addTagsItem);
        }

        if (Objects.nonNull(openAPI.getServers())) {
            openAPI.getServers().forEach(s -> {
                Server serversItem = new Server();
                serversItem.setDescription(moduleName);
                serversItem.setUrl(s.getUrl());
                updatingOpenAPI.addServersItem(serversItem);
            });
        }

        if (Objects.nonNull(openAPI.getPaths())) {
            openAPI.getPaths().forEach((key, value) -> updatingOpenAPI.getPaths().addPathItem(key, value));
        }

        if (Objects.nonNull(openAPI.getComponents()) && Objects.nonNull(openAPI.getComponents().getSchemas())) {
            openAPI.getComponents().getSchemas().forEach((key, value) -> updatingOpenAPI.getComponents().addSchemas(key, value));
        }
    }
}
