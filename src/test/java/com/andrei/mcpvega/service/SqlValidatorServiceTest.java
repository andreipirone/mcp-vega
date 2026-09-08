package com.andrei.mcpvega.service;

import com.andrei.mcpvega.exception.InvalidSqlQueryException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlValidatorServiceTest {

    private final SqlValidatorService service = new SqlValidatorService();

    @Test
    void allowsSimpleSelect() {
        assertDoesNotThrow(() -> service.readOnlyValidator("SELECT 1"));
    }

    @Test
    void allowsSelectFromTable() {
        assertDoesNotThrow(() -> service.readOnlyValidator("SELECT id, name FROM users WHERE active = true"));
    }

    @Test
    void allowsAggregateSelect() {
        assertDoesNotThrow(() -> service.readOnlyValidator("SELECT category, COUNT(*) FROM products GROUP BY category"));
    }

    @Test
    void rejectsInsert() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("INSERT INTO users (name) VALUES ('a')"));
    }

    @Test
    void rejectsUpdate() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("UPDATE users SET name = 'a'"));
    }

    @Test
    void rejectsDelete() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("DELETE FROM users"));
    }

    @Test
    void rejectsDrop() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("DROP TABLE users"));
    }

    @Test
    void rejectsTruncate() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("TRUNCATE TABLE users"));
    }

    @Test
    void rejectsCreate() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("CREATE TABLE x (id int)"));
    }

    @Test
    void rejectsAlter() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("ALTER TABLE users ADD COLUMN x int"));
    }

    @Test
    void rejectsGrant() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("GRANT SELECT ON users TO public"));
    }

    @Test
    void rejectsInvalidSyntax() {
        assertThrows(InvalidSqlQueryException.class,
                () -> service.readOnlyValidator("NOT VALID SQL AT ALL"));
    }

    @Test
    void rejectsEmptyString() {
        assertThrows(InvalidSqlQueryException.class, () -> service.readOnlyValidator(""));
    }

    @Test
    void rejectsWhitespaceOnly() {
        assertThrows(InvalidSqlQueryException.class, () -> service.readOnlyValidator("   \n\t  "));
    }
}
