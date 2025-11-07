/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.AdminRole;

import Business.Business;
import Business.Course.CourseOffering;
import Business.Finance.Payment;
import Business.Profiles.StudentProfile;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pranjalpatil
 */
public class DashboardPanel extends javax.swing.JPanel {

    /**
     * Creates new form DashboardPanel
     */
    private Business business;
    private JPanel cardSequencePanel;
    private NumberFormat currencyFormat;

    public DashboardPanel(Business business, JPanel cardSequencePanel) {
        this.business = business;
        this.cardSequencePanel = cardSequencePanel;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        initComponents();

        setupUI();
        loadAnalytics();
    }

    private void setupUI() {
        fieldTotalUsers.setEditable(false);
        fieldTotalCourses.setEditable(false);
        fieldTotalStuds.setEditable(false);

        fieldTotalUsers.setBackground(Color.WHITE);
        fieldTotalCourses.setBackground(Color.WHITE);
        fieldTotalStuds.setBackground(Color.WHITE);

        fieldTotalUsers.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 24));
        fieldTotalCourses.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 24));
        fieldTotalStuds.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 24));

        fieldTotalUsers.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldTotalCourses.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldTotalStuds.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    }

    private void loadAnalytics() {
        loadSummaryCards();
        loadUsersByRoleTable();
        loadCoursesBySemesterTable();
        loadEnrollmentByCourseTable();
        loadFinancialSummaryTable();
    }

    private void loadSummaryCards() {
        int totalUsers = business.getUserAccountDirectory().getUserAccountList().size();
        fieldTotalUsers.setText(String.valueOf(totalUsers));

        int totalCourses = business.getCourseDirectory().getOfferings().size();
        fieldTotalCourses.setText(String.valueOf(totalCourses));

        int totalStudents = business.getStudentDirectory().getStudentList().size();
        fieldTotalStuds.setText(String.valueOf(totalStudents));
    }

    private void loadUsersByRoleTable() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setRowCount(0);

        int totalStudents = business.getTotalUsersByRole("Student");
        int totalFaculty = business.getTotalUsersByRole("Faculty");
        int totalAdmin = business.getTotalUsersByRole("Admin");
        int totalRegistrar = business.getTotalUsersByRole("Registrar");

        int grandTotal = totalStudents + totalFaculty + totalAdmin + totalRegistrar;

        if (grandTotal > 0) {
            Object[] studentRow = {
                "Students",
                totalStudents,
                String.format("%.1f%%", (totalStudents * 100.0 / grandTotal)),
                totalStudents
            };
            model.addRow(studentRow);

            Object[] facultyRow = {
                "Faculty",
                totalFaculty,
                String.format("%.1f%%", (totalFaculty * 100.0 / grandTotal)),
                totalFaculty
            };
            model.addRow(facultyRow);

            Object[] adminRow = {
                "Admin",
                totalAdmin,
                String.format("%.1f%%", (totalAdmin * 100.0 / grandTotal)),
                totalAdmin
            };
            model.addRow(adminRow);

            Object[] totalRow = {
                "Total",
                grandTotal,
                "100%",
                grandTotal
            };
            model.addRow(totalRow);
        }

        tblUsers.setDefaultEditor(Object.class, null);
    }

    private void loadCoursesBySemesterTable() {
        DefaultTableModel model = (DefaultTableModel) tblSem.getModel();
        model.setRowCount(0);

        Map<String, Integer> semesterCourses = new HashMap<>();
        Map<String, Integer> semesterEnrollments = new HashMap<>();

        for (CourseOffering offering : business.getCourseDirectory().getOfferings()) {
            String semester = offering.getSemester();

            semesterCourses.put(semester, semesterCourses.getOrDefault(semester, 0) + 1);

            int enrollmentCount = offering.getActiveEnrollmentCount();
            semesterEnrollments.put(semester, semesterEnrollments.getOrDefault(semester, 0) + enrollmentCount);
        }

        int totalCourses = 0;
        int totalEnrollments = 0;

        String[] semesters = {"Fall 2024", "Spring 2025", "Fall 2025"};

        for (String semester : semesters) {
            int courses = semesterCourses.getOrDefault(semester, 0);
            int enrollments = semesterEnrollments.getOrDefault(semester, 0);
            double avgPerCourse = courses > 0 ? (enrollments * 1.0 / courses) : 0.0;

            Object[] row = {
                semester,
                courses,
                enrollments,
                String.format("%.1f", avgPerCourse)
            };
            model.addRow(row);

            totalCourses += courses;
            totalEnrollments += enrollments;
        }

        double overallAvg = totalCourses > 0 ? (totalEnrollments * 1.0 / totalCourses) : 0.0;

        Object[] totalRow = {
            "Total",
            totalCourses,
            totalEnrollments,
            String.format("%.1f", overallAvg)
        };
        model.addRow(totalRow);

        tblSem.setDefaultEditor(Object.class, null);
    }

    private void loadEnrollmentByCourseTable() {
        DefaultTableModel model = (DefaultTableModel) tblCourse.getModel();
        model.setRowCount(0);

        for (CourseOffering offering : business.getCourseDirectory().getOfferings()) {
            if (offering.getSemester().equals("Fall 2025")) {
                int enrolled = offering.getActiveEnrollmentCount();
                int capacity = offering.getCapacity();
                double percentage = capacity > 0 ? (enrolled * 100.0 / capacity) : 0.0;

                Object[] row = {
                    offering.getCourse().getCourseId(),
                    offering.getCourse().getCourseName(),
                    capacity,
                    enrolled,
                    String.format("%.1f%%", percentage)
                };
                model.addRow(row);
            }
        }

        tblCourse.setDefaultEditor(Object.class, null);
    }

    private void loadFinancialSummaryTable() {
        DefaultTableModel model = (DefaultTableModel) tblFinSum.getModel();
        model.setRowCount(0);

        double totalBilled = 0.0;
        double totalPaid = 0.0;
        double totalRefunded = 0.0;

        for (StudentProfile student : business.getStudentDirectory().getStudentList()) {
            for (Payment payment : student.getAccount().getPaymentHistory()) {
                if (payment.getType().equalsIgnoreCase("Tuition")
                        && payment.getStatus().equalsIgnoreCase("Billed")) {
                    totalBilled += payment.getAmount();
                }

                if (payment.getType().equalsIgnoreCase("Payment")
                        && payment.getStatus().equalsIgnoreCase("Paid")) {
                    totalPaid += payment.getAmount();
                }

                if (payment.getType().equalsIgnoreCase("Refund")
                        && payment.getStatus().equalsIgnoreCase("Refunded")) {
                    totalRefunded += payment.getAmount();
                }
            }
        }

        double outstanding = business.calculateOutstandingBalance();

        double billedPercent = 100.0;
        double paidPercent = totalBilled > 0 ? (totalPaid * 100.0 / totalBilled) : 0.0;
        double outstandingPercent = totalBilled > 0 ? (outstanding * 100.0 / totalBilled) : 0.0;
        double refundedPercent = totalBilled > 0 ? (totalRefunded * 100.0 / totalBilled) : 0.0;

        Object[] billedRow = {
            "Total Tuition Billed",
            currencyFormat.format(totalBilled),
            String.format("%.1f%%", billedPercent)
        };
        model.addRow(billedRow);

        Object[] paidRow = {
            "Payments Received",
            currencyFormat.format(totalPaid),
            String.format("%.1f%%", paidPercent)
        };
        model.addRow(paidRow);

        Object[] outstandingRow = {
            "Outstanding Balance",
            currencyFormat.format(outstanding),
            String.format("%.1f%%", outstandingPercent)
        };
        model.addRow(outstandingRow);

        Object[] refundedRow = {
            "Refunds Issued",
            currencyFormat.format(totalRefunded),
            String.format("%.1f%%", refundedPercent)
        };
        model.addRow(refundedRow);

        tblFinSum.setDefaultEditor(Object.class, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        fieldTotalUsers = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        fieldTotalCourses = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        fieldTotalStuds = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCourse = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblFinSum = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSem = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 255, 255));
        jLabel1.setText("University Analytics Dashboard");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(74, 74, 74))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 51));
        jLabel2.setText("Total Users");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(fieldTotalUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(29, 29, 29))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(fieldTotalUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 204, 204));
        jPanel3.setPreferredSize(new java.awt.Dimension(147, 103));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 51));
        jLabel5.setText("Total Courses");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(fieldTotalCourses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel5)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(fieldTotalCourses, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(0, 204, 204));
        jPanel4.setPreferredSize(new java.awt.Dimension(147, 103));

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 51));
        jLabel6.setText("Total Students");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(fieldTotalStuds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(17, 17, 17))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(fieldTotalStuds, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setText("Enrollment by Course (Fall 2025)");

        tblCourse.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Capacity", "Enrolled", "%"
            }
        ));
        jScrollPane1.setViewportView(tblCourse);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setText("Users By Role");

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Role", "Count", "Percentage", "Active Users"
            }
        ));
        jScrollPane3.setViewportView(tblUsers);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(255, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setText("Financial Summary");

        tblFinSum.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Category", "Amount", "Percentage"
            }
        ));
        jScrollPane4.setViewportView(tblFinSum);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(229, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setText("Courses By Semester");

        tblSem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Semester", "Courses", "Enrollments", "Avg per Course"
            }
        ));
        jScrollPane5.setViewportView(tblSem);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(212, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton2.setText("Refresh Data");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Export Report");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(50, 50, 50)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(132, 132, 132)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(129, 129, 129)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(jButton2)
                        .addGap(194, 194, 194)
                        .addComponent(jButton3)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(25, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AdminWorkAreaPanel dashboard = new AdminWorkAreaPanel(business, cardSequencePanel);
        cardSequencePanel.add(dashboard, "AdminDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "AdminDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        loadAnalytics();

        JOptionPane.showMessageDialog(this,
                "Analytics data refreshed successfully!",
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        StringBuilder report = new StringBuilder();
        report.append("NORTHEASTERN UNIVERSITY\n");
        report.append("ANALYTICS REPORT\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n\n");
        report.append("═══════════════════════════════════════════════════\n\n");

        report.append("SUMMARY:\n");
        report.append("Total Users: ").append(fieldTotalUsers.getText()).append("\n");
        report.append("Total Courses: ").append(fieldTotalCourses.getText()).append("\n");
        report.append("Total Students: ").append(fieldTotalStuds.getText()).append("\n\n");

        report.append("USERS BY ROLE:\n");
        DefaultTableModel usersModel = (DefaultTableModel) tblUsers.getModel();
        for (int i = 0; i < usersModel.getRowCount(); i++) {
            report.append(String.format("  %s: %s (%s)\n",
                    usersModel.getValueAt(i, 0),
                    usersModel.getValueAt(i, 1),
                    usersModel.getValueAt(i, 2)));
        }

        report.append("\nCOURSES BY SEMESTER:\n");
        DefaultTableModel semModel = (DefaultTableModel) tblSem.getModel();
        for (int i = 0; i < semModel.getRowCount(); i++) {
            report.append(String.format("  %s: %s courses, %s enrollments\n",
                    semModel.getValueAt(i, 0),
                    semModel.getValueAt(i, 1),
                    semModel.getValueAt(i, 2)));
        }

        report.append("\nFINANCIAL SUMMARY:\n");
        DefaultTableModel finModel = (DefaultTableModel) tblFinSum.getModel();
        for (int i = 0; i < finModel.getRowCount(); i++) {
            report.append(String.format("  %s: %s (%s)\n",
                    finModel.getValueAt(i, 0),
                    finModel.getValueAt(i, 1),
                    finModel.getValueAt(i, 2)));
        }

        report.append("\n═══════════════════════════════════════════════════\n");

        JOptionPane.showMessageDialog(this,
                new javax.swing.JScrollPane(new javax.swing.JTextArea(report.toString(), 25, 60)),
                "Analytics Report Preview",
                JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fieldTotalCourses;
    private javax.swing.JTextField fieldTotalStuds;
    private javax.swing.JTextField fieldTotalUsers;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable tblCourse;
    private javax.swing.JTable tblFinSum;
    private javax.swing.JTable tblSem;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
