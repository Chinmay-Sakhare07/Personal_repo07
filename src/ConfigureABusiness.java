/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */

import Business.Business;
import Business.Course.Course;
import Business.Course.CourseOffering;
import Business.Course.Enrollment;
import Business.Department.Department;
import Business.Person.Person;
import Business.Profiles.EmployeeProfile;
import Business.Profiles.FacultyProfile;
import Business.Profiles.StudentProfile;

/**
 *
 * @author kal bugrara
 */
class ConfigureABusiness {

    static Business initialize() {
        Business business = new Business("Northeastern University");
        
        Department coe = business.getDepartmentDirectory().newDepartment("COE", "College of Engineering");

        Person adminPerson = business.getPersonDirectory().newPerson(
                "NEU001", "Admin", "User", "admin@northeastern.edu", "617-123-4567"
        );
        adminPerson.setDepartment("Administration");

        EmployeeProfile adminProfile = business.getEmployeeDirectory().newEmployeeProfile(adminPerson);
        business.getUserAccountDirectory().newUserAccount(adminProfile, "admin", "admin");

        
        String[] facultyFirstNames = {"Anisha", "Sarah", "Michael", "Emily", "David",
            "Jennifer", "Robert", "Lisa", "James", "Mary"};
        String[] facultyLastNames = {"Gaikar", "Johnson", "Williams", "Brown", "Jones",
            "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};

        FacultyProfile[] facultyProfiles = new FacultyProfile[10];

        for (int i = 0; i < 10; i++) {
            String id = "FAC" + String.format("%03d", i + 1);
            Person facPerson = business.getPersonDirectory().newPerson(
                    id,
                    facultyFirstNames[i],
                    facultyLastNames[i],
                    facultyFirstNames[i].toLowerCase() + "." + facultyLastNames[i].toLowerCase() + "@northeastern.edu",
                    "617-373-" + String.format("%04d", 2000 + i)
            );
            facPerson.setDepartment("COE");

            facultyProfiles[i] = business.getFacultyDirectory().newFacultyProfile(facPerson);
            facultyProfiles[i].setDepartment("COE");

            // Create user accounts for faculty
            business.getUserAccountDirectory().newUserAccount(
                    facultyProfiles[i],
                    facultyFirstNames[i].toLowerCase(),
                    "password"
            );
        }

        
        Course[] courses = new Course[5];
        courses[0] = new Course("INFO5100", "Application Engineering and Development",
                "Learn Java and enterprise development", 4);
        courses[1] = new Course("INFO6205", "Program Structure and Algorithms",
                "Data structures and algorithms", 4);
        courses[2] = new Course("INFO6150", "Web Design and User Experience",
                "Modern web development", 4);
        courses[3] = new Course("INFO5001", "Data Science and Analytics",
                "Introduction to data science", 4);
        courses[4] = new Course("INFO6350", "Smartphones Based Web Development",
                "Mobile app development", 4);

        
        String semester = "Fall 2025";
        CourseOffering[] offerings = new CourseOffering[5];

        for (int i = 0; i < 5; i++) {
            offerings[i] = new CourseOffering(
                    courses[i],
                    semester,
                    facultyProfiles[i], // Assign different faculty to each course
                    30, // Capacity of 30 students
                    true // Enrollment open
            );
            business.getCourseDirectory().addOffering(offerings[i]);
            facultyProfiles[i].addCourseOffering(offerings[i]);
        }

        
        String[] studentFirstNames = {"Chinmay", "Bob", "Charlie", "Diana", "Ethan",
            "Fiona", "George", "Hannah", "Ian", "Julia"};
        String[] studentLastNames = {"Sakhare", "Baker", "Carter", "Davis", "Evans",
            "Foster", "Green", "Harris", "Irwin", "Jackson"};

        StudentProfile[] studentProfiles = new StudentProfile[10];

        for (int i = 0; i < 10; i++) {
            String id = "STU" + String.format("%03d", i + 1);
            Person stuPerson = business.getPersonDirectory().newPerson(
                    id,
                    studentFirstNames[i],
                    studentLastNames[i],
                    studentFirstNames[i].toLowerCase() + "." + studentLastNames[i].toLowerCase() + "@northeastern.edu",
                    "857-123-" + String.format("%04d", 1000 + i)
            );
            stuPerson.setDepartment("COE");

            studentProfiles[i] = business.getStudentDirectory().newStudentProfile(stuPerson);

            business.getUserAccountDirectory().newUserAccount(
                    studentProfiles[i],
                    studentFirstNames[i].toLowerCase(),
                    "password"
            );
        }

        for (int i = 0; i < 5; i++) {
            enrollStudentInCourse(studentProfiles[i], offerings[0]); // INFO5100
            enrollStudentInCourse(studentProfiles[i], offerings[1]); // INFO6205
        }

        enrollStudentInCourse(studentProfiles[5], offerings[2]); // INFO6150
        enrollStudentInCourse(studentProfiles[5], offerings[3]); // INFO5001

        enrollStudentInCourse(studentProfiles[6], offerings[0]); // INFO5100
        enrollStudentInCourse(studentProfiles[6], offerings[4]); // INFO6350

        enrollStudentInCourse(studentProfiles[7], offerings[1]); // INFO6205
        enrollStudentInCourse(studentProfiles[7], offerings[2]); // INFO6150

        enrollStudentInCourse(studentProfiles[8], offerings[3]); // INFO5001
        enrollStudentInCourse(studentProfiles[8], offerings[4]); // INFO6350

        enrollStudentInCourse(studentProfiles[9], offerings[0]); // INFO5100
        enrollStudentInCourse(studentProfiles[9], offerings[3]); // INFO5001

        if (offerings[0].getEnrollments().size() > 0) {
            offerings[0].getEnrollments().get(0).assignGrade("A");   // Student 0
        }
        if (offerings[0].getEnrollments().size() > 1) {
            offerings[0].getEnrollments().get(1).assignGrade("A-");  // Student 1
        }
        if (offerings[0].getEnrollments().size() > 2) {
            offerings[0].getEnrollments().get(2).assignGrade("B+");  // Student 2
        }

        // Give grades in INFO6205
        if (offerings[1].getEnrollments().size() > 0) {
            offerings[1].getEnrollments().get(0).assignGrade("B");   // Student 0
        }
        if (offerings[1].getEnrollments().size() > 1) {
            offerings[1].getEnrollments().get(1).assignGrade("B+");  // Student 1
        }

        studentProfiles[0].getAccount().makePayment(8000.0); // Paid full tuition for 2 courses
        studentProfiles[1].getAccount().makePayment(4000.0); // Partial payment
        studentProfiles[2].getAccount().makePayment(8000.0); // Paid full

        
        for (int i = 0; i < 9; i++) {
            business.getPersonDirectory().newPerson(
                    "PER" + String.format("%03d", i + 1),
                    "Person" + (i + 1),
                    "LastName" + (i + 1),
                    "person" + (i + 1) + "@northeastern.edu",
                    "617-555-" + String.format("%04d", 1000 + i)
            );
        }

        return business;
    }
    
    private static void enrollStudentInCourse(StudentProfile student, CourseOffering offering) {
        Enrollment enrollment = new Enrollment(student, offering);
        boolean success = offering.enrollStudent(enrollment);
        if (success) {
            student.addEnrollment(enrollment);
        }
    }
}
