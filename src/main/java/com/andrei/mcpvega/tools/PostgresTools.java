package com.andrei.mcpvega.tools;

import com.andrei.mcpvega.service.SqlValidatorService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class PostgresTools {
    private final JdbcClient jdbcClient;
    private final SqlValidatorService sqlValidatorService;

    public PostgresTools(JdbcClient jdbcClient, SqlValidatorService sqlValidatorService) {
        this.jdbcClient = jdbcClient;
        this.sqlValidatorService = sqlValidatorService;
    }

    @McpTool(name="list-all-tables", description = "List all public tables and their row counts in the PostgreSQL database")
    public List<Map<String, Object>> listAllTables(){
        String sql = """
            SELECT
                relname AS table_name,
                n_live_tup AS row_count
            FROM pg_stat_user_tables
            WHERE schemaname = 'public'
            ORDER BY table_name;
        """;

        return jdbcClient.sql(sql).query().listOfRows();
    }

    @McpTool(name="describe-table", description = "Retrieve column names and data types for a given table name")
    public List<Map<String, Object>> describeTable(@McpToolParam(description = "The name of the table to describe, e.g., 'users'") String tableName){
        String sql = """
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = :tableName
                ORDER BY ordinal_position;
        """;

        return jdbcClient.sql(sql).param("tableName", tableName).query().listOfRows();
    }

    @McpTool(name="query-database", description = """
            Executes a validated, read-only SQL query against the target database to fetch tabular data, 
            compute aggregates, or inspect table definitions.
            
            IMPORTANT DATA REDUCTION RULES:
                    - Result sets MUST NOT exceed 50 rows.
                    - If visualizing or summarizing data with many rows (~10,000+), do not use raw SELECT.
                    - If timestamps exist, bucket them using date_trunc or date_bin to <= 50 intervals.
                    - If continuous numeric data, use NTILE(50) OVER (...) and aggregate (MIN, MAX, AVG).
                    - If categorical data has high cardinality, pick the TOP 49 by count and group the rest into 'Other'.
                    - If general trend sampling is needed, sample evenly:\s
                      WHERE rn % CEIL(total_rows / 50.0) = 0.
                    - Always append LIMIT 50 as a safeguard.
            """)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryDatabase(@McpToolParam(description = "A valid read-only SQL query (SELECT statement). Do not include destructive statements (INSERT, UPDATE, DELETE, DROP). Include appropriate WHERE clauses.") String query){

        sqlValidatorService.readOnlyValidator(query);

        jdbcClient.sql("SET TRANSACTION READ ONLY").update();

        return jdbcClient.sql(query).query().listOfRows();
    }

    @McpTool(name="get-current-date-time", description = "It gets the current date and time.")
    public String getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return dateTime.format(dateTimeFormatter);
    }


}
