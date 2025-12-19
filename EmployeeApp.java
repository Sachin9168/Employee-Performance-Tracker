package EmployeePerformanceTracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class EmployeeApp {

    private static final Logger LOGGER = Logger.getLogger(EmployeeApp.class.getName());

    private final Scanner sc = new Scanner(System.in);
    private final EmployeeDAO dao = new EmployeeDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO(); // logs + KPI

    public void start() {
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice: ", 0, 8);

            try {
                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> viewEmployees();
                    case 3 -> updatePerformance();
                    case 4 -> deleteEmployee();
                    case 5 -> exportEmployeesToCsv();
                    case 6 -> addActivityLog();       // NEW
                    case 7 -> showTodayKpis();
                    case 8 -> exportTodayKpiCsv();
                    case 0 -> {
                        LOGGER.info("Application exiting.");
                        System.out.println("Goodbye.");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Employee Performance Tracker =====");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Update Performance");
        System.out.println("4. Delete Employee");
        System.out.println("5. Export Employees to CSV (Desktop)");
        System.out.println("6. Add Activity Log");
        System.out.println("7. Show TODAY KPI (from activity logs)");
        System.out.println("8. Export TODAY KPI to CSV (Desktop)");
        System.out.println("0. Exit");
        System.out.println("=======================================");
    }

    // --- Employee ---

    private void addEmployee() throws SQLException {
        String name = readNonEmptyString("Enter Name: ");
        String role = readNonEmptyString("Enter Role: ");
        double score = readDouble("Enter Score (0 - 100): ", 0.0, 100.0);
        String rating = calculateRating(score);
        String feedback = readStringAllowEmpty("Enter Feedback: ");

        Employee e = new Employee(name, role, score, rating, feedback);
        dao.addEmployee(e);

        System.out.println("Employee added with rating: " + rating);
        LOGGER.info("Added employee: " + name);

        pause();
    }

    private void viewEmployees() throws SQLException {
        List<Employee> employees = dao.getAllEmployees();
        System.out.println("\nEmployees found: " + employees.size());

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("\n--- Employee List ---");
            for (Employee e : employees) {
                System.out.println(e);
            }
        }

        pause();
    }

    private void updatePerformance() throws SQLException {
        int id = readInt("Enter ID: ", 1, Integer.MAX_VALUE);
        Employee existing = dao.getEmployeeById(id);

        if (existing == null) {
            System.out.println("Employee not found.");
            pause();
            return;
        }

        double score = readDouble("Enter new Score (0 - 100): ", 0.0, 100.0);
        String rating = calculateRating(score);
        String feedback = readStringAllowEmpty("New Feedback: ");

        boolean updated = dao.updatePerformance(id, score, rating, feedback);

        if (updated) {
            System.out.println("Updated. New rating: " + rating);
            LOGGER.info("Updated performance for employee ID " + id);
        } else {
            System.out.println("Update failed.");
        }

        pause();
    }

    private void deleteEmployee() throws SQLException {
        int id = readInt("Enter ID to delete: ", 1, Integer.MAX_VALUE);

        if (dao.deleteEmployee(id)) {
            System.out.println("Employee deleted.");
            LOGGER.info("Deleted employee ID " + id);
        } else {
            System.out.println("Employee not found.");
        }

        pause();
    }

    //     ADD ACTIVITY LOG 

    private void addActivityLog() throws SQLException {
        System.out.println("\n--- Add Activity Log ---");

        int empId = readInt("Enter Employee ID: ", 1, Integer.MAX_VALUE);

        Employee emp = dao.getEmployeeById(empId);
        if (emp == null) {
            System.out.println("❌ Employee not found with ID: " + empId);
            pause();
            return;
        }

        System.out.println("Logging activity for: " + emp.getName() + " (" + emp.getRole() + ")");

        int tasks = readInt("Tasks completed today: ", 0, Integer.MAX_VALUE);
        double hours = readDouble("Hours worked today: ", 0.0, 24.0);
        double quality = readDouble("Quality score (0 - 100): ", 0.0, 100.0);
        String comments = readStringAllowEmpty("Comments (optional): ");

        activityLogDAO.addActivityLog(empId, tasks, hours, quality, comments);

        System.out.println("✅ Activity log added for employee ID: " + empId);
        pause();
    }

    // 5. Export Employees

    private void exportEmployeesToCsv() throws SQLException, IOException {
        List<Employee> employees = dao.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No data to export.");
            pause();
            return;
        }

        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        File file = new File(desktopPath, "employees_export.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Name,Role,Score,Rating,Feedback");
            writer.newLine();

            for (Employee e : employees) {
                writer.write(
                        e.getId() + ",\"" + e.getName() + "\",\"" +
                        e.getRole() + "\"," + e.getPerformanceScore() + ",\"" +
                        e.getRating() + "\",\"" + e.getFeedback() + "\""
                );
                writer.newLine();
            }
        }

        System.out.println("\nEmployee CSV exported successfully!");
        System.out.println("📁 File saved at: " + file.getAbsolutePath());
        LOGGER.info("Employee CSV exported to: " + file.getAbsolutePath());

        pause();
    }

    // 7. Show TODAY KPI

    private void showTodayKpis() throws SQLException {
        List<ActivityKpi> kpis = activityLogDAO.getTodayKpis();

        System.out.println("\n--- TODAY's KPIs (from EMP_ACTIVITY_LOGS) ---");
        if (kpis.isEmpty()) {
            System.out.println("No activity logs found for today.");
        } else {
            for (ActivityKpi k : kpis) {
                System.out.println(k);
            }
        }

        pause();
    }

    // 8. Export TODAY KPI CSV 

    private void exportTodayKpiCsv() throws SQLException, IOException {
        List<ActivityKpi> kpis = activityLogDAO.getTodayKpis();

        if (kpis.isEmpty()) {
            System.out.println("No activity logs found for today. Nothing to export.");
            pause();
            return;
        }

        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File file = new File(desktopPath, "employee_kpi_" + dateStr + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("EmpID,Name,Role,Date,TotalTasks,TotalHours,AvgQuality,TasksPerHour");
            writer.newLine();

            for (ActivityKpi k : kpis) {
                writer.write(
                        k.getEmployeeId() + ",\"" +
                        k.getName() + "\",\"" +
                        k.getRole() + "\"," +
                        k.getActivityDate() + "," +
                        k.getTotalTasks() + "," +
                        k.getTotalHours() + "," +
                        k.getAvgQuality() + "," +
                        k.getTasksPerHour()
                );
                writer.newLine();
            }
        }

        System.out.println("\nKPI CSV for TODAY exported successfully!");
        System.out.println("📁 File saved at: " + file.getAbsolutePath());
        LOGGER.info("KPI CSV exported to: " + file.getAbsolutePath());

        pause();
    }

    private String calculateRating(double score) {
        if (score >= 90) return "Outstanding";
        if (score >= 80) return "Excellent";
        if (score >= 70) return "Good";
        if (score >= 60) return "Average";
        return "Needs Improvement";
    }


    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value < min || value > max) {
                    System.out.println("Value must be between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Input cannot be empty.");
        }
    }

    private String readStringAllowEmpty(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }
}

