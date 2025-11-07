/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.CourseWork;

/**
 *
 * @author chinm
 */
import Business.Course.CourseOffering;
import java.util.ArrayList;
import java.util.Date;

public class Assignment {

    private String assignmentId;
    private String title;
    private String description;
    private Date dueDate;
    private int maxPoints;
    private CourseOffering courseOffering;
    private ArrayList<Submission> submissions;

    public Assignment(String assignmentId, String title, String description,
            Date dueDate, int maxPoints, CourseOffering courseOffering) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxPoints = maxPoints;
        this.courseOffering = courseOffering;
        this.submissions = new ArrayList<>();
    }

    // Getters and Setters
    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public CourseOffering getCourseOffering() {
        return courseOffering;
    }

    public void setCourseOffering(CourseOffering courseOffering) {
        this.courseOffering = courseOffering;
    }

    public boolean isOverdue() {
        return new Date().after(dueDate);
    }

    @Override
    public String toString() {
        return title + " (Max: " + maxPoints + " pts, Due: " + dueDate + ")";
    }

    public void addSubmission(Submission submission) {
        submissions.add(submission);
    }

    public ArrayList<Submission> getSubmissions() {
        return submissions;
    }

    public Submission findSubmissionByStudent(String studentId) {
        for (Submission s : submissions) {
            if (s.getStudent().getPerson().getPersonId().equals(studentId)) {
                return s;
            }
        }
        return null;
    }

    public int getSubmissionCount() {
        return submissions.size();
    }

    public int getGradedSubmissionCount() {
        int count = 0;
        for (Submission s : submissions) {
            if (s.getStatus().equals("Graded")) {
                count++;
            }
        }
        return count;
    }

    public double getAverageGrade() {
        if (submissions.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        int gradedCount = 0;

        for (Submission s : submissions) {
            if (s.getStatus().equals("Graded")) {
                total += s.getPointsEarned();
                gradedCount++;
            }
        }
        return gradedCount > 0 ? total / gradedCount : 0.0;
    }
}
