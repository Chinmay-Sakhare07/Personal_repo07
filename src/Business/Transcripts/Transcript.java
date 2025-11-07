/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Transcripts;

/**
 *
 * @author chinm
 */
import Business.Course.Enrollment;
import java.util.ArrayList;

public class Transcript {

    private ArrayList<TranscriptEntry> entries;

    public Transcript() {
        entries = new ArrayList<>();
    }

    public void addTranscriptEntry(Enrollment e) {
        TranscriptEntry te = new TranscriptEntry(
                e.getOffering(),
                e.getOffering().getSemester(),
                e.getGrade(),
                e.getOffering().getCourse().getCreditHours()
        );
        entries.add(te);
    }

    public ArrayList<TranscriptEntry> getEntries() {
        return entries;
    }

    public double calculateTermGPA(String term) {
        double totalQualityPoints = 0;
        int totalCredits = 0;
        for (TranscriptEntry te : entries) {
            if (te.getTerm().equalsIgnoreCase(term) && te.getGrade() != null) {
                totalQualityPoints += te.getQualityPoints();
                totalCredits += te.getCreditHours();
            }
        }
        return totalCredits == 0 ? 0.0 : totalQualityPoints / totalCredits;
    }

    public double calculateOverallGPA() {
        double totalQualityPoints = 0;
        int totalCredits = 0;
        for (TranscriptEntry te : entries) {
            if (te.getGrade() != null) {
                totalQualityPoints += te.getQualityPoints();
                totalCredits += te.getCreditHours();
            }
        }
        return totalCredits == 0 ? 0.0 : totalQualityPoints / totalCredits;
    }

    public int getTotalCredits() {
        int sum = 0;
        for (TranscriptEntry te : entries) {
            if (te.getGrade() != null && !te.getGrade().equals("F")) {
                sum += te.getCreditHours();
            }
        }
        return sum;
    }

    public boolean hasCompletedCourse(String courseId) {
        for (TranscriptEntry te : entries) {
            if (te.getOffering().getCourse().getCourseId().equalsIgnoreCase(courseId)
                    && te.getGrade() != null && !"F".equalsIgnoreCase(te.getGrade())) {
                return true;
            }
        }
        return false;
    }

    public int getCurrentTermCredits(String term) {
        int sum = 0;
        for (TranscriptEntry te : entries) {
            if (te.getTerm().equalsIgnoreCase(term)) {
                sum += te.getCreditHours();
            }
        }
        return sum;
    }
    
    public String determineStanding(String term) {
        if (entries.isEmpty()) {
            return "Good Standing";
        }

        double termGPA = calculateTermGPA(term);
        double overallGPA = calculateOverallGPA();

        if (overallGPA < 3.0) {
            return "Academic Probation";
        }

        if (termGPA < 3.0) {
            return "Academic Warning";
        }

        return "Good Standing";
    }

    public String determineStanding() {
        if (entries.isEmpty()) {
            return "Good Standing";
        }

        double overallGPA = calculateOverallGPA();

        if (overallGPA < 3.0) {
            return "Academic Probation";
        }

        return "Good Standing";
    }

    public ArrayList<String> getAllTerms() {
        ArrayList<String> terms = new ArrayList<>();
        for (TranscriptEntry te : entries) {
            if (!terms.contains(te.getTerm())) {
                terms.add(te.getTerm());
            }
        }
        return terms;
    }

    public ArrayList<TranscriptEntry> getEntriesByTerm(String term) {
        ArrayList<TranscriptEntry> termEntries = new ArrayList<>();
        for (TranscriptEntry te : entries) {
            if (te.getTerm().equalsIgnoreCase(term)) {
                termEntries.add(te);
            }
        }
        return termEntries;
    }

}
