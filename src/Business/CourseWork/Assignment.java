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
import java.util.Date;

public class Assignment {

    private String assignmentId;
    private String title;
    private String description;
    private Date dueDate;
    private int maxPoints;
    private CourseOffering courseOffering;

    public Assignment(String assignmentId, String title, String description,
            Date dueDate, int maxPoints, CourseOffering courseOffering) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxPoints = maxPoints;
        this.courseOffering = courseOffering;
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
}
