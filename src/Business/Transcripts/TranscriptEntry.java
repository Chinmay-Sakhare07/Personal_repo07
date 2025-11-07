/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Transcripts;

import Business.Course.CourseOffering;

/**
 *
 * @author chinm
 */
public class TranscriptEntry {

    private CourseOffering offering;
    private String term;
    private String grade;
    private int creditHours;
    private double qualityPoints;

    public TranscriptEntry(CourseOffering offering, String term, String grade, int creditHours) {
        this.offering = offering;
        this.term = term;
        this.grade = grade;
        this.creditHours = creditHours;
        this.qualityPoints = calculateQualityPoints(grade, creditHours);
    }

    private double calculateQualityPoints(String grade, int credits) {
        if (grade == null || grade.trim().isEmpty()) {
            return 0.0;
        }
        double gp = 0.0;
        switch (grade) {
            case "A":
                gp = 4.0;
                break;
            case "A-":
                gp = 3.7;
                break;
            case "B+":
                gp = 3.3;
                break;
            case "B":
                gp = 3.0;
                break;
            case "B-":
                gp = 2.7;
                break;
            case "C+":
                gp = 2.3;
                break;
            case "C":
                gp = 2.0;
                break;
            case "C-":
                gp = 1.7;
                break;
            case "F":
                gp = 0.0;
                break;
        }
        return gp * credits;
    }

    public CourseOffering getOffering() {
        return offering;
    }

    public String getTerm() {
        return term;
    }

    public String getGrade() {
        return grade;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public double getQualityPoints() {
        return qualityPoints;
    }

    @Override
    public String toString() {
        return offering.getCourse().getCourseId() + " - " + offering.getCourse().getCourseName()
                + " (" + term + ") Grade: " + grade + " Credits: " + creditHours;
    }
}
