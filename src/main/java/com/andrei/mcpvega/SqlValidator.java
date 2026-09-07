package com.andrei.mcpvega;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;

public class SqlValidator {

    public static void readOnlyValidator(String query){
        try {
            Statement statement = CCJSqlParserUtil.parse(query);

            if (!(statement instanceof Select)) {
                throw new InvalidSqlQueryException("Only SELECT queries are permitted.");
            }
        } catch (InvalidSqlQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidSqlQueryException("Invalid SQL query syntax or non-read-only statement: " + e.getMessage());
        }

    }

}
