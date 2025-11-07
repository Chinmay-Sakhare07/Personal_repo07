/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Profiles;

import Business.Person.Person;

import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class StudentDirectory {

    ArrayList<StudentProfile> studentlist;

    public StudentDirectory() {
        studentlist = new ArrayList();
    }

    public StudentProfile newStudentProfile(Person p) {
        StudentProfile sp = new StudentProfile(p);
        studentlist.add(sp);
        return sp;
    }

    public StudentProfile findStudent(String id) {
        for (StudentProfile sp : studentlist) {
            if (sp.isMatch(id)) {
                return sp;
            }
        }
        return null;
    }

    public ArrayList<StudentProfile> searchByName(String name) {
        ArrayList<StudentProfile> results = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return results;
        }

        String searchTerm = name.toLowerCase().trim();
        for (StudentProfile sp : studentlist) {
            String fullName = sp.getPerson().getFullName().toLowerCase();
            if (fullName.contains(searchTerm)) {
                results.add(sp);
            }
        }
        return results;
    }

    public ArrayList<StudentProfile> searchByDepartment(String department) {
        ArrayList<StudentProfile> results = new ArrayList<>();
        if (department == null || department.trim().isEmpty()) {
            return results;
        }

        for (StudentProfile sp : studentlist) {
            String studentDept = sp.getPerson().getDepartment();
            if (studentDept != null && studentDept.equalsIgnoreCase(department)) {
                results.add(sp);
            }
        }
        return results;
    }

    public ArrayList<StudentProfile> searchById(String id) {
        ArrayList<StudentProfile> results = new ArrayList<>();
        if (id == null || id.trim().isEmpty()) {
            return results;
        }

        String searchId = id.toUpperCase().trim();
        for (StudentProfile sp : studentlist) {
            if (sp.getPerson().getPersonId().toUpperCase().contains(searchId)) {
                results.add(sp);
            }
        }
        return results;
    }

    public ArrayList<StudentProfile> getStudentList() {
        return studentlist;
    }

    public boolean removeStudent(String id) {
        StudentProfile sp = findStudent(id);
        if (sp != null) {
            return studentlist.remove(sp);
        }
        return false;
    }

    public int size() {
        return studentlist.size();
    }
}
