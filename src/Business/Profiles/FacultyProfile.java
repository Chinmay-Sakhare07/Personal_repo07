/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author chinm
 */
package Business.Profiles;

import Business.Person.Person;
import Business.Course.CourseOffering;
import java.util.ArrayList;

public class FacultyProfile extends Profile {

    private ArrayList<CourseOffering> offerings;
    private String department;

    public FacultyProfile(Person p) {
        super(p);
        this.offerings = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Faculty";
    }


    public void addCourseOffering(CourseOffering co) {
        offerings.add(co);
    }

    public ArrayList<CourseOffering> getOfferings() {
        return offerings;
    }

    public void removeCourseOffering(CourseOffering co) {
        offerings.remove(co);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean isMatch(String id) {
        return getPerson().getPersonId().equals(id);
    }

    @Override
    public String toString() {
        return getPerson().toString()
                + (department != null ? " (" + department + ")" : "");
    }
}
