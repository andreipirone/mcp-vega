package com.andrei.mcpvega;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class PostgresTools {
    private final JdbcClient jdbcClient;

    public PostgresTools(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @McpTool(name="list-all-tables", description = "List all public tables and their row counts in the PostgreSQL database")
    public List<Map<String, Object>> listAllTables(){
        String sql = """
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public'
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

    @McpTool(name="query-database", description = "Executes a validated, read-only SQL query against the target database to fetch tabular data, compute aggregates, or inspect table definitions.")
    public List<Map<String, Object>> queryDatabase(@McpToolParam(description = "A valid read-only SQL query (SELECT statement). Do not include destructive statements (INSERT, UPDATE, DELETE, DROP). Include appropriate WHERE clauses and a LIMIT of 2.") String query){
        return jdbcClient.sql(query).query().listOfRows();
    }

    @McpTool(name="get-current-date", description = "It gets the current date.")
    public String getTodoItems() {
       return LocalDate.now().toString();
    }


}
