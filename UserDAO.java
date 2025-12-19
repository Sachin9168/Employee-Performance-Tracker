package EmployeePerformanceTracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM USERS WHERE USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void registerUser(UserAccount user) throws SQLException {
        String sql = "INSERT INTO USERS (ID, USERNAME, PASSWORD, SECURITY_QUESTION, SECURITY_ANSWER, CREATED_AT) " +
                     "VALUES (USER_SEQ.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getSecurityQuestion());
            ps.setString(4, user.getSecurityAnswer());
            ps.executeUpdate();
        }
    }

    public boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return rs.getString("PASSWORD").equals(password);
            }
        }
    }

    public String getSecurityQuestion(String username) throws SQLException {
        String sql = "SELECT SECURITY_QUESTION FROM USERS WHERE USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public boolean verifyAnswer(String username, String answer) throws SQLException {
        String sql = "SELECT SECURITY_ANSWER FROM USERS WHERE USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getString(1).equalsIgnoreCase(answer);
        }
    }

    public boolean resetPassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }
}

