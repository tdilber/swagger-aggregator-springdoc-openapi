package com.beyt.doc.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("springdoc.grouping")
public class GroupedSwaggerConfig {

    private Map<String, Module> modules = new HashMap<>();
    private Boolean enable;

    private Long refreshTimeoutMs;

    private Module baseModule;

    public Boolean getEnable() {
        return enable;
    }

    public Module getBaseModule() {
        return baseModule;
    }

    public Map<String, Module> getModules() {
        return modules;
    }

    public Long getRefreshTimeoutMs() {
        return refreshTimeoutMs;
    }

    public static class Module {
        private String url;
        private String description;

        public void setUrl(String url) {
            this.url = url;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public String getDescription() {
            return description;
        }
    }

    public void setModules(Map<String, Module> modules) {
        this.modules = modules;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setBaseModule(Module baseModule) {
        this.baseModule = baseModule;
    }

    public void setRefreshTimeoutMs(Long refreshTimeoutMs) {
        this.refreshTimeoutMs = refreshTimeoutMs;
    }
}
