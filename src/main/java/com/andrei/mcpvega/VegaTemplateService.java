package com.andrei.mcpvega;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class VegaTemplateService {

    private final ResourceLoader resourceLoader;

    public VegaTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public Optional<String> loadTemplate(String chartType){
        Resource resource = resourceLoader.getResource("classpath:vega-templates/" + chartType + ".json");

        try {
            return Optional.of(resource.getContentAsString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Vega template for type: " + chartType, e);
        }
    }
}
