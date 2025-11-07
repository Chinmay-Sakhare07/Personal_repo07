/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Course;

import Business.Profiles.StudentProfile;
import Business.Transcripts.TranscriptEntry;

/**
 *
 * @author pranjalpatil
 */
public class Enrollment {

    private StudentProfile student;
    private CourseOffering offering;
    private String status;
    private String grade;

    public Enrollment(StudentProfile student, CourseOffering offering) {
        this.student = student;
        this.offering = offering;
        this.status = "Enrolled";
        this.grade = null;
    }

    public void assignGrade(String grade) {
        this.grade = grade;
        this.status = "Completed";


        TranscriptEntry te = new TranscriptEntry(
                offering,
                offering.getSemester(),
                grade,
                offering.getCourse().getCreditHours()
        );
        student.getTranscript().getEntries().add(te);
    }

    public void drop() {
        this.status = "Dropped";
    }

    public StudentProfile getStudent() {
        return student;
    }

    public CourseOffering getOffering() {
        return offering;
    }

    public String getStatus() {
        return status;
    }

    public String getGrade() {
        return grade;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return student.getPerson().getPersonId() + " enrolled in "
                + offering.getCourse().getCourseId() + " (" + status + ")";
    }

}
