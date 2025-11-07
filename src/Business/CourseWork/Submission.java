/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.CourseWork;

/**
 *
 * @author chinm
 */
import Business.Profiles.StudentProfile;
import java.util.Date;

public class Submission {

    private String submissionId;
    private StudentProfile student;
    private Assignment assignment;
    private Date submissionDate;
    private int pointsEarned;
    private String feedback;
    private String status;

    public Submission(StudentProfile student, Assignment assignment) {
        this.submissionId = generateSubmissionId(student, assignment);
        this.student = student;
        this.assignment = assignment;
        this.submissionDate = new Date();
        this.pointsEarned = 0;
        this.status = "Submitted";
    }

    private String generateSubmissionId(StudentProfile student, Assignment assignment) {
        return student.getPerson().getPersonId() + "_"
                + assignment.getAssignmentId() + "_"
                + System.currentTimeMillis();
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
        this.status = "Graded";
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLate() {
        return submissionDate.after(assignment.getDueDate());
    }

    public double getPercentage() {
        if (assignment.getMaxPoints() == 0) {
            return 0.0;
        }
        return (pointsEarned * 100.0) / assignment.getMaxPoints();
    }

    @Override
    public String toString() {
        return student.getPerson().getFullName() + " - "
                + assignment.getTitle() + ": "
                + pointsEarned + "/" + assignment.getMaxPoints()
                + " (" + status + ")";
    }
}
