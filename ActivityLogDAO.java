package EmployeePerformanceTracker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
	
    public List<ActivityKpi> getTodayKpis() throws SQLException {
        List<ActivityKpi> list = new ArrayList<>();

        String sql =
            "SELECT e.ID AS EMP_ID, e.NAME, e.ROLE, " +
            "       TRUNC(a.ACTIVITY_DATE) AS ACT_DATE, " +
            "       SUM(NVL(a.TASKS_COMPLETED, 0)) AS TOTAL_TASKS, " +
            "       SUM(NVL(a.HOURS_WORKED, 0))    AS TOTAL_HOURS, " +
            "       ROUND(AVG(NVL(a.QUALITY_SCORE, 0)), 2) AS AVG_QUALITY, " +
            "       ROUND( CASE WHEN SUM(NVL(a.HOURS_WORKED,0)) = 0 THEN 0 " +
            "                   ELSE SUM(NVL(a.TASKS_COMPLETED,0)) / SUM(NVL(a.HOURS_WORKED,0)) " +
            "              END, 2) AS TASKS_PER_HOUR " +
            "FROM EMPLOYEES e " +
            "JOIN EMP_ACTIVITY_LOGS a ON e.ID = a.EMP_ID " +
            "WHERE TRUNC(a.ACTIVITY_DATE) = TRUNC(SYSDATE) " +
            "GROUP BY e.ID, e.NAME, e.ROLE, TRUNC(a.ACTIVITY_DATE) " +
            "ORDER BY TOTAL_TASKS DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActivityKpi kpi = new ActivityKpi(
                    rs.getInt("EMP_ID"),
                    rs.getString("NAME"),
                    rs.getString("ROLE"),
                    rs.getDate("ACT_DATE"),
                    rs.getInt("TOTAL_TASKS"),
                    rs.getDouble("TOTAL_HOURS"),
                    rs.getDouble("AVG_QUALITY"),
                    rs.getDouble("TASKS_PER_HOUR")
                );
                list.add(kpi);
            }
        }

        return list;
    }
    
    public void addActivityLog(int empId,
                               int tasksCompleted,
                               double hoursWorked,
                               double qualityScore,
                               String comments) throws SQLException {

        String sql = "INSERT INTO EMP_ACTIVITY_LOGS " +
                     "(ID, EMP_ID, ACTIVITY_DATE, TASKS_COMPLETED, HOURS_WORKED, QUALITY_SCORE, COMMENTS) " +
                     "VALUES (EMP_ACTIVITY_SEQ.NEXTVAL, ?, SYSDATE, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ps.setInt(2, tasksCompleted);
            ps.setDouble(3, hoursWorked);
            ps.setDouble(4, qualityScore);
            ps.setString(5, comments);

            ps.executeUpdate();
        }
    }
}
