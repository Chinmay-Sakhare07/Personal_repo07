/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business;

import Business.Course.CourseOffering;
import Business.Department.DepartmentDirectory;
import Business.Finance.Payment;
import Business.Person.Person;
import Business.Person.PersonDirectory;
import Business.Profiles.CourseDirectory;
import Business.Profiles.EmployeeDirectory;
import Business.Profiles.FacultyDirectory;
import Business.Profiles.StudentDirectory;
import Business.Profiles.StudentProfile;
import Business.UserAccounts.UserAccount;

import Business.UserAccounts.UserAccountDirectory;
import java.util.Random;

/**
 *
 * @author kal bugrara
 */
public class Business {

    String name;
    PersonDirectory persondirectory;
    EmployeeDirectory employeedirectory;
    UserAccountDirectory useraccountdirectory;
    StudentDirectory studentdirectory;
    FacultyDirectory facultyDirectory;
    CourseDirectory courseDirectory;
    DepartmentDirectory departmentDirectory;

    public Business(String n) {
        name = n;
        persondirectory = new PersonDirectory();
        employeedirectory = new EmployeeDirectory(this);
        useraccountdirectory = new UserAccountDirectory();
        studentdirectory = new StudentDirectory();
        facultyDirectory = new FacultyDirectory();
        courseDirectory = new CourseDirectory();
        departmentDirectory = new DepartmentDirectory();
    }

    public PersonDirectory getPersonDirectory() {
        return persondirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonDirectory getPersondirectory() {
        return persondirectory;
    }

    public void setPersondirectory(PersonDirectory persondirectory) {
        this.persondirectory = persondirectory;
    }

    public EmployeeDirectory getEmployeedirectory() {
        return employeedirectory;
    }

    public void setEmployeedirectory(EmployeeDirectory employeedirectory) {
        this.employeedirectory = employeedirectory;
    }

    public UserAccountDirectory getUseraccountdirectory() {
        return useraccountdirectory;
    }

    public void setUseraccountdirectory(UserAccountDirectory useraccountdirectory) {
        this.useraccountdirectory = useraccountdirectory;
    }

    public StudentDirectory getStudentdirectory() {
        return studentdirectory;
    }

    public void setStudentdirectory(StudentDirectory studentdirectory) {
        this.studentdirectory = studentdirectory;
    }

    public FacultyDirectory getFacultyDirectory() {
        return facultyDirectory;
    }

    public void setFacultyDirectory(FacultyDirectory facultyDirectory) {
        this.facultyDirectory = facultyDirectory;
    }

    public UserAccountDirectory getUserAccountDirectory() {
        return useraccountdirectory;
    }

    public EmployeeDirectory getEmployeeDirectory() {
        return employeedirectory;
    }

    public CourseDirectory getCourseDirectory() {
        return courseDirectory;
    }

    public StudentDirectory getStudentDirectory() {
        return studentdirectory;
    }

    public DepartmentDirectory getDepartmentDirectory() {
        return departmentDirectory;
    }

    public void setDepartmentDirectory(DepartmentDirectory departmentDirectory) {
        this.departmentDirectory = departmentDirectory;
    }

    public String generateUniquePersonId() {
        Random random = new Random();
        String id;
        do {
            int randomNum = 100000 + random.nextInt(900000);
            id = "NEU" + randomNum;
        } while (persondirectory.findPerson(id) != null);

        return id;
    }

    public boolean checkDuplicateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        for (Person p : persondirectory.getPersonList()) {
            if (email.equalsIgnoreCase(p.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public int getTotalUsersByRole(String role) {
        int count = 0;
        for (UserAccount ua : useraccountdirectory.getUserAccountList()) {
            if (ua.getRole().equalsIgnoreCase(role)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCoursesPerSemester(String semester) {
        int count = 0;
        for (CourseOffering offering : courseDirectory.getOfferings()) {
            if (offering.getSemester().equalsIgnoreCase(semester)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalEnrolledStudents() {
        int count = 0;
        for (CourseOffering offering : courseDirectory.getOfferings()) {
            count += offering.getEnrollments().size();
        }
        return count;
    }

    public double calculateTotalTuitionRevenue() {
        double total = 0.0;
        for (StudentProfile student : studentdirectory.getStudentList()) {
            for (Payment payment : student.getAccount().getPaymentHistory()) {
                if (payment.getType().equalsIgnoreCase("Payment")
                        && payment.getStatus().equalsIgnoreCase("Paid")) {
                    total += payment.getAmount();
                }
            }
        }
        return total;
    }

    public double calculateOutstandingBalance() {
        double total = 0.0;
        for (StudentProfile student : studentdirectory.getStudentList()) {
            total += student.getAccount().getOutstandingBalance();
        }
        return total;
    }
}
