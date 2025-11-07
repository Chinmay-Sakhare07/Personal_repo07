/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */

import Business.Business;
import Business.Course.Course;
import Business.Course.CourseOffering;
import Business.Course.Enrollment;
import Business.CourseWork.Assignment;
import Business.CourseWork.Submission;
import Business.Department.Department;
import Business.Person.Person;
import Business.Profiles.EmployeeProfile;
import Business.Profiles.FacultyProfile;
import Business.Profiles.StudentProfile;
import java.text.SimpleDateFormat;

/**
 *
 * @author kal bugrara
 */
class ConfigureABusiness {

    public static Business initialize() {
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

            business.getUserAccountDirectory().newUserAccount(
                    facultyProfiles[i],
                    facultyFirstNames[i].toLowerCase(),
                    "password"
            );
        }

        Course[] courses = new Course[10];
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
        courses[5] = new Course("INFO6250", "Web Development Tools and Methods",
                "Advanced web development", 4);
        courses[6] = new Course("INFO7500", "Cryptocurrency and Smart Contracts",
                "Blockchain technology", 4);
        courses[7] = new Course("INFO7390", "Advances in Data Science",
                "Advanced data analytics", 4);
        courses[8] = new Course("INFO6105", "Data Science Engineering",
                "Engineering practices for data science", 4);
        courses[9] = new Course("INFO7255", "Advanced Big Data Applications",
                "Big data technologies", 4);

        String[] studentFirstNames = {"Chinmay", "Shreya", "Agnel", "Pranav", "Rutuj",
            "Swapnil", "Atul", "Pragati", "Vartika", "Parth"};
        String[] studentLastNames = {"Sakhare", "Darban", "Salve", "Waghmare", "Bhise",
            "Bala", "Tiwary", "Narote", "Singh", "Sonawane"};

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
                    "****"
            );
        }

        System.out.println("===== CREATING COURSE OFFERINGS =====");

        CourseOffering[] fall2024Offerings = new CourseOffering[5];
        for (int i = 0; i < 5; i++) {
            fall2024Offerings[i] = new CourseOffering(
                    courses[i],
                    "Fall 2024",
                    facultyProfiles[i],
                    30,
                    false
            );
            business.getCourseDirectory().addOffering(fall2024Offerings[i]);
            facultyProfiles[i].addCourseOffering(fall2024Offerings[i]);
        }

        Enrollment chinmay_fall24_1 = new Enrollment(studentProfiles[0], fall2024Offerings[0]);
        fall2024Offerings[0].enrollStudent(chinmay_fall24_1);
        studentProfiles[0].addEnrollment(chinmay_fall24_1);

        Enrollment chinmay_fall24_2 = new Enrollment(studentProfiles[0], fall2024Offerings[1]);
        fall2024Offerings[1].enrollStudent(chinmay_fall24_2);
        studentProfiles[0].addEnrollment(chinmay_fall24_2);

        Enrollment shreya_fall24_1 = new Enrollment(studentProfiles[1], fall2024Offerings[0]);
        fall2024Offerings[0].enrollStudent(shreya_fall24_1);
        studentProfiles[1].addEnrollment(shreya_fall24_1);

        Enrollment shreya_fall24_2 = new Enrollment(studentProfiles[1], fall2024Offerings[1]);
        fall2024Offerings[1].enrollStudent(shreya_fall24_2);
        studentProfiles[1].addEnrollment(shreya_fall24_2);

        Enrollment agnel_fall24_1 = new Enrollment(studentProfiles[2], fall2024Offerings[2]);
        fall2024Offerings[2].enrollStudent(agnel_fall24_1);
        studentProfiles[2].addEnrollment(agnel_fall24_1);

        Enrollment agnel_fall24_2 = new Enrollment(studentProfiles[2], fall2024Offerings[3]);
        fall2024Offerings[3].enrollStudent(agnel_fall24_2);
        studentProfiles[2].addEnrollment(agnel_fall24_2);

        Enrollment pranav_fall24_1 = new Enrollment(studentProfiles[3], fall2024Offerings[2]);
        fall2024Offerings[2].enrollStudent(pranav_fall24_1);
        studentProfiles[3].addEnrollment(pranav_fall24_1);

        Enrollment pranav_fall24_2 = new Enrollment(studentProfiles[3], fall2024Offerings[3]);
        fall2024Offerings[3].enrollStudent(pranav_fall24_2);
        studentProfiles[3].addEnrollment(pranav_fall24_2);

        Enrollment rutuj_fall24_1 = new Enrollment(studentProfiles[4], fall2024Offerings[2]);
        fall2024Offerings[2].enrollStudent(rutuj_fall24_1);
        studentProfiles[4].addEnrollment(rutuj_fall24_1);

        Enrollment rutuj_fall24_2 = new Enrollment(studentProfiles[4], fall2024Offerings[3]);
        fall2024Offerings[3].enrollStudent(rutuj_fall24_2);
        studentProfiles[4].addEnrollment(rutuj_fall24_2);

        chinmay_fall24_1.assignGrade("A");
        chinmay_fall24_2.assignGrade("B+");
        shreya_fall24_1.assignGrade("A-");
        shreya_fall24_2.assignGrade("B");
        agnel_fall24_1.assignGrade("B+");
        agnel_fall24_2.assignGrade("B");
        pranav_fall24_1.assignGrade("B");
        pranav_fall24_2.assignGrade("B-");
        rutuj_fall24_1.assignGrade("A-");
        rutuj_fall24_2.assignGrade("B+");

        CourseOffering[] spring2025Offerings = new CourseOffering[5];
        for (int i = 0; i < 5; i++) {
            spring2025Offerings[i] = new CourseOffering(
                    courses[i],
                    "Spring 2025",
                    facultyProfiles[i],
                    30,
                    false
            );
            business.getCourseDirectory().addOffering(spring2025Offerings[i]);
            facultyProfiles[i].addCourseOffering(spring2025Offerings[i]);
        }

        Enrollment chinmay_spring25_1 = new Enrollment(studentProfiles[0], spring2025Offerings[2]);
        spring2025Offerings[2].enrollStudent(chinmay_spring25_1);
        studentProfiles[0].addEnrollment(chinmay_spring25_1);

        Enrollment chinmay_spring25_2 = new Enrollment(studentProfiles[0], spring2025Offerings[3]);
        spring2025Offerings[3].enrollStudent(chinmay_spring25_2);
        studentProfiles[0].addEnrollment(chinmay_spring25_2);

        Enrollment shreya_spring25_1 = new Enrollment(studentProfiles[1], spring2025Offerings[2]);
        spring2025Offerings[2].enrollStudent(shreya_spring25_1);
        studentProfiles[1].addEnrollment(shreya_spring25_1);

        Enrollment shreya_spring25_2 = new Enrollment(studentProfiles[1], spring2025Offerings[3]);
        spring2025Offerings[3].enrollStudent(shreya_spring25_2);
        studentProfiles[1].addEnrollment(shreya_spring25_2);

        Enrollment agnel_spring25_1 = new Enrollment(studentProfiles[2], spring2025Offerings[0]);
        spring2025Offerings[0].enrollStudent(agnel_spring25_1);
        studentProfiles[2].addEnrollment(agnel_spring25_1);

        Enrollment agnel_spring25_2 = new Enrollment(studentProfiles[2], spring2025Offerings[1]);
        spring2025Offerings[1].enrollStudent(agnel_spring25_2);
        studentProfiles[2].addEnrollment(agnel_spring25_2);

        Enrollment pranav_spring25_1 = new Enrollment(studentProfiles[3], spring2025Offerings[0]);
        spring2025Offerings[0].enrollStudent(pranav_spring25_1);
        studentProfiles[3].addEnrollment(pranav_spring25_1);

        Enrollment pranav_spring25_2 = new Enrollment(studentProfiles[3], spring2025Offerings[1]);
        spring2025Offerings[1].enrollStudent(pranav_spring25_2);
        studentProfiles[3].addEnrollment(pranav_spring25_2);

        Enrollment rutuj_spring25_1 = new Enrollment(studentProfiles[4], spring2025Offerings[0]);
        spring2025Offerings[0].enrollStudent(rutuj_spring25_1);
        studentProfiles[4].addEnrollment(rutuj_spring25_1);

        Enrollment rutuj_spring25_2 = new Enrollment(studentProfiles[4], spring2025Offerings[1]);
        spring2025Offerings[1].enrollStudent(rutuj_spring25_2);
        studentProfiles[4].addEnrollment(rutuj_spring25_2);

        chinmay_spring25_1.assignGrade("A");
        chinmay_spring25_2.assignGrade("A-");
        shreya_spring25_1.assignGrade("B+");
        shreya_spring25_2.assignGrade("B");
        agnel_spring25_1.assignGrade("A");
        agnel_spring25_2.assignGrade("A-");
        pranav_spring25_1.assignGrade("B-");
        pranav_spring25_2.assignGrade("C+");
        rutuj_spring25_1.assignGrade("A");
        rutuj_spring25_2.assignGrade("A-");

        System.out.println("Creating Fall 2025 offerings...");

        CourseOffering[] fall2025Offerings = new CourseOffering[10];
        for (int i = 0; i < 10; i++) {
            fall2025Offerings[i] = new CourseOffering(
                    courses[i],
                    "Fall 2025",
                    facultyProfiles[i % 10],
                    30,
                    true
            );
            business.getCourseDirectory().addOffering(fall2025Offerings[i]);
            facultyProfiles[i % 10].addCourseOffering(fall2025Offerings[i]);
            System.out.println("Created: " + courses[i].getCourseId() + " for Fall 2025");
        }

        System.out.println("\n===== ENROLLING STUDENTS IN FALL 2025 =====");

        System.out.println("Enrolling Chinmay (STU001)...");
        enrollStudentInCourseDebug(studentProfiles[0], fall2025Offerings[4], business);

        System.out.println("Enrolling Shreya (STU002)...");
        enrollStudentInCourseDebug(studentProfiles[1], fall2025Offerings[4], business);

        System.out.println("Enrolling Agnel (STU003)...");
        enrollStudentInCourseDebug(studentProfiles[2], fall2025Offerings[4], business);

        System.out.println("Enrolling Pranav (STU004)...");
        enrollStudentInCourseDebug(studentProfiles[3], fall2025Offerings[4], business);

        System.out.println("Enrolling Rutuj (STU005)...");
        enrollStudentInCourseDebug(studentProfiles[4], fall2025Offerings[2], business);

        System.out.println("Enrolling Swapnil (STU006)...");
        enrollStudentInCourseDebug(studentProfiles[5], fall2025Offerings[0], business);

        System.out.println("Enrolling Atul (STU007)...");
        enrollStudentInCourseDebug(studentProfiles[6], fall2025Offerings[2], business);

        System.out.println("Enrolling Pragati (STU008)...");
        enrollStudentInCourseDebug(studentProfiles[7], fall2025Offerings[0], business);

        System.out.println("Enrolling Vartika (STU009)...");
        enrollStudentInCourseDebug(studentProfiles[8], fall2025Offerings[2], business);

        System.out.println("Enrolling Parth (STU010)...");
        enrollStudentInCourseDebug(studentProfiles[9], fall2025Offerings[4], business);

        System.out.println("===========================================\n");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            Assignment a1 = new Assignment(
                    "INFO5100_A1",
                    "Homework 1 - Java Basics",
                    "Complete exercises on Java fundamentals",
                    sdf.parse("10/25/2025"),
                    100,
                    fall2025Offerings[0]
            );
            fall2025Offerings[0].addAssignment(a1);

            Assignment a2 = new Assignment(
                    "INFO5100_A2",
                    "Project 1 - Swing Application",
                    "Build a Java Swing application",
                    sdf.parse("11/15/2025"),
                    150,
                    fall2025Offerings[0]
            );
            fall2025Offerings[0].addAssignment(a2);

            Assignment a3 = new Assignment(
                    "INFO5100_A3",
                    "Quiz 1 - OOP Concepts",
                    "Online quiz covering chapters 1-5",
                    sdf.parse("12/05/2025"),
                    50,
                    fall2025Offerings[0]
            );
            fall2025Offerings[0].addAssignment(a3);

            Assignment a4 = new Assignment(
                    "INFO6205_A1",
                    "Homework 1 - Sorting Algorithms",
                    "Implement various sorting algorithms",
                    sdf.parse("10/18/2025"),
                    100,
                    fall2025Offerings[1]
            );
            fall2025Offerings[1].addAssignment(a4);

            Assignment a5 = new Assignment(
                    "INFO6205_A2",
                    "Project 1 - Data Structures",
                    "Implement custom data structures",
                    sdf.parse("11/08/2025"),
                    150,
                    fall2025Offerings[1]
            );
            fall2025Offerings[1].addAssignment(a5);

            Assignment a6 = new Assignment(
                    "INFO6150_A1",
                    "Homework 1 - HTML/CSS",
                    "Create a responsive website",
                    sdf.parse("10/20/2025"),
                    100,
                    fall2025Offerings[2]
            );
            fall2025Offerings[2].addAssignment(a6);

            Assignment a7 = new Assignment(
                    "INFO6150_A2",
                    "UX Design Project",
                    "Design user experience for mobile app",
                    sdf.parse("11/10/2025"),
                    120,
                    fall2025Offerings[2]
            );
            fall2025Offerings[2].addAssignment(a7);

            Assignment a8 = new Assignment(
                    "INFO5001_A1",
                    "Data Analysis Project",
                    "Analyze dataset and present findings",
                    sdf.parse("10/22/2025"),
                    100,
                    fall2025Offerings[3]
            );
            fall2025Offerings[3].addAssignment(a8);

            Assignment a9 = new Assignment(
                    "INFO5001_A2",
                    "Machine Learning Lab",
                    "Build predictive models",
                    sdf.parse("11/12/2025"),
                    130,
                    fall2025Offerings[3]
            );
            fall2025Offerings[3].addAssignment(a9);

            Assignment a10 = new Assignment(
                    "INFO6350_A1",
                    "Mobile App Prototype",
                    "Create a mobile application prototype",
                    sdf.parse("10/26/2025"),
                    100,
                    fall2025Offerings[4]
            );
            fall2025Offerings[4].addAssignment(a10);

            Assignment a11 = new Assignment(
                    "INFO6350_A2",
                    "Mobile Development Final Project",
                    "Complete mobile application",
                    sdf.parse("11/20/2025"),
                    150,
                    fall2025Offerings[4]
            );
            fall2025Offerings[4].addAssignment(a11);

            Assignment a12 = new Assignment(
                    "INFO6250_A1",
                    "Web Tools Assignment",
                    "Explore modern web tools",
                    sdf.parse("10/24/2025"),
                    100,
                    fall2025Offerings[5]
            );
            fall2025Offerings[5].addAssignment(a12);

            Assignment a13 = new Assignment(
                    "INFO7500_A1",
                    "Blockchain Basics",
                    "Introduction to blockchain",
                    sdf.parse("10/27/2025"),
                    100,
                    fall2025Offerings[6]
            );
            fall2025Offerings[6].addAssignment(a13);

            Assignment a14 = new Assignment(
                    "INFO7390_A1",
                    "Advanced Analytics",
                    "Complex data analysis",
                    sdf.parse("10/29/2025"),
                    100,
                    fall2025Offerings[7]
            );
            fall2025Offerings[7].addAssignment(a14);

            Assignment a15 = new Assignment(
                    "INFO6105_A1",
                    "Data Engineering Project",
                    "Build data pipeline",
                    sdf.parse("10/30/2025"),
                    100,
                    fall2025Offerings[8]
            );
            fall2025Offerings[8].addAssignment(a15);

            Assignment a16 = new Assignment(
                    "INFO7255_A1",
                    "Big Data Analysis",
                    "Work with large datasets",
                    sdf.parse("11/01/2025"),
                    100,
                    fall2025Offerings[9]
            );
            fall2025Offerings[9].addAssignment(a16);

            Submission sub1 = new Submission(studentProfiles[5], a1);
            sub1.setSubmissionDate(sdf.parse("10/24/2025"));
            a1.addSubmission(sub1);

            Submission sub2 = new Submission(studentProfiles[7], a1);
            sub2.setSubmissionDate(sdf.parse("10/23/2025"));
            sub2.setPointsEarned(85);
            sub2.setFeedback("Good work! Some minor issues with error handling.");
            sub2.setStatus("Graded");
            a1.addSubmission(sub2);

            System.out.println("✅ All Fall 2025 assignments created");

        } catch (Exception e) {
            System.err.println("❌ Error creating assignments: " + e.getMessage());
            e.printStackTrace();
        }

        studentProfiles[0].getAccount().makePayment(20000.0);
        studentProfiles[1].getAccount().makePayment(16000.0);
        studentProfiles[2].getAccount().makePayment(20000.0);
        studentProfiles[3].getAccount().makePayment(16000.0);
        studentProfiles[4].getAccount().makePayment(20000.0);


        System.out.println("\n========== BUSINESS CONFIGURED ==========");
        System.out.println("Total Persons: " + business.getPersonDirectory().getPersonList().size());
        System.out.println("Total Students: " + business.getStudentDirectory().size());
        System.out.println("Total Faculty: " + business.getFacultyDirectory().size());
        System.out.println("Total Course Offerings: " + business.getCourseDirectory().size());
        System.out.println("\nStudent Enrollment Verification:");
        for (int i = 0; i < 10; i++) {
            System.out.println(studentProfiles[i].getPerson().getFullName() + " (STU"
                    + String.format("%03d", i + 1) + "): "
                    + studentProfiles[i].getEnrollments().size() + " total enrollments");
        }
        System.out.println("=========================================\n");
        return business;
    }

    private static void enrollStudentInCourseDebug(StudentProfile student, CourseOffering offering, Business business) {
        System.out.println("  Attempting: " + student.getPerson().getFullName()
                + " -> " + offering.getCourse().getCourseId());

        Enrollment enrollment = new Enrollment(student, offering);
        boolean success = offering.enrollStudent(enrollment);

        if (success) {
            student.addEnrollment(enrollment);
            System.out.println("  SUCCESS - Student now has " + student.getEnrollments().size() + " enrollments");
        } else {
            System.out.println("  FAILED - Enrollment rejected");
        }
    }

    private static void enrollStudentInCourse(StudentProfile student, CourseOffering offering) {
        Enrollment enrollment = new Enrollment(student, offering);
        boolean success = offering.enrollStudent(enrollment);
        if (success) {
            student.addEnrollment(enrollment);
        }
    }
}
