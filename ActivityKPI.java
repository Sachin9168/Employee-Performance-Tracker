package EmployeePerformanceTracker;

import java.sql.Date;

public class ActivityKpi {

    private int employeeId;
    private String name;
    private String role;
    private Date activityDate;
    private int totalTasks;
    private double totalHours;
    private double avgQuality;
    private double tasksPerHour;

    public ActivityKpi(int employeeId, String name, String role, Date activityDate,
                       int totalTasks, double totalHours, double avgQuality, double tasksPerHour) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
        this.activityDate = activityDate;
        this.totalTasks = totalTasks;
        this.totalHours = totalHours;
        this.avgQuality = avgQuality;
        this.tasksPerHour = tasksPerHour;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public Date getActivityDate() { return activityDate; }
    public int getTotalTasks() { return totalTasks; }
    public double getTotalHours() { return totalHours; }
    public double getAvgQuality() { return avgQuality; }
    public double getTasksPerHour() { return tasksPerHour; }

    @Override
    public String toString() {
        return "EmpID: " + employeeId +
               " | Name: " + name +
               " | Role: " + role +
               " | Date: " + activityDate +
               " | Tasks: " + totalTasks +
               " | Hours: " + totalHours +
               " | AvgQuality: " + avgQuality +
               " | Tasks/Hour: " + tasksPerHour;
    }
}
