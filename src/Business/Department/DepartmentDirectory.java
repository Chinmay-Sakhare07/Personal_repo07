/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Department;

import java.util.ArrayList;

/**
 *
 * @author chinm
 */
public class DepartmentDirectory {

    private ArrayList<Department> departments;

    public DepartmentDirectory() {
        this.departments = new ArrayList<>();
    }

    public Department newDepartment(String id, String name) {
        Department dept = new Department(id, name);
        departments.add(dept);
        return dept;
    }

    public Department findDepartment(String id) {
        for (Department dept : departments) {
            if (dept.isMatch(id)) {
                return dept;
            }
        }
        return null;
    }

    public ArrayList<Department> getAllDepartments() {
        return departments;
    }

    public boolean removeDepartment(String id) {
        Department dept = findDepartment(id);
        if (dept != null) {
            return departments.remove(dept);
        }
        return false;
    }

    public int size() {
        return departments.size();
    }
}
