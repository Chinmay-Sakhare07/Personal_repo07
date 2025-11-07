/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Profiles;

import Business.Course.CourseOffering;
import java.util.ArrayList;

/**
 *
 * @author chinm
 */
public class CourseDirectory {

    private ArrayList<CourseOffering> offerings;

    public CourseDirectory() {
        offerings = new ArrayList<>();
    }

    public ArrayList<CourseOffering> getOfferings() {
        return offerings;
    }

    public void addOffering(CourseOffering co) {
        offerings.add(co);
    }

    public ArrayList<CourseOffering> searchByCourseId(String courseId) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        if (courseId == null || courseId.trim().isEmpty()) {
            return results;
        }

        String searchId = courseId.toUpperCase().trim();
        for (CourseOffering co : offerings) {
            if (co.getCourse().getCourseId().toUpperCase().contains(searchId)) {
                results.add(co);
            }
        }
        return results;
    }

    public ArrayList<CourseOffering> searchByFaculty(String facultyId) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        if (facultyId == null || facultyId.trim().isEmpty()) {
            return results;
        }

        for (CourseOffering co : offerings) {
            if (co.getFaculty() != null
                    && co.getFaculty().getPerson().getPersonId().equalsIgnoreCase(facultyId)) {
                results.add(co);
            }
        }
        return results;
    }

    public ArrayList<CourseOffering> searchBySemester(String semester) {
        ArrayList<CourseOffering> results = new ArrayList<>();
        if (semester == null || semester.trim().isEmpty()) {
            return results;
        }

        for (CourseOffering co : offerings) {
            if (co.getSemester().equalsIgnoreCase(semester)) {
                results.add(co);
            }
        }
        return results;
    }

    public boolean removeOffering(CourseOffering co) {
        return offerings.remove(co);
    }

    public int size() {
        return offerings.size();
    }
}
