/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.StudentRole;

import Business.Business;
import Business.Person.Person;
import Business.Profiles.StudentProfile;
import Business.Transcripts.Transcript;
import Business.Transcripts.TranscriptEntry;
import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pranjalpatil
 */
public class GraduationAuditJPanel extends javax.swing.JPanel {

    /**
     * Creates new form GraduationAuditPanel
     */
    private Business business;
    private StudentProfile studentProfile;
    private JPanel cardSequencePanel;
    private DecimalFormat gpaFormat;

    public GraduationAuditJPanel(Business business, StudentProfile studentProfile, JPanel cardSequencePanel) {
        this.business = business;
        this.studentProfile = studentProfile;
        this.cardSequencePanel = cardSequencePanel;
        this.gpaFormat = new DecimalFormat("0.00");

        initComponents();

        setupUI();
        loadGraduationAudit();
    }

    private void setupUI() {
        fieldStudent.setEditable(false);
        fieldDegreeReq.setEditable(false);
        fieldReqCred.setEditable(false);
        fieldComplCreds.setEditable(false);
        fieldRemCreds.setEditable(false);
        fieldGrade.setEditable(false);
        fieldSemester.setEditable(false);
        fieldTotalElective.setEditable(false);
        fieldOverallGPA.setEditable(false);
        fieldAcademicStanding.setEditable(false);
        fieldRequirementsNotMet.setEditable(false);
        fieldProjectedGraduation.setEditable(false);

        fieldStudent.setBackground(new Color(220, 220, 220));
        fieldDegreeReq.setBackground(new Color(220, 220, 220));
        fieldReqCred.setBackground(new Color(220, 220, 220));
        fieldComplCreds.setBackground(new Color(220, 220, 220));
        fieldRemCreds.setBackground(new Color(220, 220, 220));
        fieldGrade.setBackground(new Color(220, 220, 220));
        fieldSemester.setBackground(new Color(220, 220, 220));
        fieldTotalElective.setBackground(new Color(220, 220, 220));
        fieldOverallGPA.setBackground(new Color(220, 220, 220));
        fieldAcademicStanding.setBackground(new Color(220, 220, 220));
        fieldRequirementsNotMet.setBackground(new Color(220, 220, 220));
        fieldProjectedGraduation.setBackground(new Color(220, 220, 220));

        comboBoxReadyToGraduate.removeAllItems();
        comboBoxReadyToGraduate.addItem("Yes");
        comboBoxReadyToGraduate.addItem("No");
        comboBoxReadyToGraduate.setEnabled(false);

        progressBarDegreeReq.setMinimum(0);
        progressBarDegreeReq.setMaximum(32);
        progressBarDegreeReq.setStringPainted(true);
    }

    private void loadGraduationAudit() {
        Person person = studentProfile.getPerson();
        Transcript transcript = studentProfile.getTranscript();

        fieldStudent.setText(person.getFullName() + " (" + person.getPersonId() + ")");

        fieldDegreeReq.setText("MSIS - Master of Science in Information Systems");

        fieldReqCred.setText("32");

        int creditsCompleted = transcript.getTotalCredits();
        fieldComplCreds.setText(String.valueOf(creditsCompleted));

        int creditsRemaining = Math.max(0, 32 - creditsCompleted);
        fieldRemCreds.setText(String.valueOf(creditsRemaining));

        progressBarDegreeReq.setValue(creditsCompleted);
        progressBarDegreeReq.setString(creditsCompleted + " / 32 credits ("
                + String.format("%.0f%%", (creditsCompleted * 100.0 / 32)) + ")");

        if (creditsCompleted >= 32) {
            progressBarDegreeReq.setForeground(new Color(144, 238, 144));
        } else if (creditsCompleted >= 20) {
            progressBarDegreeReq.setForeground(new Color(173, 216, 230));
        } else if (creditsCompleted >= 10) {
            progressBarDegreeReq.setForeground(new Color(255, 218, 185));
        } else {
            progressBarDegreeReq.setForeground(new Color(255, 182, 193));
        }

        boolean coreCompleted = transcript.hasCompletedCourse("INFO5100");
        TranscriptEntry coreEntry = null;

        for (TranscriptEntry entry : transcript.getEntries()) {
            if (entry.getOffering().getCourse().getCourseId().equals("INFO5100")) {
                coreEntry = entry;
                break;
            }
        }

        if (coreCompleted && coreEntry != null) {
            fieldGrade.setText(coreEntry.getGrade());
            fieldSemester.setText(coreEntry.getTerm());
            fieldGrade.setForeground(new Color(40, 167, 69));
        } else {
            fieldGrade.setText("Not Completed");
            fieldSemester.setText("N/A");
            fieldGrade.setForeground(new Color(220, 53, 69));
        }

        loadElectiveCourses();

        double overallGPA = transcript.calculateOverallGPA();
        fieldOverallGPA.setText(gpaFormat.format(overallGPA));

        if (overallGPA >= 3.7) {
            fieldOverallGPA.setForeground(new Color(34, 139, 34));
        } else if (overallGPA >= 3.0) {
            fieldOverallGPA.setForeground(new Color(40, 167, 69));
        } else if (overallGPA >= 2.7) {
            fieldOverallGPA.setForeground(new Color(253, 126, 20));
        } else {
            fieldOverallGPA.setForeground(new Color(220, 53, 69));
        }

        String standing = transcript.determineStanding();
        fieldAcademicStanding.setText(standing);

        switch (standing) {
            case "Good Standing":
                fieldAcademicStanding.setForeground(new Color(40, 167, 69));
                break;
            case "Academic Warning":
                fieldAcademicStanding.setForeground(new Color(253, 126, 20));
                break;
            case "Academic Probation":
                fieldAcademicStanding.setForeground(new Color(220, 53, 69));
                break;
        }

        boolean readyToGraduate = studentProfile.isReadyToGraduate();

        if (readyToGraduate) {
            comboBoxReadyToGraduate.setSelectedItem("Yes");
            comboBoxReadyToGraduate.setForeground(new Color(40, 167, 69));
            fieldRequirementsNotMet.setText("All requirements met!");
            fieldRequirementsNotMet.setForeground(new Color(40, 167, 69));
            fieldProjectedGraduation.setText("Eligible Now");
            fieldProjectedGraduation.setForeground(new Color(40, 167, 69));
        } else {
            comboBoxReadyToGraduate.setSelectedItem("No");
            comboBoxReadyToGraduate.setForeground(new Color(220, 53, 69));

            StringBuilder requirements = new StringBuilder();

            if (!coreCompleted) {
                requirements.append("• Core course INFO5100 not completed");
            }

            if (creditsRemaining > 0) {
                if (requirements.length() > 0) {
                    requirements.append(", ");
                }
                requirements.append("• Need ").append(creditsRemaining).append(" more credits");
            }

            fieldRequirementsNotMet.setText(requirements.toString());
            fieldRequirementsNotMet.setForeground(new Color(220, 53, 69));

            int semestersNeeded = (int) Math.ceil(creditsRemaining / 8.0);
            String projectedSemester = calculateProjectedGraduation(semestersNeeded);
            fieldProjectedGraduation.setText(projectedSemester);
            fieldProjectedGraduation.setForeground(new Color(0, 123, 255));
        }
    }

    private void loadElectiveCourses() {
        DefaultTableModel model = (DefaultTableModel) tblElectiveCourses.getModel();
        model.setRowCount(0);

        int totalElectiveCredits = 0;

        for (TranscriptEntry entry : studentProfile.getTranscript().getEntries()) {
            if (!entry.getOffering().getCourse().getCourseId().equals("INFO5100")) {
                Object[] row = new Object[4];
                row[0] = entry.getOffering().getCourse().getCourseId();
                row[1] = entry.getOffering().getCourse().getCourseName();
                row[2] = entry.getGrade();
                row[3] = entry.getCreditHours();

                model.addRow(row);
                totalElectiveCredits += entry.getCreditHours();
            }
        }

        fieldTotalElective.setText(totalElectiveCredits + " / 28");

        tblElectiveCourses.setDefaultEditor(Object.class, null);
    }

    private String calculateProjectedGraduation(int semestersNeeded) {
        if (semestersNeeded == 0) {
            return "Eligible Now";
        }

        int currentYear = 2025;
        String currentSemester = "Fall";

        for (int i = 0; i < semestersNeeded; i++) {
            if (currentSemester.equals("Fall")) {
                currentSemester = "Spring";
                currentYear++;
            } else {
                currentSemester = "Fall";
            }
        }

        return currentSemester + " " + currentYear;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnback = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        fieldStudent = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fieldDegreeReq = new javax.swing.JTextField();
        fieldReqCred = new javax.swing.JTextField();
        fieldComplCreds = new javax.swing.JTextField();
        fieldRemCreds = new javax.swing.JTextField();
        progressBarDegreeReq = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        fieldGrade = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        fieldSemester = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblElectiveCourses = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        fieldTotalElective = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        fieldOverallGPA = new javax.swing.JTextField();
        fieldAcademicStanding = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        comboBoxReadyToGraduate = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        fieldRequirementsNotMet = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        fieldProjectedGraduation = new javax.swing.JTextField();

        setBackground(new java.awt.Color(215, 204, 200));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Graduation Audit");

        btnback.setText("Back");
        btnback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbackActionPerformed(evt);
            }
        });

        jLabel2.setText("Student:");

        jLabel3.setText("DEGREE REQUIREMENTS ");

        jLabel4.setText("Required Credits:");

        jLabel5.setText("Completed Credits:");

        jLabel6.setText("Remainig Credits:");

        jLabel7.setText("Progress");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(56, 56, 56)))
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldReqCred, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fieldRemCreds, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(144, 144, 144)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progressBarDegreeReq, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fieldComplCreds, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(fieldDegreeReq, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fieldDegreeReq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fieldReqCred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(fieldComplCreds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(fieldRemCreds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(progressBarDegreeReq, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("CORE REQUIREMENTS");

        jLabel9.setText("Grade:");

        jLabel10.setText("Semester:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(105, 105, 105)))
                .addComponent(jLabel10)
                .addGap(87, 87, 87)
                .addComponent(fieldSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(129, 129, 129))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fieldSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addGap(30, 30, 30))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(fieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33))))
        );

        tblElectiveCourses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Grade", "Credits"
            }
        ));
        jScrollPane2.setViewportView(tblElectiveCourses);

        jLabel12.setText("ELECTIVE COURSES");

        jLabel11.setText("Total Elective Credits:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel12))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldTotalElective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldTotalElective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jLabel13.setText("ACADEMIC PERFORMANCE");

        jLabel14.setText("Overall GPA:");

        jLabel15.setText("Academic Standing:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(fieldOverallGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addGap(28, 28, 28)
                        .addComponent(fieldAcademicStanding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(157, 157, 157))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(fieldOverallGPA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(fieldAcademicStanding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setText(" GRADUATION STATUS   ");

        jLabel17.setText("Ready to Graduate:");

        comboBoxReadyToGraduate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Yes", "No" }));

        jLabel18.setText("Requirements Not Met:");

        jLabel19.setText("Projected Graduation:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxReadyToGraduate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fieldRequirementsNotMet, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(46, 46, 46)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fieldProjectedGraduation, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(comboBoxReadyToGraduate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(fieldRequirementsNotMet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(fieldProjectedGraduation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(fieldStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnback)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(fieldStudent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        StudentWorkAreaJPanel dashboard = new StudentWorkAreaJPanel(business, studentProfile, cardSequencePanel);
        cardSequencePanel.add(dashboard, "StudentDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "StudentDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }//GEN-LAST:event_btnbackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JComboBox<String> comboBoxReadyToGraduate;
    private javax.swing.JTextField fieldAcademicStanding;
    private javax.swing.JTextField fieldComplCreds;
    private javax.swing.JTextField fieldDegreeReq;
    private javax.swing.JTextField fieldGrade;
    private javax.swing.JTextField fieldOverallGPA;
    private javax.swing.JTextField fieldProjectedGraduation;
    private javax.swing.JTextField fieldRemCreds;
    private javax.swing.JTextField fieldReqCred;
    private javax.swing.JTextField fieldRequirementsNotMet;
    private javax.swing.JTextField fieldSemester;
    private javax.swing.JTextField fieldStudent;
    private javax.swing.JTextField fieldTotalElective;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JProgressBar progressBarDegreeReq;
    private javax.swing.JTable tblElectiveCourses;
    // End of variables declaration//GEN-END:variables
}
