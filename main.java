package EmployeePerformanceTracker;

import java.io.IOException;
import java.util.logging.*;

public class Main {

    public static void main(String[] args) {
        configureLogging();

        AuthApp authApp = new AuthApp();
        String loggedInUser = authApp.start();

        if (loggedInUser == null) {
            return;
        }

        System.out.println("\nLogged in as: " + loggedInUser);
        System.out.println("Loading Employee Performance Tracker...\n");

        EmployeeApp app = new EmployeeApp();
        app.start();
    }

    private static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }

        try {
            FileHandler fileHandler = new FileHandler("employee_app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            rootLogger.addHandler(consoleHandler);
        }
    }
}
