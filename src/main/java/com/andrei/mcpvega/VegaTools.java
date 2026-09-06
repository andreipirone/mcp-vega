package com.andrei.mcpvega;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class VegaTools {

    private final VegaTemplateService vegaTemplateService;

    public VegaTools(VegaTemplateService vegaTemplateService) {
        this.vegaTemplateService = vegaTemplateService;
    }



    @McpTool(name = "generate-vega-lite-chart", description = """
            Retrieves a reference Vega-Lite v6 template for a specific chart type. 
            Use this template as a strict structural guide when producing the final spec.            
            """)
    String generateVegaChart(@McpToolParam(description = "The target chart format. " +
            "Allowed values (case-insensitive): 'bar', 'line', 'pie'. " +
            "Do not supply file extensions or custom names.") String chartType,
                             @McpToolParam(description = "The dataset to embed into the chart, " +
                                     "formatted as a valid JSON array of objects") String jsonData){

        String template = vegaTemplateService.loadTemplate(chartType).get();

        String prompt = """
            You are an expert Vega-Lite generator.
            Given the input data and a reference template, create a valid, runnable Vega-Lite JSON specification.
            
            Rules:
            1. Inject the data inside the `data.values` array.
            2. Follow the visual structure and encodings of the reference template.
            3. Infer proper data types: 'nominal', 'quantitative', 'temporal', or 'ordinal'.
            4. Output ONLY valid, raw JSON (no markdown fences, no preamble).

            Target Chart Type: %s
            
            Reference Template:
            %s

            Input Data:
            %s
            """.formatted(chartType, template, jsonData);

        return prompt;

    }
}
