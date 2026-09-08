package com.andrei.mcpvega.tools;

import com.andrei.mcpvega.exception.ToolValidationException;
import com.andrei.mcpvega.model.ChartType;
import com.andrei.mcpvega.service.VegaTemplateService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class VegaTools {

    private final VegaTemplateService vegaTemplateService;

    public VegaTools(VegaTemplateService vegaTemplateService) {
        this.vegaTemplateService = vegaTemplateService;
    }

    @McpTool(name = "list-available-chart-formats", description = """
        Returns the complete catalog of supported chart types, visual formats, and style presets available for generation (e.g., bar, line, pie).
        Call this tool whenever the user asks what kinds of charts can be created, requests recommendations for visualizing specific data, or specifies an ambiguous or unsupported chart type.
        Use the returned list to validate available formats before fetching a template or generating a spec.
        """
    )
    List<String> listChartFormats(){
        return Arrays.stream(ChartType.values()).map(ChartType::key).toList();
    }

    @McpTool(
            name = "generate-vega-lite-chart",
            description = """
        Fetches the valid, canonical Vega-Lite v6 schema/template for creating charts and data visualizations (e.g., bar, line, scatter, heatmap).
        ALWAYS call this tool first whenever the user asks to generate, build, plot, or update a Vega-Lite visualization or chart.
        Do not fabricate or guess the Vega-Lite JSON specification from memory; retrieve this template first to serve as the strict structural foundation for your final output.
        """
    )
    String generateVegaChart(@McpToolParam(description = "The target chart format. Allowed values (case-insensitive): 'bar', 'line', 'pie'. Do not supply file extensions or custom names.") String chartType,
                             @McpToolParam(description = "The dataset to embed into the chart, " +
                                     "formatted as a valid JSON array of objects") String jsonData){

        ChartType type = ChartType.fromKey(chartType)
                .orElseThrow(() -> new ToolValidationException(
                        "Unsupported chart type: '" + chartType + "'. Supported types: " + ChartType.supportedKeys()));

        String template = vegaTemplateService.loadTemplate(type.key())
                .orElseThrow(() -> new IllegalStateException(
                        "Vega template is missing for chart type: " + type.key()));

        return """
            You are an expert Vega-Lite generator.
            Given the input data and a reference template, create a valid, runnable Vega-Lite JSON specification.

            Rules:
            1. Inject the data inside the `data.values` array.
            2. Follow the visual structure and encodings of the reference template.
            3. Infer proper data types: 'nominal', 'quantitative', 'temporal', or 'ordinal'.
            4. Output exactly one fenced block wrapped as ```vega ... ```. Do not include any preamble, conversational text, or additional markdown fences.

            Target Chart Type: %s

            Reference Template:
            %s

            Input Data:
            %s
            """.formatted(type.key(), template, jsonData);
    }
}
