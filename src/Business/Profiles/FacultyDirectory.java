/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Profiles;

import Business.Person.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chinm
 */
public class FacultyDirectory {

    private final ArrayList<FacultyProfile> facultyList;

    public FacultyDirectory() {
        this.facultyList = new ArrayList<>();
    }

    public FacultyProfile newFacultyProfile(Person p) {
        FacultyProfile fp = new FacultyProfile(p);
        facultyList.add(fp);
        return fp;
    }

    public FacultyProfile findFacultyByPersonId(String id) {
        for (FacultyProfile fp : facultyList) {
            if (fp.isMatch(id)) {
                return fp;
            }
        }
        return null;
    }

    public ArrayList<FacultyProfile> searchByName(String name) {
        ArrayList<FacultyProfile> results = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return results;
        }

        String searchTerm = name.toLowerCase().trim();
        for (FacultyProfile fp : facultyList) {
            String fullName = fp.getPerson().getFullName().toLowerCase();
            if (fullName.contains(searchTerm)) {
                results.add(fp);
            }
        }
        return results;
    }

    public ArrayList<FacultyProfile> searchByDepartment(String department) {
        ArrayList<FacultyProfile> results = new ArrayList<>();
        if (department == null || department.trim().isEmpty()) {
            return results;
        }

        for (FacultyProfile fp : facultyList) {
            if (department.equalsIgnoreCase(fp.getDepartment())) {
                results.add(fp);
            }
        }
        return results;
    }

    public ArrayList<FacultyProfile> searchById(String id) {
        ArrayList<FacultyProfile> results = new ArrayList<>();
        if (id == null || id.trim().isEmpty()) {
            return results;
        }

        String searchId = id.toUpperCase().trim();
        for (FacultyProfile fp : facultyList) {
            if (fp.getPerson().getPersonId().toUpperCase().contains(searchId)) {
                results.add(fp);
            }
        }
        return results;
    }

    public List<FacultyProfile> getAllFaculty() {
        return new ArrayList<>(facultyList);
    }

    public boolean removeFaculty(FacultyProfile fp) {
        return facultyList.remove(fp);
    }

    public int size() {
        return facultyList.size();
    }
}
