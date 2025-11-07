/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Profiles;

import Business.Course.Enrollment;
import Business.Person.Person;
import Business.Transcripts.Transcript;
import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class StudentProfile extends Profile {

    private Transcript transcript;
    private StudentAccount account;
    private ArrayList<Enrollment> enrollments;

    public StudentProfile(Person p) {
        super(p);
        this.transcript = new Transcript();
        this.account = new StudentAccount();
        this.enrollments = new ArrayList<>();

    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public boolean isMatch(String id) {
        return person.getPersonId().equals(id);
    }

    public void addEnrollment(Enrollment e) {
        enrollments.add(e);
        transcript.addTranscriptEntry(e);
        account.billTuition(e.getOffering().getCourse().getCreditHours() * 1000.0);
    }

    public ArrayList<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public StudentAccount getAccount() {
        return account;
    }

    public boolean isReadyToGraduate() {
        double totalCredits = transcript.getTotalCredits();
        boolean hasCore = transcript.hasCompletedCourse("INFO5100");
        return totalCredits >= 32 && hasCore;
    }

    public int getCurrentTermCredits(String term) {
        int sum = 0;
        for (Enrollment e : enrollments) {
            if (e.getOffering().getSemester().equalsIgnoreCase(term)) {
                sum += e.getOffering().getCourse().getCreditHours();
            }
        }
        return sum;
    }

}
