/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Course;

import Business.Profiles.FacultyProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pranjalpatil
 */
public class CourseOffering {

    private Course course;
    private String semester;
    private FacultyProfile faculty;
    private int capacity;
    private boolean enrollmentOpen;
    private ArrayList<Enrollment> enrollments;

    public CourseOffering(Course course, String semester, FacultyProfile faculty, int capacity, boolean enrollmentOpen) {
        this.course = course;
        this.semester = semester;
        this.faculty = faculty;
        this.capacity = capacity;
        this.enrollmentOpen = enrollmentOpen;
        this.enrollments = new ArrayList<>();
    }

    public boolean isEnrollmentOpen() {
        return enrollmentOpen;
    }

    public void setEnrollmentOpen(boolean open) {
        this.enrollmentOpen = open;
    }

    public Course getCourse() {
        return course;
    }

    public FacultyProfile getFaculty() {
        return faculty;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setFaculty(FacultyProfile faculty) {
        this.faculty = faculty;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEnrollments(ArrayList<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getSemester() {
        return semester;
    }

    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }

    public boolean enrollStudent(Enrollment e) {
        if (!enrollmentOpen) {
            return false;
        }
        if (enrollments.size() >= capacity) {
            return false;
        }
        enrollments.add(e);
        return true;
    }

    public double calculateTotalTuitionCollected() {
        double total = 0.0;
        int creditHours = course.getCreditHours();
        double tuitionPerCredit = 1000.0; // $1000 per credit hour

        for (Enrollment e : enrollments) {
            if (!e.getStatus().equalsIgnoreCase("Dropped")) {
                total += creditHours * tuitionPerCredit;
            }
        }
        return total;
    }

    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new HashMap<>();

        for (Enrollment e : enrollments) {
            String grade = e.getGrade();
            if (grade != null && !grade.trim().isEmpty()) {
                distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
            }
        }
        return distribution;
    }

    public double calculateAverageGrade() {
        double totalGPA = 0.0;
        int count = 0;

        for (Enrollment e : enrollments) {
            String grade = e.getGrade();
            if (grade != null && !grade.trim().isEmpty()) {
                totalGPA += gradeToGPA(grade);
                count++;
            }
        }

        return count > 0 ? totalGPA / count : 0.0;
    }

    private double gradeToGPA(String grade) {
        switch (grade) {
            case "A":
                return 4.0;
            case "A-":
                return 3.7;
            case "B+":
                return 3.3;
            case "B":
                return 3.0;
            case "B-":
                return 2.7;
            case "C+":
                return 2.3;
            case "C":
                return 2.0;
            case "C-":
                return 1.7;
            case "F":
                return 0.0;
            default:
                return 0.0;
        }
    }

    public int getActiveEnrollmentCount() {
        int count = 0;
        for (Enrollment e : enrollments) {
            if (!e.getStatus().equalsIgnoreCase("Dropped")) {
                count++;
            }
        }
        return count;
    }

    public boolean isFull() {
        return getActiveEnrollmentCount() >= capacity;
    }

    public int getAvailableSeats() {
        return capacity - getActiveEnrollmentCount();
    }
}
