package EmployeePerformanceTracker;

import java.sql.SQLException;
import java.util.Scanner;

public class AuthApp {

    private final Scanner sc = new Scanner(System.in);
    private final UserDAO userDAO = new UserDAO();

    public String start() {
        while (true) {
            printAuthMenu();
            int choice = readInt("Enter your choice: ", 0, 3);

            try {
                return switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> forgetPassword();
                    case 0 -> {
                        System.out.println("Exiting application.");
                        yield null;
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        yield null;
                    }
                };
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void printAuthMenu() {
        System.out.println("\n===== Login System =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Forgot Password");
        System.out.println("0. Exit");
        System.out.println("========================");
    }
    
    //  REGISTER 
    private String register() throws SQLException {
        System.out.println("\n--- User Registration ---");

        String username;
        while (true) {
            username = readNonEmptyString("Create username: ");
            if (username.length() < 3) {
                System.out.println("Username must be at least 3 characters.");
                continue;
            }
            if (userDAO.usernameExists(username)) {
                System.out.println("This username already exists. Try another.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            password = readNonEmptyString("Create password: ");
            if (password.length() < 4) {
                System.out.println("Password must be at least 4 characters.");
                continue;
            }
            String confirm = readNonEmptyString("Confirm password: ");
            if (!password.equals(confirm)) {
                System.out.println("Passwords do not match. Try again.");
                continue;
            }
            break;
        }

        System.out.println("\nSet up a security question for password recovery.");
        String question = readNonEmptyString("Security question: ");
        String answer = readNonEmptyString("Security answer: ");

        UserAccount user = new UserAccount(username, password, question, answer);
        userDAO.registerUser(user);

        System.out.println("Registration successful. You can now login.");
        return null; 
    }

    // LOGIN 
    private String login() throws SQLException {
        System.out.println("\n--- Login ---");
        String username = readNonEmptyString("Username: ");
        String password = readNonEmptyString("Password: ");

        boolean ok = userDAO.validateLogin(username, password);
        if (ok) {
            System.out.println("Login successful. Welcome, " + username + "!");
            return username;
        } else {
            System.out.println("Invalid username or password.");
            return null;
        }
    }

    //  FORGOT PASSWORD 

    private String forgetPassword() throws SQLException {
        System.out.println("\n--- Password Recovery ---");
        String username = readNonEmptyString("Enter username: ");

        if (!userDAO.usernameExists(username)) {
            System.out.println("❌ User not found.");
            return null;
        }

        String question = userDAO.getSecurityQuestion(username);

        
        if (question == null || question.trim().isEmpty()) {
            System.out.println("⚠ This account does NOT have a recovery question set.");
            System.out.println("You cannot reset password using 'Forgot Password'.");
            System.out.println("👉 Create a new account or ask admin to reset it manually.");
            return null;
        }

        System.out.println("\nSecurity Question: " + question);
        String answer = readNonEmptyString("Answer: ");

        boolean ok = userDAO.verifyAnswer(username, answer);
        if (!ok) {
            System.out.println("❌ Incorrect answer. Password reset denied.");
            return null;
        }

        String newPass;
        while (true) {
            newPass = readNonEmptyString("Enter new password: ");
            if (newPass.length() < 4) {
                System.out.println("Password must be at least 4 characters.");
                continue;
            }
            String confirm = readNonEmptyString("Confirm new password: ");
            if (!newPass.equals(confirm)) {
                System.out.println("Passwords do not match. Try again.");
                continue;
            }
            break;
        }

        boolean changed = userDAO.resetPassword(username, newPass);
        if (changed) {
            System.out.println("✔ Password updated successfully. Please login again.");
        } else {
            System.out.println("❌ Password update failed (DB issue).");
        }
        return null;  
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

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}
