package EmployeePerformanceTracker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmployeeDAO {

    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());

    public void addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO EMPLOYEES " +
                     "(ID, NAME, ROLE, PERFORMANCE_SCORE, RATING, FEEDBACK, LAST_UPDATED) " +
                     "VALUES (EMP_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getRole());
            ps.setDouble(3, e.getPerformanceScore());
            ps.setString(4, e.getRating());
            ps.setString(5, e.getFeedback());

            int rows = ps.executeUpdate();
            LOGGER.info("Inserted " + rows + " employee(s).");
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT ID, NAME, ROLE, PERFORMANCE_SCORE, RATING, FEEDBACK " +
                     "FROM EMPLOYEES ORDER BY ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getString("ROLE"),
                        rs.getDouble("PERFORMANCE_SCORE"),
                        rs.getString("RATING"),
                        rs.getString("FEEDBACK")
                );
                list.add(e);
            }
        }
        LOGGER.info("Fetched " + list.size() + " employee(s).");
        return list;
    }

    public Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT ID, NAME, ROLE, PERFORMANCE_SCORE, RATING, FEEDBACK " +
                     "FROM EMPLOYEES WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("ID"),
                            rs.getString("NAME"),
                            rs.getString("ROLE"),
                            rs.getDouble("PERFORMANCE_SCORE"),
                            rs.getString("RATING"),
                            rs.getString("FEEDBACK")
                    );
                }
            }
        }
        return null;
    }

    public boolean updatePerformance(int id, double score, String rating, String feedback) throws SQLException {
        String sql = "UPDATE EMPLOYEES " +
                     "SET PERFORMANCE_SCORE = ?, RATING = ?, FEEDBACK = ?, LAST_UPDATED = SYSDATE " +
                     "WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, score);
            ps.setString(2, rating);
            ps.setString(3, feedback);
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            LOGGER.info("Updated " + rows + " row(s) for employee ID " + id);
            return rows > 0;
        }
    }

    public boolean deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM EMPLOYEES WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            LOGGER.info("Deleted " + rows + " row(s) for employee ID " + id);
            return rows > 0;
        }
    }
}
