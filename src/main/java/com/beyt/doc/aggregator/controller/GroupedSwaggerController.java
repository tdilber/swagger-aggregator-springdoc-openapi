package com.beyt.doc.aggregator.controller;

import com.beyt.doc.aggregator.service.GroupedSwaggerService;
import com.beyt.doc.aggregator.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Yaml;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty("springdoc.grouping.enable")
public class GroupedSwaggerController {
    private final GroupedSwaggerService groupedSwaggerService;

    @GetMapping(Constants.DOCUMENT_URL)
    public String groupedApiDocs() throws JsonProcessingException {
        return Yaml.mapper().writeValueAsString(groupedSwaggerService.getCurrentOpenAPI());
    }
}
