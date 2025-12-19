package EmployeePerformanceTracker;

public class Employee {
	
    private int id;
    private String name;
    private String role;
    private double performanceScore;
    private String rating;
    private String feedback;

    public Employee(int id, String name, String role,
                    double performanceScore, String rating, String feedback) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.performanceScore = performanceScore;
        this.rating = rating;
        this.feedback = feedback;
    }

    public Employee(String name, String role,
                    double performanceScore, String rating, String feedback) {
        this(0, name, role, performanceScore, rating, feedback);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { 
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Name: " + name +
                " | Role: " + role +
                " | Score: " + performanceScore +
                " | Rating: " + rating +
                " | Feedback: " + feedback;
    }
}
