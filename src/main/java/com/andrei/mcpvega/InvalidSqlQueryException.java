package com.andrei.mcpvega;

public class InvalidSqlQueryException extends RuntimeException {
    public InvalidSqlQueryException(String message) {
        super(message);
    }

    public InvalidSqlQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
