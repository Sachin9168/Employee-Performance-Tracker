package EmployeePerformanceTracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleDriver;

public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String URL  = "jdbc:oracle:thin:@//localhost:1521/oracle";
    private static final String USER = "SCOTT";
    private static final String PASS = "Sachin";

    static {
        try {
            DriverManager.registerDriver(new OracleDriver());
            LOGGER.info("Oracle JDBC driver registered successfully.");
        } catch (SQLException e) {
            LOGGER.severe("Failed to register Oracle JDBC driver: " + e.getMessage());
            throw new RuntimeException("Failed to register Oracle JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        LOGGER.fine("Opening database connection...");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

	


