/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.StudentRole;

import Business.Business;
import Business.Profiles.StudentProfile;
import Business.Transcripts.Transcript;
import Business.Transcripts.TranscriptEntry;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chinm
 */
public class TranscriptReviewJPanel extends javax.swing.JPanel {

    private Business business;
    private StudentProfile studentProfile;
    private JPanel cardSequencePanel;
    private DecimalFormat gpaFormat;
    private String selectedSemester;

    public TranscriptReviewJPanel(Business business, StudentProfile studentProfile, JPanel cardSequencePanel) {
        this.business = business;
        this.studentProfile = studentProfile;
        this.cardSequencePanel = cardSequencePanel;
        this.gpaFormat = new DecimalFormat("0.00");

        initComponents();

        if (!checkTuitionPayment()) {
            return;
        }

        setupUI();
        loadStudentInfo();
        loadSemesters();
        loadTranscriptData();
        updateGPASummary();
        updateGraduationStatus();
    }

    private boolean checkTuitionPayment() {
        double balance = studentProfile.getAccount().getOutstandingBalance();

        if (balance > 0) {
            disableAllComponents();

            JOptionPane.showMessageDialog(this,
                    "You cannot view your transcript until all tuition is paid.\n\n"
                    + "Outstanding Balance: $" + String.format("%.2f", balance) + "\n\n"
                    + "Please go to 'Pay Tuition' to make a payment.",
                    "Transcript Access Denied",
                    JOptionPane.ERROR_MESSAGE);

            navigateBack();
            return false;
        }

        return true;
    }

    private void disableAllComponents() {
        cmbBoxSemester.setEnabled(false);
        tblTranscripts.setEnabled(false);
        progressBarStatus.setEnabled(false);
    }

    private void setupUI() {
        fieldName.setEditable(false);
        fieldStudentID.setEditable(false);
        fieldTermGPA.setEditable(false);
        fiedOverallGPA.setEditable(false);
        fieldOverallCredits.setEditable(false);
        fieldAcademicStanding.setEditable(false);
        fieldCreditRequired.setEditable(false);
        fieldCreditsCompleted.setEditable(false);
        fieldRemaingCredits.setEditable(false);

        chckBoxCompleted.setEnabled(false);

        fieldCreditRequired.setText("32");

        progressBarStatus.setMinimum(0);
        progressBarStatus.setMaximum(32);
        progressBarStatus.setStringPainted(true);
    }

    private void loadStudentInfo() {
        fieldName.setText(studentProfile.getPerson().getFullName());
        fieldStudentID.setText(studentProfile.getPerson().getPersonId());
    }

    private void loadSemesters() {
        cmbBoxSemester.removeAllItems();

        cmbBoxSemester.addItem("All Semesters");

        ArrayList<String> terms = studentProfile.getTranscript().getAllTerms();

        for (String term : terms) {
            cmbBoxSemester.addItem(term);
        }

        if (cmbBoxSemester.getItemCount() > 0) {
            cmbBoxSemester.setSelectedIndex(0);
            selectedSemester = "All Semesters";
        }
    }

    private void loadTranscriptData() {
        DefaultTableModel model = (DefaultTableModel) tblTranscripts.getModel();
        model.setRowCount(0);

        Transcript transcript = studentProfile.getTranscript();
        ArrayList<TranscriptEntry> entries;

        if (selectedSemester != null && !selectedSemester.equals("All Semesters")) {
            entries = transcript.getEntriesByTerm(selectedSemester);
        } else {
            entries = transcript.getEntries();
        }

        for (TranscriptEntry entry : entries) {
            String term = entry.getTerm();
            String standing = transcript.determineStanding(term);
            String courseId = entry.getOffering().getCourse().getCourseId();
            String courseName = entry.getOffering().getCourse().getCourseName();
            String grade = entry.getGrade() != null ? entry.getGrade() : "In Progress";
            int credits = entry.getCreditHours();

            Object[] row = new Object[6];
            row[0] = term;
            row[1] = standing;
            row[2] = courseId;
            row[3] = courseName;
            row[4] = grade;
            row[5] = credits;

            model.addRow(row);
        }

        tblTranscripts.setDefaultEditor(Object.class, null);

        if (entries.isEmpty()) {
            if (selectedSemester != null && !selectedSemester.equals("All Semesters")) {
                JOptionPane.showMessageDialog(this,
                        "No courses found for " + selectedSemester,
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void updateGPASummary() {
        Transcript transcript = studentProfile.getTranscript();

        if (selectedSemester != null && !selectedSemester.equals("All Semesters")) {
            double termGPA = transcript.calculateTermGPA(selectedSemester);
            fieldTermGPA.setText(gpaFormat.format(termGPA));
            colorCodeGPA(fieldTermGPA, termGPA);
        } else {
            fieldTermGPA.setText("N/A");
            fieldTermGPA.setForeground(Color.BLACK);
        }

        double overallGPA = transcript.calculateOverallGPA();
        fiedOverallGPA.setText(gpaFormat.format(overallGPA));
        colorCodeGPA(fiedOverallGPA, overallGPA);

        int totalCredits = transcript.getTotalCredits();
        fieldOverallCredits.setText(totalCredits + " / 32");

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
            default:
                fieldAcademicStanding.setForeground(Color.BLACK);
                break;
        }
    }

    private void colorCodeGPA(javax.swing.JTextField field, double gpa) {
        if (gpa >= 3.7) {
            field.setForeground(new Color(34, 139, 34));
        } else if (gpa >= 3.0) {
            field.setForeground(new Color(40, 167, 69));
        } else if (gpa >= 2.7) {
            field.setForeground(new Color(253, 126, 20));
        } else {
            field.setForeground(new Color(220, 53, 69));
        }
    }

    private void updateGraduationStatus() {
        Transcript transcript = studentProfile.getTranscript();

        boolean coreCompleted = transcript.hasCompletedCourse("INFO5100");
        chckBoxCompleted.setSelected(coreCompleted);

        int creditsCompleted = transcript.getTotalCredits();
        fieldCreditsCompleted.setText(String.valueOf(creditsCompleted));

        int creditsRemaining = Math.max(0, 32 - creditsCompleted);
        fieldRemaingCredits.setText(String.valueOf(creditsRemaining));

        progressBarStatus.setValue(creditsCompleted);
        progressBarStatus.setString(creditsCompleted + " / 32 credits ("
                + String.format("%.0f%%", (creditsCompleted * 100.0 / 32)) + ")");

        if (creditsCompleted >= 32) {
            progressBarStatus.setForeground(new Color(144, 238, 144));
        } else if (creditsCompleted >= 20) {
            progressBarStatus.setForeground(new Color(173, 216, 230));
        } else if (creditsCompleted >= 10) {
            progressBarStatus.setForeground(new Color(255, 218, 185));
        } else {
            progressBarStatus.setForeground(new Color(255, 182, 193));
        }

        boolean readyToGraduate = studentProfile.isReadyToGraduate();

        if (readyToGraduate) {
            fieldRemaingCredits.setText("✓ Yes");
            fieldRemaingCredits.setForeground(new Color(40, 167, 69));
            fieldRemaingCredits.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
        } else {
            fieldRemaingCredits.setText("✗ No (" + creditsRemaining + " credits remaining)");
            fieldRemaingCredits.setForeground(new Color(220, 53, 69));
            fieldRemaingCredits.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fieldName = new javax.swing.JTextField();
        fieldStudentID = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbBoxSemester = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTranscripts = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fieldTermGPA = new javax.swing.JTextField();
        fieldOverallGPA = new javax.swing.JLabel();
        fiedOverallGPA = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        fieldOverallCredits = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        fieldAcademicStanding = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        chckBoxCompleted = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        progressBarStatus = new javax.swing.JProgressBar();
        jLabel12 = new javax.swing.JLabel();
        fieldCreditRequired = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        fieldCreditsCompleted = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        fieldRemaingCredits = new javax.swing.JTextField();

        setBackground(new java.awt.Color(215, 204, 200));

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Transcript Review");

        jLabel2.setText("Student Name:");

        jLabel3.setText("Filter by Semester");

        cmbBoxSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxSemesterActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("GPA Summary");

        tblTranscripts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Term", "Standing", "CourseID", "Course Name", "Grade", "Credits"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTranscripts);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Academic Transcripts");

        jLabel6.setText("Term GPA ");

        fieldOverallGPA.setText("Overall GPA");

        jLabel8.setText("Academic Standing");

        jLabel9.setText("Overall Credits Completed");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Graduation Status");

        jLabel10.setText("Core Course Status (INFO 5100)");

        chckBoxCompleted.setText("Completed");
        chckBoxCompleted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chckBoxCompletedActionPerformed(evt);
            }
        });

        jLabel11.setText("Progress Status");

        jLabel12.setText("Credits Required");

        jLabel13.setText("Credits Completed");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Ready To Graduate?");

        fieldRemaingCredits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldRemaingCreditsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(btnBack)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(53, 53, 53))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fieldName)
                            .addComponent(cmbBoxSemester, 0, 200, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(fieldStudentID, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldTermGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(236, 236, 236)
                                .addComponent(fieldOverallCredits))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(chckBoxCompleted, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(fieldOverallGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(fiedOverallGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(fieldAcademicStanding))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(progressBarStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(27, 27, 27))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fieldCreditRequired, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(fieldCreditsCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(fieldRemaingCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(53, 53, 53)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(433, 433, 433)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(btnBack))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldStudentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbBoxSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fieldTermGPA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fieldOverallCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldOverallGPA)
                            .addComponent(fiedOverallGPA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldAcademicStanding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(chckBoxCompleted)
                        .addComponent(jLabel11))
                    .addComponent(progressBarStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(fieldCreditRequired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(fieldCreditsCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(fieldRemaingCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(153, 153, 153)
                    .addComponent(jLabel5)
                    .addContainerGap(427, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chckBoxCompletedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chckBoxCompletedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chckBoxCompletedActionPerformed

    private void fieldRemaingCreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldRemaingCreditsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldRemaingCreditsActionPerformed

    private void cmbBoxSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxSemesterActionPerformed
        selectedSemester = (String) cmbBoxSemester.getSelectedItem();
        loadTranscriptData();
        updateGPASummary();
    }//GEN-LAST:event_cmbBoxSemesterActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        navigateBack();
    }//GEN-LAST:event_btnBackActionPerformed

    private void navigateBack() {
        StudentWorkAreaJPanel dashboard = new StudentWorkAreaJPanel(business, studentProfile, cardSequencePanel);
        cardSequencePanel.add(dashboard, "StudentDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "StudentDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JCheckBox chckBoxCompleted;
    private javax.swing.JComboBox<String> cmbBoxSemester;
    private javax.swing.JTextField fiedOverallGPA;
    private javax.swing.JTextField fieldAcademicStanding;
    private javax.swing.JTextField fieldCreditRequired;
    private javax.swing.JTextField fieldCreditsCompleted;
    private javax.swing.JTextField fieldName;
    private javax.swing.JTextField fieldOverallCredits;
    private javax.swing.JLabel fieldOverallGPA;
    private javax.swing.JTextField fieldRemaingCredits;
    private javax.swing.JTextField fieldStudentID;
    private javax.swing.JTextField fieldTermGPA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBarStatus;
    private javax.swing.JTable tblTranscripts;
    // End of variables declaration//GEN-END:variables
}
