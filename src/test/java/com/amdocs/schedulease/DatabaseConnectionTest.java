package com.amdocs.schedulease;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            
            System.out.println("✅ Database connection successful!");
            System.out.println("Database: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Version: " + connection.getMetaData().getDatabaseProductVersion());
        }
    }

    @Test
    public void testJdbcTemplate() {
        String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
        assertEquals("1", result, "JDBC Template query should work");
        System.out.println("✅ JDBC Template is working!");
    }

    @Test
    public void testDatabaseName() {
        String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        // MySQL on Windows is case-insensitive, so use equalsIgnoreCase
        assertTrue(dbName.equalsIgnoreCase("schedulEase"), "Should connect to schedulEase database");
        System.out.println("✅ Connected to database: " + dbName);
    }
}
