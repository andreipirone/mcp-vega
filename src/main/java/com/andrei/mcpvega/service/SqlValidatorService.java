package com.andrei.mcpvega.service;

import com.andrei.mcpvega.exception.InvalidSqlQueryException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Service;

@Service
public class SqlValidatorService {

    public void readOnlyValidator(String query){
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
