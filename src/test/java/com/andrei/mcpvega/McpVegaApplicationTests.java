package com.andrei.mcpvega;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class McpVegaApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource bean should not be null");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            System.out.println(" Connected to: " + connection.getMetaData().getDatabaseProductName());
            System.out.println(" Database version: " + connection.getMetaData().getDatabaseProductVersion());
        }
    }

}
