/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.FacultyRole;

import Business.Business;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Dialog; 
import java.util.Locale;



/**
 *
 * @author anishagaikar
 */
public class FacultyConsole extends javax.swing.JPanel {

private final Business business;
private final JPanel cardPanel;
private DefaultTableModel courseModel;
private TableRowSorter<TableModel> sorter;
private DefaultTableModel studentModel;
private DefaultTableModel reportModel;
private DefaultTableModel tuitionModel;
private final DataProvider data;

static class FacultyProfileDTO {
    String facultyId, name, email, phone, department, officeHours;
    FacultyProfileDTO(String id, String name, String email, String phone, String dept, String hrs) {
        this.facultyId=id; this.name=name; this.email=email; this.phone=phone; this.department=dept; this.officeHours=hrs;
    }
}
static class CourseDTO {
    String id, title, description, room, time, semester, syllabusPath, facultyId;
    int capacity;
    boolean enrollmentOpen;
    CourseDTO(String id, String title, String description, int capacity, String room, String time, String semester, String facultyId) {
        this.id=id; this.title=title; this.description=description; this.capacity=capacity; this.room=room; this.time=time;
        this.semester=semester; this.facultyId=facultyId; this.enrollmentOpen=false;
    }
}
static class StudentDTO { String id, name, email; StudentDTO(String i,String n,String e){id=i;name=n;email=e;} }
static class EnrollmentDTO { String courseId, studentId; double billed, paid; EnrollmentDTO(String c,String s,double b,double p){courseId=c;studentId=s;billed=b;paid=p;} }
static class GradeItemDTO { String courseId, studentId, item; double pointsEarned, pointsMax; GradeItemDTO(String c,String s,String it,double e,double m){courseId=c;studentId=s;item=it;pointsEarned=e;pointsMax=m;} }

// ---- grade helpers ----
private static String toLetter(double pct){
    if(pct>=93) return "A"; if(pct>=90) return "A-"; if(pct>=87) return "B+"; if(pct>=83) return "B";
    if(pct>=80) return "B-"; if(pct>=77) return "C+"; if(pct>=73) return "C"; if(pct>=70) return "C-";
    if(pct>=60) return "D"; return "F";
}
private static double toGpa(String letter){
    switch (letter){
        case "A": return 4.0; case "A-": return 3.7; case "B+": return 3.3; case "B": return 3.0; case "B-": return 2.7;
        case "C+": return 2.3; case "C": return 2.0; case "C-": return 1.7; case "D": return 1.0; default: return 0.0;
    }
}

// ---- provider SPI ----
interface DataProvider {
    FacultyProfileDTO getMe();
    List<String> getSemesters();
    List<CourseDTO> getMyCourses();
    List<StudentDTO> getStudents();
    List<EnrollmentDTO> getEnrollments();
    List<GradeItemDTO> getGrades();

    void updateCourse(CourseDTO c);
    void setEnrollment(String courseId, boolean open);
    void saveSyllabus(String courseId, String path);
    void addGrade(GradeItemDTO g);
    
    List<FacultyProfileDTO> getFacultyDirectory();
void upsertFaculty(FacultyProfileDTO f);
void deleteFaculty(String facultyId);

// transcript across a course for a student (derived from grades/enrollments)
default List<GradeItemDTO> getTranscriptItems(String courseId, String studentId) {
    return getGrades().stream()
            .filter(g -> g.courseId.equals(courseId) && g.studentId.equals(studentId))
            .collect(Collectors.toList());
}

    default List<EnrollmentDTO> enrollmentsForCourse(String cid){
        return getEnrollments().stream().filter(e->e.courseId.equals(cid)).collect(Collectors.toList());
    }
    default StudentDTO findStudent(String sid){
        return getStudents().stream().filter(s->s.id.equals(sid)).findFirst().orElse(null);
    }
    default CourseDTO findCourse(String cid){
        return getMyCourses().stream().filter(c->c.id.equals(cid)).findFirst().orElse(null);
    }
}

// ---- reflection-backed Business provider (stubbed; safe defaults) ----
static class BusinessDataProvider implements DataProvider {
    private final Business business;
    private FacultyProfileDTO me;
    private List<String> semesters = new ArrayList<>();
    private List<CourseDTO> courses = new ArrayList<>();
    private List<StudentDTO> students = new ArrayList<>();
    private List<EnrollmentDTO> enrollments = new ArrayList<>();
    private List<GradeItemDTO> grades = new ArrayList<>();

    BusinessDataProvider(Business b){
        this.business=b;
        tryLoad();
    }
    
    private final List<FacultyProfileDTO> facultyDir = new ArrayList<>();

@Override public List<FacultyProfileDTO> getFacultyDirectory() { return facultyDir; }
@Override public void upsertFaculty(FacultyProfileDTO f) {
    int i = -1;
    for (int k=0;k<facultyDir.size();k++) if (facultyDir.get(k).facultyId.equals(f.facultyId)) { i=k; break; }
    if (i>=0) facultyDir.set(i, f); else facultyDir.add(f);
}
@Override public void deleteFaculty(String facultyId) {
    facultyDir.removeIf(x -> x.facultyId.equals(facultyId));
}



    @Override public FacultyProfileDTO getMe(){ return me; }
    @Override public List<String> getSemesters(){ return semesters; }
    @Override public List<CourseDTO> getMyCourses(){ return courses; }
    @Override public List<StudentDTO> getStudents(){ return students; }
    @Override public List<EnrollmentDTO> getEnrollments(){ return enrollments; }
    @Override public List<GradeItemDTO> getGrades(){ return grades; }

    @Override public void updateCourse(CourseDTO c){ /* TODO: map back to your Course and persist */ }
    @Override public void setEnrollment(String courseId, boolean open){ CourseDTO cc=findCourse(courseId); if(cc!=null) cc.enrollmentOpen=open; /* TODO persist */ }
    @Override public void saveSyllabus(String courseId, String path){ CourseDTO cc=findCourse(courseId); if(cc!=null) cc.syllabusPath=path; /* TODO persist */ }
    @Override public void addGrade(GradeItemDTO g){ grades.add(g); /* TODO persist */ }

private void tryLoad() {
    if (business == null) { seedFallback(); return; }

    try {
        // ---------- reset ----------
        facultyDir.clear();
        semesters = new ArrayList<>();
        courses   = new ArrayList<>();
        students  = new ArrayList<>();
        enrollments = new ArrayList<>();
        grades    = new ArrayList<>();

        // ---------- Faculty (me + directory) ----------
        Object facDir = business.getClass().getMethod("getFacultyDirectory").invoke(business);
        List<Object> facProfiles = safeList(invoke(facDir,
                "getFacultyProfiles","getFacultyProfileList","getFacultyList","getProfiles"));

        for (Object fp : facProfiles) {
            Object person = invoke(fp, "getPerson");
            String id    = str(invoke(person, "getPersonId","getId"));
            String name  = (str(invoke(person,"getFirstName")) + " " + str(invoke(person,"getLastName"))).trim();
            String email = str(invoke(person,"getEmail","getEmailId","getEmailAddress"));
            String phone = str(invoke(person,"getPhone","getPhoneNumber"));
            String dept  = str(invoke(fp, "getDepartment"));
            String hrs   = ""; // not in model
            facultyDir.add(new FacultyProfileDTO(id, name, email, phone, dept, hrs));
        }
        // pick "me" (first faculty, matches how you create them in ConfigureABusiness)
        me = facultyDir.isEmpty()
                ? new FacultyProfileDTO("FAC000","Faculty","faculty@northeastern.edu","","COE","")
                : facultyDir.get(0);

        // ---------- Students ----------
        Object stuDir = business.getClass().getMethod("getStudentDirectory").invoke(business);
        List<Object> stuProfiles = safeList(invoke(stuDir,
                "getStudentProfiles","getStudentProfileList","getStudentList","getProfiles"));
        for (Object sp : stuProfiles) {
            Object person = invoke(sp, "getPerson");
            String id    = str(invoke(person, "getPersonId","getId"));
            String name  = (str(invoke(person,"getFirstName")) + " " + str(invoke(person,"getLastName"))).trim();
            String email = str(invoke(person,"getEmail","getEmailId","getEmailAddress"));
            students.add(new StudentDTO(id, name, email));
        }

        // ---------- Course offerings (filter to "my courses") ----------
        Object courseDir = business.getClass().getMethod("getCourseDirectory").invoke(business);
        List<Object> offerings = safeList(invoke(courseDir,
                "getCourseOfferings","getOfferings","getCourseOfferingList","getOfferingsList","getAllOfferings"));

        for (Object off : offerings) {
            Object c   = invoke(off, "getCourse");
            String cid = str(invoke(c, "getCOurseNumber","getCourseNumber","getCourseId","getId"));
            String ttl = str(invoke(c, "getName","getCourseName","getTitle"));
            String desc= str(invoke(c, "getDescription","getCourseDescription"));
            Integer cap= (Integer) orDefault(invoke(off, "getCapacity"), 30);
            String sem = str(invoke(off, "getSemester"));

            Object facProf = invoke(off, "getFacultyProfile","getFaculty");
            String fid = "";
            if (facProf != null) {
                Object p = invoke(facProf, "getPerson");
                fid = p != null ? str(invoke(p, "getPersonId","getId")) : "";
            }

            boolean open = Boolean.TRUE.equals(invoke(off, "isEnrollmentOpen","getEnrollmentOpen","isOpen"));

            // gather semesters
            if (sem != null && !sem.isEmpty() && !semesters.contains(sem)) semesters.add(sem);

            // only add to "my courses" if I'm the assigned faculty
            if (me != null && me.facultyId.equals(fid)) {
                CourseDTO dto = new CourseDTO(cid, ttl, desc, cap, "", "", sem, fid);
                dto.enrollmentOpen = open;
                courses.add(dto);
            }

            // ---------- Enrollments & letter grades ----------
            List<Object> ens = safeList(invoke(off, "getEnrollments","getSeatlist","getSeatList"));
            for (Object en : ens) {
                Object sp = invoke(en, "getStudentProfile","getStudent");
                String sid = "";
                if (sp != null) {
                    Object p = invoke(sp, "getPerson");
                    sid = p != null ? str(invoke(p, "getPersonId","getId"))
                                    : str(invoke(sp, "getStudentId","getId"));
                }
                if (!sid.isEmpty() && cid != null && !cid.isEmpty()) {
                    enrollments.add(new EnrollmentDTO(cid, sid, 0.0, 0.0));
                }

                String letter = str(invoke(en, "getLetterGrade","getGradeLetter","getGrade"));
                if (letter != null && !letter.isEmpty() && !sid.isEmpty()) {
                    double pct = letterToPercent(letter);
                    grades.add(new GradeItemDTO(cid, sid, "Final", pct, 100.0));
                }
            }
        }

        if (semesters.isEmpty()) semesters = Arrays.asList("Fall 2025");

        System.out.println("[FacultyConsole] Loaded from Business: " +
                "faculty=" + facultyDir.size() +
                ", students=" + students.size() +
                ", courses(my)=" + courses.size() +
                ", enrollments=" + enrollments.size() +
                ", grades=" + grades.size());
    } catch (Throwable t) {
        t.printStackTrace();
        System.err.println("[FacultyConsole] Reflection failed; using fallback.");
        seedFallback();
    }
}



    private void seedFallback() {
    // your original hard-coded sample data here (unchanged)
    // ... (keep what you already had)
  
    // reset everything
    facultyDir.clear();
    semesters = new ArrayList<>();
    courses   = new ArrayList<>();
    students  = new ArrayList<>();
    enrollments = new ArrayList<>();
    grades    = new ArrayList<>();

    // ---- faculty (current user + directory) ----
    me = new FacultyProfileDTO(
            "FAC001",
            "Dr. Ada Lovelace",
            "ada@neu.edu",
            "617-373-2001",
            "COE",
            "Mon 2–4 PM, ISEC-410"
    );
    facultyDir.add(me);
    facultyDir.add(new FacultyProfileDTO("FAC002","Sarah Johnson","sarah.j@northeastern.edu","617-373-2002","COE","Tue 11–1"));
    facultyDir.add(new FacultyProfileDTO("FAC003","Michael Williams","mw@northeastern.edu","617-373-2003","COE","Wed 3–5"));

    // ---- semesters ----
    semesters = Arrays.asList("Fall 2025","Spring 2025","Fall 2024");

    // ---- courses taught by 'me' ----
    courses.add(new CourseDTO("INFO5100","Application Engineering and Development",
            "Learn Java and enterprise development", 40, "WVH-110", "MW 10:00–11:40", "Fall 2025", me.facultyId));
    courses.add(new CourseDTO("INFO6205","Program Structure and Algorithms",
            "Data structures and algorithms", 35, "ISEC-140", "TR 1:35–3:15", "Fall 2025", me.facultyId));
    courses.add(new CourseDTO("INFO6150","Web Design and User Experience",
            "Modern web development", 45, "EXP-050", "MW 4:35–6:15", "Spring 2025", me.facultyId));
    courses.add(new CourseDTO("INFO5001","Data Science and Analytics",
            "Introduction to data science", 40, "ISEC-120", "TR 9:50–11:30", "Fall 2024", me.facultyId));
    courses.add(new CourseDTO("INFO6350","Smartphones Based Web Development",
            "Mobile app development", 50, "WVH-108", "TR 3:25–5:05", "Spring 2025", me.facultyId));

    // make only the currently-running semester open
    for (CourseDTO c : courses) c.enrollmentOpen = "Fall 2025".equals(c.semester);

    // optional sample syllabus paths (blank by default)
    for (CourseDTO c : courses) c.syllabusPath = null;

    // ---- students ----
    students = Arrays.asList(
            new StudentDTO("S001","Chinmay Sakhare","chinmay@northeastern.edu"),
            new StudentDTO("S002","Bob Baker","bob.baker@northeastern.edu"),
            new StudentDTO("S003","Charlie Carter","charlie.c@northeastern.edu"),
            new StudentDTO("S004","Diana Davis","diana.d@northeastern.edu"),
            new StudentDTO("S005","Ethan Evans","ethan.e@northeastern.edu"),
            new StudentDTO("S006","Fiona Foster","fiona.f@northeastern.edu"),
            new StudentDTO("S007","George Green","george.g@northeastern.edu"),
            new StudentDTO("S008","Hannah Harris","hannah.h@northeastern.edu"),
            new StudentDTO("S009","Ian Irwin","ian.i@northeastern.edu"),
            new StudentDTO("S010","Julia Jackson","julia.j@northeastern.edu")
    );

    // ---- enrollments (also carries billed/paid for tuition tab) ----
    // Assume per-course tuition = 2500 for illustration
    final double TUITION = 2500.0;

    // INFO5100 (Fall 2025)
    enrollments.add(new EnrollmentDTO("INFO5100","S001",TUITION, TUITION));    // paid full
    enrollments.add(new EnrollmentDTO("INFO5100","S002",TUITION, 2000));       // partial
    enrollments.add(new EnrollmentDTO("INFO5100","S003",TUITION, TUITION));
    enrollments.add(new EnrollmentDTO("INFO5100","S007",TUITION, 1500));

    // INFO6205 (Fall 2025)
    enrollments.add(new EnrollmentDTO("INFO6205","S001",TUITION, 2200));
    enrollments.add(new EnrollmentDTO("INFO6205","S002",TUITION, TUITION));
    enrollments.add(new EnrollmentDTO("INFO6205","S008",TUITION, 1000));

    // INFO6150 (Spring 2025)
    enrollments.add(new EnrollmentDTO("INFO6150","S005",TUITION, 1800));
    enrollments.add(new EnrollmentDTO("INFO6150","S007",TUITION, TUITION));
    enrollments.add(new EnrollmentDTO("INFO6150","S008",TUITION, TUITION));

    // INFO5001 (Fall 2024)
    enrollments.add(new EnrollmentDTO("INFO5001","S005",TUITION, TUITION));
    enrollments.add(new EnrollmentDTO("INFO5001","S009",TUITION, 2000));
    enrollments.add(new EnrollmentDTO("INFO5001","S010",TUITION, TUITION));

    // INFO6350 (Spring 2025)
    enrollments.add(new EnrollmentDTO("INFO6350","S006",TUITION, 1000));
    enrollments.add(new EnrollmentDTO("INFO6350","S009",TUITION, TUITION));
    enrollments.add(new EnrollmentDTO("INFO6350","S010",TUITION, 2300));

    // ---- granular grades (used by transcript, ranking, reports) ----
    // INFO5100
    grades.add(new GradeItemDTO("INFO5100","S001","HW1",95,100));
    grades.add(new GradeItemDTO("INFO5100","S001","Project",180,200));
    grades.add(new GradeItemDTO("INFO5100","S001","Midterm",88,100));

    grades.add(new GradeItemDTO("INFO5100","S002","HW1",82,100));
    grades.add(new GradeItemDTO("INFO5100","S002","Project",160,200));
    grades.add(new GradeItemDTO("INFO5100","S002","Midterm",74,100));

    grades.add(new GradeItemDTO("INFO5100","S003","HW1",91,100));
    grades.add(new GradeItemDTO("INFO5100","S003","Project",170,200));
    grades.add(new GradeItemDTO("INFO5100","S003","Midterm",86,100));

    grades.add(new GradeItemDTO("INFO5100","S007","HW1",77,100));
    grades.add(new GradeItemDTO("INFO5100","S007","Project",150,200));
    grades.add(new GradeItemDTO("INFO5100","S007","Midterm",70,100));

    // INFO6205
    grades.add(new GradeItemDTO("INFO6205","S001","Quiz",18,20));
    grades.add(new GradeItemDTO("INFO6205","S001","Midterm",84,100));
    grades.add(new GradeItemDTO("INFO6205","S001","Final",170,200));

    grades.add(new GradeItemDTO("INFO6205","S002","Quiz",19,20));
    grades.add(new GradeItemDTO("INFO6205","S002","Midterm",92,100));
    grades.add(new GradeItemDTO("INFO6205","S002","Final",185,200));

    grades.add(new GradeItemDTO("INFO6205","S008","Quiz",16,20));
    grades.add(new GradeItemDTO("INFO6205","S008","Midterm",70,100));
    grades.add(new GradeItemDTO("INFO6205","S008","Final",150,200));

    // INFO6150
    grades.add(new GradeItemDTO("INFO6150","S005","UX HW",45,50));
    grades.add(new GradeItemDTO("INFO6150","S005","Project",88,100));
    grades.add(new GradeItemDTO("INFO6150","S005","Final",86,100));

    grades.add(new GradeItemDTO("INFO6150","S007","UX HW",48,50));
    grades.add(new GradeItemDTO("INFO6150","S007","Project",92,100));
    grades.add(new GradeItemDTO("INFO6150","S007","Final",94,100));

    grades.add(new GradeItemDTO("INFO6150","S008","UX HW",46,50));
    grades.add(new GradeItemDTO("INFO6150","S008","Project",90,100));
    grades.add(new GradeItemDTO("INFO6150","S008","Final",91,100));

    // INFO5001
    grades.add(new GradeItemDTO("INFO5001","S005","Lab",95,100));
    grades.add(new GradeItemDTO("INFO5001","S005","Midterm",86,100));
    grades.add(new GradeItemDTO("INFO5001","S005","Final",175,200));

    grades.add(new GradeItemDTO("INFO5001","S009","Lab",78,100));
    grades.add(new GradeItemDTO("INFO5001","S009","Midterm",72,100));
    grades.add(new GradeItemDTO("INFO5001","S009","Final",150,200));

    grades.add(new GradeItemDTO("INFO5001","S010","Lab",96,100));
    grades.add(new GradeItemDTO("INFO5001","S010","Midterm",90,100));
    grades.add(new GradeItemDTO("INFO5001","S010","Final",188,200));

    // INFO6350
    grades.add(new GradeItemDTO("INFO6350","S006","App HW",40,50));
    grades.add(new GradeItemDTO("INFO6350","S006","Project",80,100));
    grades.add(new GradeItemDTO("INFO6350","S006","Final",70,100));

    grades.add(new GradeItemDTO("INFO6350","S009","App HW",49,50));
    grades.add(new GradeItemDTO("INFO6350","S009","Project",95,100));
    grades.add(new GradeItemDTO("INFO6350","S009","Final",190,200));

    grades.add(new GradeItemDTO("INFO6350","S010","App HW",48,50));
    grades.add(new GradeItemDTO("INFO6350","S010","Project",92,100));
    grades.add(new GradeItemDTO("INFO6350","S010","Final",186,200));

}

    
    private static Object invoke(Object target, String... names) {
    if (target == null) return null;
    for (String n : names) {
        try { return target.getClass().getMethod(n).invoke(target); } catch (Exception ignore) {}
    }
    return null;
}
    
    
    @SuppressWarnings("unchecked")
private static List<Object> safeList(Object maybeList) {
    if (maybeList == null) return java.util.Collections.emptyList();
    if (maybeList instanceof java.util.Collection) return new java.util.ArrayList<>((java.util.Collection<?>) maybeList);
    return java.util.Collections.emptyList();
}
private static String str(Object o){ return o==null? "": String.valueOf(o).trim(); }
private static Object orDefault(Object o, Object d){ return o==null? d: o; }
private static double letterToPercent(String l){
    switch (l.trim()) {
        case "A": return 95; case "A-": return 91;
        case "B+": return 88; case "B": return 85; case "B-": return 81;
        case "C+": return 78; case "C": return 75; case "C-": return 71;
        case "D": return 65; default: return 55;
    }
}
    
}

// ---- in-memory fallback (works immediately) ----
static class InMemoryDataProvider extends BusinessDataProvider {
    InMemoryDataProvider(){ super(null); }
}

    /**
     * Creates new form FacultyConsole
     */
   public FacultyConsole(Business business, JPanel CardSequencePanel) {
    this.business = business;
    this.cardPanel = CardSequencePanel;
    initComponents();
    DataProvider dp;
    
try {
    dp = new BusinessDataProvider(business);
    if (dp.getMyCourses()==null || dp.getMyCourses().isEmpty()) {
        dp = new InMemoryDataProvider();
    }
} catch (Throwable t) {
    dp = new InMemoryDataProvider();
}
this.data = dp;
    initAfterGui();
}
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelCourses = new javax.swing.JPanel();
        btnOpenEnrollment = new javax.swing.JButton();
        btnCloseEnrollment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCourses = new javax.swing.JTable();
        btnUpdate = new javax.swing.JButton();
        btnCourseSaveSyllabus = new javax.swing.JButton();
        btnCourseBack = new javax.swing.JButton();
        jPanelStudents = new javax.swing.JPanel();
        cboCourses = new javax.swing.JComboBox<>();
        lblStudentCourse = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnStudentTranscript = new javax.swing.JButton();
        btnStudentGrade = new javax.swing.JButton();
        btnStudentViewRanking = new javax.swing.JButton();
        btnStudentBack = new javax.swing.JButton();
        jPanelProfiles = new javax.swing.JPanel();
        lblProfileFacultyID = new javax.swing.JLabel();
        lblProfilesName = new javax.swing.JLabel();
        lblProfilesEmail = new javax.swing.JLabel();
        lblProfilesPhone = new javax.swing.JLabel();
        lblProfilesDepartment = new javax.swing.JLabel();
        lblProfilesOfficehrs = new javax.swing.JLabel();
        txtProfilesFacultyID = new javax.swing.JTextField();
        txtProfileName = new javax.swing.JTextField();
        txtProfilesEmail = new javax.swing.JTextField();
        txtProfilesPhone = new javax.swing.JTextField();
        txtProfilesDepatment = new javax.swing.JTextField();
        txtProfileOfficeHrs = new javax.swing.JTextField();
        btnProfilesEdit = new javax.swing.JButton();
        btnProfilesSave = new javax.swing.JButton();
        btnProfilesBack = new javax.swing.JButton();
        lblReportsSemester = new javax.swing.JPanel();
        lblReportSemester = new javax.swing.JLabel();
        lblReportsCourse = new javax.swing.JLabel();
        cboReportSemester = new javax.swing.JComboBox<>();
        cboReportsCourse = new javax.swing.JComboBox<>();
        btnReportsLoad = new javax.swing.JButton();
        btnReportsExport = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblReports = new javax.swing.JTable();
        btnReportsBack = new javax.swing.JButton();
        jPanelTuition = new javax.swing.JPanel();
        tblTuition = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnTuitionBack = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 153, 153));

        jTabbedPane1.setBackground(new java.awt.Color(0, 204, 204));

        jPanelCourses.setBackground(new java.awt.Color(0, 153, 153));

        btnOpenEnrollment.setText("Open Enrolment");
        btnOpenEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenEnrollmentActionPerformed(evt);
            }
        });

        btnCloseEnrollment.setText("close Enrollment");

        tblCourses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Title", "Description", "Capacity", "Room", "time"
            }
        ));
        jScrollPane1.setViewportView(tblCourses);

        btnUpdate.setText("Update ");

        btnCourseSaveSyllabus.setText("Save Syllabus");

        btnCourseBack.setText("Back");
        btnCourseBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCourseBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCoursesLayout = new javax.swing.GroupLayout(jPanelCourses);
        jPanelCourses.setLayout(jPanelCoursesLayout);
        jPanelCoursesLayout.setHorizontalGroup(
            jPanelCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCoursesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCourseBack)
                .addGap(184, 184, 184))
            .addGroup(jPanelCoursesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanelCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelCoursesLayout.createSequentialGroup()
                        .addComponent(btnOpenEnrollment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCloseEnrollment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCourseSaveSyllabus)
                        .addGap(0, 247, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelCoursesLayout.setVerticalGroup(
            jPanelCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCoursesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCourseBack)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jPanelCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenEnrollment)
                    .addComponent(btnCloseEnrollment)
                    .addComponent(btnUpdate)
                    .addComponent(btnCourseSaveSyllabus))
                .addContainerGap(310, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Courses Management", jPanelCourses);

        jPanelStudents.setBackground(new java.awt.Color(0, 153, 153));

        cboCourses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        cboCourses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCoursesActionPerformed(evt);
            }
        });

        lblStudentCourse.setText("Courses");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Student ID", "Name", "Email", "Total%", "Letter", "GPA"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        btnStudentTranscript.setText("Access Transcript");

        btnStudentGrade.setText("Grade Assigning");
        btnStudentGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStudentGradeActionPerformed(evt);
            }
        });

        btnStudentViewRanking.setText("view Ranking");

        btnStudentBack.setText("Back");

        javax.swing.GroupLayout jPanelStudentsLayout = new javax.swing.GroupLayout(jPanelStudents);
        jPanelStudents.setLayout(jPanelStudentsLayout);
        jPanelStudentsLayout.setHorizontalGroup(
            jPanelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStudentsLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(btnStudentTranscript)
                .addGap(18, 18, 18)
                .addComponent(btnStudentGrade)
                .addGap(30, 30, 30)
                .addComponent(btnStudentViewRanking)
                .addContainerGap(243, Short.MAX_VALUE))
            .addGroup(jPanelStudentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStudentCourse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboCourses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnStudentBack)
                .addGap(71, 71, 71))
            .addGroup(jPanelStudentsLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanelStudentsLayout.setVerticalGroup(
            jPanelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStudentsLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCourses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStudentCourse)
                    .addComponent(btnStudentBack))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStudentTranscript)
                    .addComponent(btnStudentGrade)
                    .addComponent(btnStudentViewRanking))
                .addContainerGap(267, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Students Management", jPanelStudents);

        jPanelProfiles.setBackground(new java.awt.Color(0, 153, 153));

        lblProfileFacultyID.setText("Faculty ID");

        lblProfilesName.setText("Name");

        lblProfilesEmail.setText("Email ID");

        lblProfilesPhone.setText("Phone");

        lblProfilesDepartment.setText("Department");

        lblProfilesOfficehrs.setText("Office Hrs");

        txtProfilesDepatment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProfilesDepatmentActionPerformed(evt);
            }
        });

        btnProfilesEdit.setText("Edit");

        btnProfilesSave.setText("Save");
        btnProfilesSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfilesSaveActionPerformed(evt);
            }
        });

        btnProfilesBack.setText("Back");

        javax.swing.GroupLayout jPanelProfilesLayout = new javax.swing.GroupLayout(jPanelProfiles);
        jPanelProfiles.setLayout(jPanelProfilesLayout);
        jPanelProfilesLayout.setHorizontalGroup(
            jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProfilesLayout.createSequentialGroup()
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProfilesLayout.createSequentialGroup()
                        .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProfilesLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(lblProfilesEmail))
                            .addGroup(jPanelProfilesLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblProfileFacultyID)
                                    .addComponent(lblProfilesPhone)
                                    .addComponent(lblProfilesDepartment)
                                    .addComponent(lblProfilesOfficehrs)
                                    .addComponent(lblProfilesName))))
                        .addGap(68, 68, 68)
                        .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtProfileOfficeHrs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(txtProfilesPhone, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProfilesEmail, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProfileName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProfilesFacultyID, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProfilesDepatment))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)
                        .addComponent(btnProfilesBack))
                    .addGroup(jPanelProfilesLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btnProfilesEdit)
                        .addGap(79, 79, 79)
                        .addComponent(btnProfilesSave)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(56, 56, 56))
        );
        jPanelProfilesLayout.setVerticalGroup(
            jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProfilesLayout.createSequentialGroup()
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProfilesLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProfileFacultyID)
                            .addComponent(txtProfilesFacultyID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelProfilesLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(btnProfilesBack)))
                .addGap(18, 18, 18)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfilesName)
                    .addComponent(txtProfileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProfilesEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfilesEmail))
                .addGap(18, 18, 18)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProfilesPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfilesPhone))
                .addGap(16, 16, 16)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProfilesDepatment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfilesDepartment))
                .addGap(18, 18, 18)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProfileOfficeHrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfilesOfficehrs))
                .addGap(85, 85, 85)
                .addGroup(jPanelProfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProfilesEdit)
                    .addComponent(btnProfilesSave))
                .addContainerGap(185, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Profiles Management", jPanelProfiles);

        lblReportsSemester.setBackground(new java.awt.Color(0, 153, 153));

        lblReportSemester.setText("Semester");

        lblReportsCourse.setText("Course");

        btnReportsLoad.setText("Load");
        btnReportsLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsLoadActionPerformed(evt);
            }
        });

        btnReportsExport.setText("Export");
        btnReportsExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsExportActionPerformed(evt);
            }
        });

        tblReports.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Letter Grade", "Count", "Percentage%"
            }
        ));
        jScrollPane4.setViewportView(tblReports);
        if (tblReports.getColumnModel().getColumnCount() > 0) {
            tblReports.getColumnModel().getColumn(2).setResizable(false);
        }

        btnReportsBack.setText("Back");

        javax.swing.GroupLayout lblReportsSemesterLayout = new javax.swing.GroupLayout(lblReportsSemester);
        lblReportsSemester.setLayout(lblReportsSemesterLayout);
        lblReportsSemesterLayout.setHorizontalGroup(
            lblReportsSemesterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblReportsSemesterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReportsBack)
                .addGap(71, 71, 71))
            .addGroup(lblReportsSemesterLayout.createSequentialGroup()
                .addGroup(lblReportsSemesterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblReportsSemesterLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lblReportsSemesterLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(lblReportsSemesterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnReportsExport)
                            .addGroup(lblReportsSemesterLayout.createSequentialGroup()
                                .addComponent(lblReportSemester)
                                .addGap(18, 18, 18)
                                .addComponent(cboReportSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(lblReportsCourse)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboReportsCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnReportsLoad)))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        lblReportsSemesterLayout.setVerticalGroup(
            lblReportsSemesterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblReportsSemesterLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(lblReportsSemesterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReportSemester)
                    .addComponent(lblReportsCourse)
                    .addComponent(cboReportSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboReportsCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReportsLoad))
                .addGap(18, 18, 18)
                .addComponent(btnReportsExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReportsBack)
                .addGap(98, 98, 98))
        );

        jTabbedPane1.addTab("Performance Reports", lblReportsSemester);

        jPanelTuition.setBackground(new java.awt.Color(0, 153, 153));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Student ID", "Billed", "Paid", "Remaining Due"
            }
        ));
        tblTuition.setViewportView(jTable1);

        btnTuitionBack.setText("Back");

        javax.swing.GroupLayout jPanelTuitionLayout = new javax.swing.GroupLayout(jPanelTuition);
        jPanelTuition.setLayout(jPanelTuitionLayout);
        jPanelTuitionLayout.setHorizontalGroup(
            jPanelTuitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTuitionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTuitionBack)
                .addGap(42, 42, 42))
            .addGroup(jPanelTuitionLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(tblTuition, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanelTuitionLayout.setVerticalGroup(
            jPanelTuitionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTuitionLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(tblTuition, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(btnTuitionBack)
                .addGap(65, 65, 65))
        );

        jTabbedPane1.addTab("Enrollment Insighnt", jPanelTuition);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(98, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStudentGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentGradeActionPerformed
        // TODO add your handling code here:
        String sel = (String) cboCourses.getSelectedItem();
if (sel == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
String cid = sel.contains(" - ") ? sel.substring(0, sel.indexOf(" - ")) : sel;

int row = jTable2.getSelectedRow();
if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student to grade."); return; }
int modelRow = jTable2.convertRowIndexToModel(row);
String sid = String.valueOf(((javax.swing.table.DefaultTableModel) jTable2.getModel()).getValueAt(modelRow, 0));

String item = JOptionPane.showInputDialog(this, "Grade Item (e.g., HW2):", "HW2");
if (item == null || item.trim().isEmpty()) return;
String earnedStr = JOptionPane.showInputDialog(this, "Points Earned:", "85");
String maxStr = JOptionPane.showInputDialog(this, "Points Max:", "100");
try {
    double e = Double.parseDouble(earnedStr);
    double m = Double.parseDouble(maxStr);
    if (m <= 0) { JOptionPane.showMessageDialog(this, "Max must be > 0."); return; }
    data.addGrade(new GradeItemDTO(cid, sid, item, e, m));
    JOptionPane.showMessageDialog(this, "Grade saved.");
    selectCourseFromCombo();
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Invalid number: " + ex.getMessage());
}

    }//GEN-LAST:event_btnStudentGradeActionPerformed

    private void btnOpenEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenEnrollmentActionPerformed
        {  setEnrollment(true);
        }
    }//GEN-LAST:event_btnOpenEnrollmentActionPerformed

    private void txtProfilesDepatmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProfilesDepatmentActionPerformed
        // TODO add your handling code here:
        String v = txtProfilesDepatment.getText().trim();
    txtProfilesDepatment.setText(v);
    if (v.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Department can't be empty.");
        txtProfilesDepatment.requestFocus();
        return;
    }
    txtProfileOfficeHrs.requestFocusInWindow();
    }//GEN-LAST:event_txtProfilesDepatmentActionPerformed

    private void btnProfilesSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfilesSaveActionPerformed
        // TODO add your handling code here:
        FacultyProfileDTO p = data.getMe();
p.name = txtProfileName.getText().trim();
p.email = txtProfilesEmail.getText().trim();
p.phone = txtProfilesPhone.getText().trim();
p.department = txtProfilesDepatment.getText().trim();
p.officeHours = txtProfileOfficeHrs.getText().trim();
setProfileEditable(false);
JOptionPane.showMessageDialog(this,"Profile saved.");

    }//GEN-LAST:event_btnProfilesSaveActionPerformed

    private void btnReportsLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsLoadActionPerformed
        // TODO add your handling code here:
        String sem = (String) cboReportSemester.getSelectedItem();
String courseSel = (String) cboReportsCourse.getSelectedItem();
if (sem == null || courseSel == null) { JOptionPane.showMessageDialog(this, "Select semester & course."); return; }
String cid = courseSel.substring(0, courseSel.indexOf(" - "));

Map<String,Integer> dist = new LinkedHashMap<>();
java.util.Arrays.asList("A","A-","B+","B","B-","C+","C","C-","D","F").forEach(k -> dist.put(k, 0));

java.util.List<EnrollmentDTO> es = data.enrollmentsForCourse(cid);
double totalPct = 0; int count = 0;
for (EnrollmentDTO e : es) {
    double[] agg = aggregateGrade(cid, e.studentId);
    double pct = agg[1] == 0 ? 0 : (agg[0] / agg[1]) * 100.0;
    String letter = toLetter(pct);
    dist.put(letter, dist.get(letter) + 1);
    totalPct += pct; count++;
}

reportModel.setRowCount(0);
int total = Math.max(1, count);
for (Map.Entry<String,Integer> en : dist.entrySet()) {
    double perc = (en.getValue() * 100.0) / total;
    reportModel.addRow(new Object[]{ en.getKey(), en.getValue(), String.format(java.util.Locale.US, "%.2f", perc) });
}
double avg = count == 0 ? 0 : totalPct / count;
reportModel.addRow(new Object[]{ "Average %", String.format(java.util.Locale.US, "%.2f", avg), "" });
reportModel.addRow(new Object[]{ "Approx Class GPA", String.format(java.util.Locale.US, "%.2f", toGpa(toLetter(avg))), "" });

    }//GEN-LAST:event_btnReportsLoadActionPerformed

    private void btnReportsExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsExportActionPerformed
        // TODO add your handling code here:
        if (reportModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "No report loaded."); return; }
javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
chooser.setDialogTitle("Export CSV");
chooser.setSelectedFile(new java.io.File("course_report_" + java.time.LocalDate.now() + ".csv"));
int res = chooser.showSaveDialog(this);
if (res == javax.swing.JFileChooser.APPROVE_OPTION) {
    java.io.File f = chooser.getSelectedFile();
    try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(f), java.nio.charset.StandardCharsets.UTF_8))) {
        for (int c = 0; c < reportModel.getColumnCount(); c++) {
            pw.print(reportModel.getColumnName(c));
            if (c < reportModel.getColumnCount() - 1) pw.print(",");
        }
        pw.println();
        for (int r = 0; r < reportModel.getRowCount(); r++) {
            for (int c = 0; c < reportModel.getColumnCount(); c++) {
                Object v = reportModel.getValueAt(r, c);
                pw.print(v == null ? "" : v.toString());
                if (c < reportModel.getColumnCount() - 1) pw.print(",");
            }
            pw.println();
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        return;
    }
    JOptionPane.showMessageDialog(this, "Exported.");
}

    }//GEN-LAST:event_btnReportsExportActionPerformed

    private void btnStudentTranscriptActionPerformed(java.awt.event.ActionEvent evt) {
    String sel = (String) cboCourses.getSelectedItem();
    if (sel == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
    String cid = sel.contains(" - ") ? sel.substring(0, sel.indexOf(" - ")) : sel;

    int row = jTable2.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student."); return; }
    int mr = jTable2.convertRowIndexToModel(row);
    String sid = String.valueOf(studentModel.getValueAt(mr, 0));

    showTranscriptDialog(cid, sid);
}
    
    private void btnStudentViewRankingActionPerformed(java.awt.event.ActionEvent evt) {
    String sel = (String) cboCourses.getSelectedItem();
    if (sel == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
    String cid = sel.contains(" - ") ? sel.substring(0, sel.indexOf(" - ")) : sel;
    showRankingDialog(cid);
}
    
    private void cboCoursesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCoursesActionPerformed
        // TODO add your handling code here:
        selectCourseFromCombo();

    }//GEN-LAST:event_cboCoursesActionPerformed

    private void btnCourseBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCourseBackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCourseBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseEnrollment;
    private javax.swing.JButton btnCourseBack;
    private javax.swing.JButton btnCourseSaveSyllabus;
    private javax.swing.JButton btnOpenEnrollment;
    private javax.swing.JButton btnProfilesBack;
    private javax.swing.JButton btnProfilesEdit;
    private javax.swing.JButton btnProfilesSave;
    private javax.swing.JButton btnReportsBack;
    private javax.swing.JButton btnReportsExport;
    private javax.swing.JButton btnReportsLoad;
    private javax.swing.JButton btnStudentBack;
    private javax.swing.JButton btnStudentGrade;
    private javax.swing.JButton btnStudentTranscript;
    private javax.swing.JButton btnStudentViewRanking;
    private javax.swing.JButton btnTuitionBack;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboCourses;
    private javax.swing.JComboBox<String> cboReportSemester;
    private javax.swing.JComboBox<String> cboReportsCourse;
    private javax.swing.JPanel jPanelCourses;
    private javax.swing.JPanel jPanelProfiles;
    private javax.swing.JPanel jPanelStudents;
    private javax.swing.JPanel jPanelTuition;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblProfileFacultyID;
    private javax.swing.JLabel lblProfilesDepartment;
    private javax.swing.JLabel lblProfilesEmail;
    private javax.swing.JLabel lblProfilesName;
    private javax.swing.JLabel lblProfilesOfficehrs;
    private javax.swing.JLabel lblProfilesPhone;
    private javax.swing.JLabel lblReportSemester;
    private javax.swing.JLabel lblReportsCourse;
    private javax.swing.JPanel lblReportsSemester;
    private javax.swing.JLabel lblStudentCourse;
    private javax.swing.JTable tblCourses;
    private javax.swing.JTable tblReports;
    private javax.swing.JScrollPane tblTuition;
    private javax.swing.JTextField txtProfileName;
    private javax.swing.JTextField txtProfileOfficeHrs;
    private javax.swing.JTextField txtProfilesDepatment;
    private javax.swing.JTextField txtProfilesEmail;
    private javax.swing.JTextField txtProfilesFacultyID;
    private javax.swing.JTextField txtProfilesPhone;
    // End of variables declaration//GEN-END:variables

    private DefaultTableModel facultyModel;
    private JTable tblFaculty;
    private JButton btnFacultyAdd, btnFacultyEdit, btnFacultyDelete;
    
    private String selectedCourseIdFromCombo() {
    String sel = (String) cboCourses.getSelectedItem();
    if (sel == null || sel.trim().isEmpty()) return null;
    int idx = sel.indexOf(" - ");
    return idx > 0 ? sel.substring(0, idx) : sel;
}
    
    
    private JPanel getFormOnlyPanel() {
    JPanel p = new JPanel(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
    c.insets = new java.awt.Insets(6,12,6,12); c.anchor = java.awt.GridBagConstraints.WEST;
    int y=0;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Faculty ID"), c); c.gridx=1; p.add(txtProfilesFacultyID, c); y++;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Name"), c); c.gridx=1; p.add(txtProfileName, c); y++;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Email ID"), c); c.gridx=1; p.add(txtProfilesEmail, c); y++;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Phone"), c); c.gridx=1; p.add(txtProfilesPhone, c); y++;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Department"), c); c.gridx=1; p.add(txtProfilesDepatment, c); y++;
    c.gridx=0;c.gridy=y; p.add(new JLabel("Office Hrs"), c); c.gridx=1; p.add(txtProfileOfficeHrs, c); y++;
    JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    buttons.add(btnProfilesEdit); buttons.add(btnProfilesSave);
    c.gridx=0;c.gridy=y;c.gridwidth=2; p.add(buttons, c);
    return p;
}

private Component buildSpacer(int h){
    JPanel s=new JPanel(); s.setPreferredSize(new Dimension(1,h)); return s;
}

private void refreshFacultyTable(){
    facultyModel.setRowCount(0);
    for (FacultyProfileDTO f : data.getFacultyDirectory()){
        facultyModel.addRow(new Object[]{f.facultyId,f.name,f.email,f.phone,f.department,f.officeHours});
    }
}

private void addOrEditFaculty(String id){
    FacultyProfileDTO f = (id==null)? new FacultyProfileDTO("", "", "", "", "", "") :
            data.getFacultyDirectory().stream().filter(x->x.facultyId.equals(id)).findFirst().orElse(null);
    if (f==null && id!=null){ JOptionPane.showMessageDialog(this,"Not found."); return; }

    JTextField tId = new JTextField(f.facultyId,20);
    JTextField tNm = new JTextField(f.name,20);
    JTextField tEm = new JTextField(f.email,20);
    JTextField tPh = new JTextField(f.phone,20);
    JTextField tDp = new JTextField(f.department,20);
    JTextField tHr = new JTextField(f.officeHours,20);

    JPanel panel = new JPanel(new java.awt.GridLayout(0,2,8,8));
    panel.add(new JLabel("Faculty ID")); panel.add(tId);
    panel.add(new JLabel("Name"));       panel.add(tNm);
    panel.add(new JLabel("Email"));      panel.add(tEm);
    panel.add(new JLabel("Phone"));      panel.add(tPh);
    panel.add(new JLabel("Department")); panel.add(tDp);
    panel.add(new JLabel("Office Hrs")); panel.add(tHr);

    int res = JOptionPane.showConfirmDialog(this, panel, (id==null?"Add Faculty":"Edit Faculty"), JOptionPane.OK_CANCEL_OPTION);
    if (res == JOptionPane.OK_OPTION){
        if (tId.getText().trim().isEmpty()){ JOptionPane.showMessageDialog(this,"Faculty ID is required."); return; }
        FacultyProfileDTO nf = new FacultyProfileDTO(
                tId.getText().trim(), tNm.getText().trim(), tEm.getText().trim(),
                tPh.getText().trim(), tDp.getText().trim(), tHr.getText().trim()
        );
        data.upsertFaculty(nf);
        refreshFacultyTable();
    }
}



    
    private void showTranscriptDialog(String courseId, String studentId) {
    JDialog dlg = new JDialog(
        SwingUtilities.getWindowAncestor(this),
        "Transcript",
        java.awt.Dialog.ModalityType.APPLICATION_MODAL
    );
    DefaultTableModel m = new DefaultTableModel(new Object[]{"Item","Earned","Max","%","Letter"}, 0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    JTable t = new JTable(m);
    double earned = 0, max = 0;
    for (GradeItemDTO g : data.getTranscriptItems(courseId, studentId)) {
        double pct = g.pointsMax == 0 ? 0 : (g.pointsEarned*100.0/g.pointsMax);
        m.addRow(new Object[]{ g.item, g.pointsEarned, g.pointsMax,
                String.format(Locale.US,"%.2f", pct), toLetter(pct) });
        earned += g.pointsEarned; max += g.pointsMax;
    }
    double totalPct = max==0 ? 0 : (earned*100.0/max);
    String letter = toLetter(totalPct);
    double gpa = toGpa(letter);
    final double earnedFinal   = earned;
    final double maxFinal      = max;
    final double totalPctFinal = totalPct;
    final String letterFinal   = letter;

    JLabel summary = new JLabel(String.format(Locale.US,
            "Total: %.2f / %.2f  |  Percent: %.2f%%  |  Letter: %s  |  GPA: %.2f",
            earned, max, totalPct, letter, gpa));

    JButton export = new JButton("Export CSV");
export.addActionListener(e -> {
    JFileChooser fc = new JFileChooser();
    fc.setSelectedFile(new File("transcript_"+studentId+"_"+courseId+".csv"));
    if (fc.showSaveDialog(dlg)==JFileChooser.APPROVE_OPTION){
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(fc.getSelectedFile()), StandardCharsets.UTF_8))) {

            pw.println("Item,Earned,Max,Percent,Letter");
            for (int r = 0; r < m.getRowCount(); r++) {
                pw.println(m.getValueAt(r,0)+","+m.getValueAt(r,1)+","+
                           m.getValueAt(r,2)+","+m.getValueAt(r,3)+","+
                           m.getValueAt(r,4));
            }

            // 👇 use the final snapshots here
            pw.printf(Locale.US, "TOTAL,%.2f,%.2f,%.2f,%s%n",
                      earnedFinal, maxFinal, totalPctFinal, letterFinal);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dlg, "Export failed: " + ex.getMessage());
        }
    }
});

    JPanel south = new JPanel(new java.awt.BorderLayout(8,8));
    south.add(summary, java.awt.BorderLayout.CENTER);
    south.add(export,  java.awt.BorderLayout.EAST);

    dlg.getContentPane().setLayout(new java.awt.BorderLayout(8,8));
    dlg.add(new JScrollPane(t), java.awt.BorderLayout.CENTER);
    dlg.add(south, java.awt.BorderLayout.SOUTH);
    dlg.setSize(700, 400);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
}

private void showRankingDialog(String courseId){
    // build rank list
    class Row { String sid, name; double pct; String letter; double gpa; }
    java.util.List<Row> rows = new ArrayList<>();
    for (EnrollmentDTO e : data.enrollmentsForCourse(courseId)) {
        StudentDTO s = data.findStudent(e.studentId); if (s==null) continue;
        double[] agg = aggregateGrade(courseId, s.id);
        double pct = agg[1]==0 ? 0 : (agg[0]/agg[1])*100.0;
        Row r = new Row(); r.sid=s.id; r.name=s.name; r.pct=pct; r.letter=toLetter(pct); r.gpa=toGpa(r.letter);
        rows.add(r);
    }
    rows.sort((a,b)->Double.compare(b.pct,a.pct)); // descending

    DefaultTableModel m = new DefaultTableModel(new Object[]{"Rank","Student ID","Name","Total%","Letter","GPA"}, 0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    double gpaSum=0;
    for (int i=0;i<rows.size();i++){
        Row r = rows.get(i);
        gpaSum += r.gpa;
        m.addRow(new Object[]{ i+1, r.sid, r.name, String.format(Locale.US,"%.2f",r.pct), r.letter, String.format(Locale.US,"%.2f", r.gpa) });
    }
    double classGpa = rows.isEmpty()?0:(gpaSum/rows.size());

    JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Ranking", Dialog.ModalityType.APPLICATION_MODAL);
    JTable t = new JTable(m);
    JLabel footer = new JLabel(String.format(Locale.US,"Class average GPA: %.2f", classGpa));
    dlg.getContentPane().setLayout(new java.awt.BorderLayout(8,8));
    dlg.add(new JScrollPane(t), java.awt.BorderLayout.CENTER);
    dlg.add(footer, java.awt.BorderLayout.SOUTH);
    dlg.setSize(650, 400);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);
}
    
    
private void refreshReportCoursesCombo() {
    cboReportsCourse.removeAllItems();
    String sem = (String) cboReportSemester.getSelectedItem();
    if (sem == null) return;
    for (CourseDTO c : data.getMyCourses()) {
        if (sem.equals(c.semester)) cboReportsCourse.addItem(c.id + " - " + c.title);
    }
    if (cboReportsCourse.getItemCount() > 0) cboReportsCourse.setSelectedIndex(0);
}

private void loadTuitionTable() {
    tuitionModel.setRowCount(0);
    double billedSum = 0, paidSum = 0, dueSum = 0;
    for (EnrollmentDTO en : data.getEnrollments()) {
        StudentDTO s = data.findStudent(en.studentId);
        if (s == null) continue;
        double due = Math.max(0, en.billed - en.paid);
        billedSum += en.billed; paidSum += en.paid; dueSum += due;
        tuitionModel.addRow(new Object[]{
                en.studentId, s.name, en.courseId,
                String.format(java.util.Locale.US, "%.2f", en.billed),
                String.format(java.util.Locale.US, "%.2f", en.paid),
                String.format(java.util.Locale.US, "%.2f", due)
        });
    }
    // Add a totals row
    tuitionModel.addRow(new Object[]{
            "— TOTAL —", "", "",
            String.format(java.util.Locale.US, "%.2f", billedSum),
            String.format(java.util.Locale.US, "%.2f", paidSum),
            String.format(java.util.Locale.US, "%.2f", dueSum)
    });
}

    
    private void loadReportSemesterCombo() {
    cboReportSemester.removeAllItems();
    for (String s : data.getSemesters()) {
        cboReportSemester.addItem(s);
    }
    if (cboReportSemester.getItemCount() > 0) {
        cboReportSemester.setSelectedIndex(0);
    }
    refreshReportCoursesCombo();
    // keep report courses in sync with semester
    cboReportSemester.addActionListener(e -> refreshReportCoursesCombo());
}
    
    
    
    
    
    private void setProfileEditable(boolean editable) {
    // keep Faculty ID read-only
    txtProfilesFacultyID.setEditable(false);

    txtProfileName.setEditable(editable);
    txtProfilesEmail.setEditable(editable);
    txtProfilesPhone.setEditable(editable);
    txtProfilesDepatment.setEditable(editable);
    txtProfileOfficeHrs.setEditable(editable);
}
    private void initAfterGui() {
    // course table
    courseModel = new DefaultTableModel(
            new Object[]{"Course ID","Title","Description","Capacity","Room","Time","Semester","Enroll Open","Syllabus"}, 0){
        @Override public boolean isCellEditable(int r, int c){ return c!=0 && c!=7; }
    };
    tblCourses.setModel(courseModel);
    sorter = new TableRowSorter<>(tblCourses.getModel());
    tblCourses.setRowSorter(sorter);

    // students table
    studentModel = new DefaultTableModel(
            new Object[]{"Student ID","Name","Email","Total%","Letter","GPA"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    jTable2.setModel(studentModel);

    // reports table
    reportModel = new DefaultTableModel(
            new Object[]{"Letter Grade","Count","Percentage%"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    tblReports.setModel(reportModel);

    // tuition table
    tuitionModel = new DefaultTableModel(
            new Object[]{"Student ID","Student Name","Course ID","Billed","Paid","Remaining Due"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    jTable1.setModel(tuitionModel);

    // load initial data
    refreshCoursesTable();
    loadCourseCombo();
    loadReportSemesterCombo();
    loadTuitionTable();

    // load profile
    FacultyProfileDTO p = data.getMe();
    txtProfilesFacultyID.setText(p.facultyId);
    txtProfileName.setText(p.name);
    txtProfilesEmail.setText(p.email);
    txtProfilesPhone.setText(p.phone);
    txtProfilesDepatment.setText(p.department);
    txtProfileOfficeHrs.setText(p.officeHours);

    // profile fields readonly by default
    setProfileEditable(false);
    btnCloseEnrollment.addActionListener(e -> setEnrollment(false));
    btnUpdate.addActionListener(e -> showCourseEditor());
    btnCourseSaveSyllabus.addActionListener(e -> saveSyllabus());
    btnProfilesEdit.addActionListener(e -> setProfileEditable(true));
    
    
    JPanel dirPanel = new JPanel(new java.awt.BorderLayout(8,8));
dirPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Faculty Directory"));

facultyModel = new DefaultTableModel(
    new Object[]{"Faculty ID","Name","Email","Phone","Department","Office Hrs"},0){
    @Override public boolean isCellEditable(int r,int c){ return false; }
};
tblFaculty = new JTable(facultyModel);
dirPanel.add(new JScrollPane(tblFaculty), java.awt.BorderLayout.CENTER);

JPanel btns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
btnFacultyAdd = new JButton("Add");
btnFacultyEdit = new JButton("Edit Selected");
btnFacultyDelete = new JButton("Delete Selected");
btns.add(btnFacultyAdd); btns.add(btnFacultyEdit); btns.add(btnFacultyDelete);
dirPanel.add(btns, java.awt.BorderLayout.SOUTH);

// ➕ put the directory on its own tab (no reparenting of profile fields)
jTabbedPane1.addTab("Faculty Directory", dirPanel);

// wire actions (unchanged)
btnFacultyAdd.addActionListener(e -> addOrEditFaculty(null));
btnFacultyEdit.addActionListener(e -> {
    int r = tblFaculty.getSelectedRow(); if (r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    addOrEditFaculty(String.valueOf(facultyModel.getValueAt(tblFaculty.convertRowIndexToModel(r),0)));
});
btnFacultyDelete.addActionListener(e -> {
    int r = tblFaculty.getSelectedRow(); if (r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    String id = String.valueOf(facultyModel.getValueAt(tblFaculty.convertRowIndexToModel(r),0));
    if (JOptionPane.showConfirmDialog(this,"Delete "+id+"?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
        data.deleteFaculty(id); refreshFacultyTable();
    }
});

// finally load the directory rows
refreshFacultyTable();

// wire actions
btnFacultyAdd.addActionListener(e -> addOrEditFaculty(null));
btnFacultyEdit.addActionListener(e -> {
    int r = tblFaculty.getSelectedRow(); if (r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    addOrEditFaculty(String.valueOf(facultyModel.getValueAt(tblFaculty.convertRowIndexToModel(r),0)));
});
btnFacultyDelete.addActionListener(e -> {
    int r = tblFaculty.getSelectedRow(); if (r<0){ JOptionPane.showMessageDialog(this,"Select a row."); return; }
    String id = String.valueOf(facultyModel.getValueAt(tblFaculty.convertRowIndexToModel(r),0));
    if (JOptionPane.showConfirmDialog(this,"Delete "+id+"?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
        data.deleteFaculty(id); refreshFacultyTable();
    }
});
 

btnCourseSaveSyllabus.addActionListener(e -> saveSyllabus());
btnOpenEnrollment.addActionListener(e -> setEnrollment(true));
btnCloseEnrollment.addActionListener(e -> setEnrollment(false));
btnUpdate.addActionListener(e -> showCourseEditor());

btnStudentTranscript.addActionListener(this::btnStudentTranscriptActionPerformed);
btnStudentViewRanking.addActionListener(this::btnStudentViewRankingActionPerformed);


}

    private void showCourseEditor() {
    int row = tblCourses.getSelectedRow();
    if (row < 0) { JOptionPane.showMessageDialog(this, "Select a course row to edit."); return; }
    int modelRow = tblCourses.convertRowIndexToModel(row);
    String id = String.valueOf(courseModel.getValueAt(modelRow, 0));
    CourseDTO c = data.findCourse(id);
    if (c == null) { JOptionPane.showMessageDialog(this, "Course not found."); return; }

    CourseEditDialog dlg = new CourseEditDialog(SwingUtilities.getWindowAncestor(this), c);
    dlg.setLocationRelativeTo(this);
    dlg.setVisible(true);             // blocks until closed

    if (dlg.isSaved()) {
        data.updateCourse(c);         // you can persist to Business here if you want
        refreshCoursesTable();
        // keep the same row selected after refresh
        for (int i = 0; i < courseModel.getRowCount(); i++) {
            if (String.valueOf(courseModel.getValueAt(i,0)).equals(id)) {
                int viewIndex = tblCourses.convertRowIndexToView(i);
                tblCourses.setRowSelectionInterval(viewIndex, viewIndex);
                break;
            }
        }
    }
}

    
    private final class CourseEditDialog extends JDialog {
    private final CourseDTO course;
    private final DefaultTableModel model;
    private boolean saved = false;

    CourseEditDialog(java.awt.Window owner, CourseDTO course) {
        super(owner, "Edit Course: " + course.id, ModalityType.APPLICATION_MODAL);
        this.course = course;

        // table with one editable row
        model = new DefaultTableModel(
                new Object[]{"Title","Description","Capacity","Room","Time","Semester","Enrollment Open","Syllabus"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 2) return Integer.class;             // capacity
                if (c == 6) return Boolean.class;             // enrollment open
                return String.class;
            }
        };
        model.addRow(new Object[]{
                course.title,
                course.description,
                course.capacity,
                course.room,
                course.time,
                course.semester,
                course.enrollmentOpen,
                course.syllabusPath == null ? "" : course.syllabusPath
        });

        JTable table = new JTable(model);
        table.setRowHeight(24);
        JScrollPane sp = new JScrollPane(table);

        // bottom buttons
        JButton btnChooseSyllabus = new JButton("Browse Syllabus...");
        btnChooseSyllabus.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                model.setValueAt(fc.getSelectedFile().getAbsolutePath(), 0, 7);
            }
        });

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
    try {
        // commit any in-progress cell edit
        if (table.isEditing() && table.getCellEditor() != null) {
            if (!table.getCellEditor().stopCellEditing()) {
                throw new IllegalStateException("Finish editing the current cell first.");
            }
        }

        String title     = asStr(0,0);
        String desc      = asStr(0,1);
        int capacity     = asInt(0,2);
        String room      = asStr(0,3);
        String time      = asStr(0,4);
        String semester  = asStr(0,5);
        boolean open     = asBool(0,6);
        String syllabus  = asStr(0,7);

        if (title.isEmpty())   throw new IllegalArgumentException("Title is required.");
        if (capacity <= 0)     throw new IllegalArgumentException("Capacity must be > 0.");

        // write back to the same CourseDTO instance (persists for app lifetime)
        course.title = title;
        course.description = desc;
        course.capacity = capacity;
        course.room = room;
        course.time = time;
        course.semester = semester;
        course.enrollmentOpen = open;
        course.syllabusPath = (syllabus == null || syllabus.isBlank()) ? null : syllabus;

        saved = true;
        dispose();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
    }
});

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        JPanel south = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        south.add(new JLabel("Course ID: " + course.id + "    "));
        south.add(btnChooseSyllabus);
        south.add(btnCancel);
        south.add(btnSave);

        getContentPane().setLayout(new java.awt.BorderLayout(8,8));
        getContentPane().add(sp, java.awt.BorderLayout.CENTER);
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);
        setPreferredSize(new java.awt.Dimension(900, 260));
        pack();
    }

    private String asStr(int r, int c) { Object v = model.getValueAt(r,c); return v==null? "": v.toString().trim(); }
    private int asInt(int r, int c) {
        Object v = model.getValueAt(r,c);
        if (v instanceof Integer) return (Integer) v;
        return Integer.parseInt(v.toString().trim());
    }
    private boolean asBool(int r, int c) {
        Object v = model.getValueAt(r,c);
        if (v instanceof Boolean) return (Boolean) v;
        String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        return s.equals("true") || s.equals("yes") || s.equals("y");
    }
    boolean isSaved() { return saved; }
}
    
private void setEnrollment(boolean open) {
    int row = tblCourses.getSelectedRow();
    if(row<0){ JOptionPane.showMessageDialog(this,"Select a course row first."); return; }
    int modelRow = tblCourses.convertRowIndexToModel(row);
    String id = String.valueOf(courseModel.getValueAt(modelRow,0));
    data.setEnrollment(id, open);
    JOptionPane.showMessageDialog(this, (open? "Enrollment opened for ":"Enrollment closed for ")+id);
    refreshCoursesTable();
}

private void initCourseTableModel() { /* not needed anymore */ }

private void loadCourseCombo() {
    cboCourses.removeAllItems();
    for(CourseDTO c: data.getMyCourses()){
        cboCourses.addItem(c.id + " - " + c.title);
    }
    if(cboCourses.getItemCount()>0) cboCourses.setSelectedIndex(0);
    selectCourseFromCombo();
}

private void refreshCoursesTable() {
    courseModel.setRowCount(0);
    for(CourseDTO c: data.getMyCourses()){
        courseModel.addRow(new Object[]{
                c.id, c.title, c.description, c.capacity, c.room, c.time, c.semester,
                c.enrollmentOpen? "Yes":"No", c.syllabusPath==null? "": c.syllabusPath
        });
    }
}

private void saveDetails() {
    int row = tblCourses.getSelectedRow();
    if(row<0){ JOptionPane.showMessageDialog(this,"Select a course row to update."); return; }
    int modelRow = tblCourses.convertRowIndexToModel(row);
    String id = String.valueOf(courseModel.getValueAt(modelRow,0));
    CourseDTO c = data.findCourse(id);
    if(c==null){ JOptionPane.showMessageDialog(this,"Course not found."); return; }
    try{
        c.title = String.valueOf(courseModel.getValueAt(modelRow,1));
        c.description = String.valueOf(courseModel.getValueAt(modelRow,2));
        c.capacity = Integer.parseInt(String.valueOf(courseModel.getValueAt(modelRow,3)));
        c.room = String.valueOf(courseModel.getValueAt(modelRow,4));
        c.time = String.valueOf(courseModel.getValueAt(modelRow,5));
        c.semester = String.valueOf(courseModel.getValueAt(modelRow,6));
        data.updateCourse(c);
        JOptionPane.showMessageDialog(this,"Course details updated.");
    }catch(Exception ex){
        JOptionPane.showMessageDialog(this,"Invalid input: "+ex.getMessage());
    }
    refreshCoursesTable();
}

private void saveSyllabus() {
    int row = tblCourses.getSelectedRow();
    if(row<0){ JOptionPane.showMessageDialog(this,"Select a course row first."); return; }
    int modelRow = tblCourses.convertRowIndexToModel(row);
    String id = String.valueOf(courseModel.getValueAt(modelRow,0));

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose syllabus file (PDF/Doc)");
    chooser.setFileFilter(new FileNameExtensionFilter("Documents","pdf","doc","docx","txt"));
    int res = chooser.showOpenDialog(this);
    if(res==JFileChooser.APPROVE_OPTION){
        data.saveSyllabus(id, chooser.getSelectedFile().getAbsolutePath());
        JOptionPane.showMessageDialog(this,"Syllabus path saved.");
        refreshCoursesTable();
    }
}

private CourseDTO findCourse(String id) { return data.findCourse(id); }

private void loadCourseIntoFields(Course c) { /* not used; safe to keep empty */ }

private void selectCourseFromCombo() {
    String sel = (String) cboCourses.getSelectedItem();
    studentModel.setRowCount(0);
    if(sel==null || sel.trim().isEmpty()) return;
    String cid = sel.contains(" - ") ? sel.substring(0, sel.indexOf(" - ")) : sel;

    List<EnrollmentDTO> es = data.enrollmentsForCourse(cid);
    for(EnrollmentDTO e: es){
        StudentDTO s = data.findStudent(e.studentId);
        if(s==null) continue;
        double[] agg = aggregateGrade(cid, s.id);
        double pct = agg[1]==0 ? 0 : (agg[0]/agg[1])*100.0;
        String letter = toLetter(pct);
        double gpa = toGpa(letter);
        studentModel.addRow(new Object[]{
                s.id, s.name, s.email,
                String.format(java.util.Locale.US,"%.2f", pct),
                letter,
                String.format(java.util.Locale.US,"%.2f", gpa)
        });
    }
}

private double[] aggregateGrade(String courseId, String studentId){
    double earned=0, max=0;
    for(GradeItemDTO g: data.getGrades()){
        if(g.courseId.equals(courseId) && g.studentId.equals(studentId)){
            earned += g.pointsEarned; max += g.pointsMax;
        }
    }
    return new double[]{earned, max};
}


    private static class Course {

        public Course() {
        }
    }
    
    
    
}
