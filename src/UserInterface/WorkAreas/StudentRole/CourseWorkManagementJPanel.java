/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.StudentRole;

import Business.Business;
import Business.Course.CourseOffering;
import Business.Course.Enrollment;
import Business.CourseWork.Assignment;
import Business.CourseWork.Submission;
import Business.Profiles.StudentProfile;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chinm
 */
public class CourseWorkManagementJPanel extends javax.swing.JPanel {

    /**
     * Creates new form CourseWorkManagementJPanel
     */
    private Business business;
    private StudentProfile studentProfile;
    private JPanel cardSequencePanel;
    private SimpleDateFormat dateTimeFormat;
    private SimpleDateFormat dateFormat;

    private CourseOffering selectedCourseOffering;
    private ArrayList<Assignment> currentAssignments;
    private Assignment selectedAssignment;

    public CourseWorkManagementJPanel(Business business, StudentProfile studentProfile, JPanel cardSequencePanel) {
        this.business = business;
        this.studentProfile = studentProfile;
        this.cardSequencePanel = cardSequencePanel;
        this.currentAssignments = new ArrayList<>();

        dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        initComponents();

        setupUI();

        loadEnrolledCourses();
    }

    private void setupUI() {
        fieldInstructor.setEditable(false);
        fieldSemester.setEditable(false);
        fieldCredits.setEditable(false);
        jTextField1.setEditable(false);
        fieldSubmittedOn.setEditable(false);
        fieldStatus.setEditable(false);
        fieldGrade.setEditable(false);
        fieldPercentage.setEditable(false);
        fieldFeedback.setEditable(false);

        clearSubmissionDetails();
    }

    private void loadEnrolledCourses() {
        cmbSelectCourse.removeAllItems();

        ArrayList<Enrollment> enrollments = studentProfile.getEnrollments();

        System.out.println("===== COURSE WORK MANAGEMENT DEBUG =====");
        System.out.println("Student: " + studentProfile.getPerson().getFullName());
        System.out.println("Total enrollments: " + enrollments.size());

        if (enrollments.isEmpty()) {
            System.out.println("ERROR: No enrollments found!");
            JOptionPane.showMessageDialog(this,
                    "You are not enrolled in any courses.",
                    "No Enrollments",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        System.out.println("\nAll Enrollments:");
        for (Enrollment e : enrollments) {
            System.out.println("  - Course: " + e.getOffering().getCourse().getCourseId());
            System.out.println("    Semester: " + e.getOffering().getSemester());
            System.out.println("    Status: " + e.getStatus());
            System.out.println("    Assignments: " + e.getOffering().getAssignments().size());
        }

        int fall2025Count = 0;
        for (Enrollment enrollment : enrollments) {
            System.out.println("\nChecking enrollment:");
            System.out.println("  Status: " + enrollment.getStatus());
            System.out.println("  Semester: '" + enrollment.getOffering().getSemester() + "'");
            System.out.println("  Equals 'Fall 2025'? " + enrollment.getOffering().getSemester().equals("Fall 2025"));

            if (!enrollment.getStatus().equalsIgnoreCase("Dropped")
                    && enrollment.getOffering().getSemester().equals("Fall 2025")) {

                CourseOffering offering = enrollment.getOffering();
                String displayText = offering.getCourse().getCourseId() + " - "
                        + offering.getCourse().getCourseName();
                cmbSelectCourse.addItem(displayText);
                fall2025Count++;

                System.out.println("  -> ADDED to dropdown: " + displayText);
                System.out.println("  -> Assignments count: " + offering.getAssignments().size());
            }
        }

        System.out.println("\nTotal Fall 2025 courses in dropdown: " + fall2025Count);
        System.out.println("========================================\n");

        if (fall2025Count == 0) {
            JOptionPane.showMessageDialog(this,
                    "You are not enrolled in any Fall 2025 courses.\n"
                    + "Please register for courses first.",
                    "No Fall 2025 Enrollments",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (cmbSelectCourse.getItemCount() > 0) {
            cmbSelectCourse.setSelectedIndex(0);
            loadCourseDetails();
        }
    }

    private void loadCourseDetails() {
        int selectedIndex = cmbSelectCourse.getSelectedIndex();

        if (selectedIndex < 0) {
            return;
        }

        ArrayList<Enrollment> enrollments = studentProfile.getEnrollments();
        int currentIndex = 0;

        for (Enrollment enrollment : enrollments) {
            if (!enrollment.getStatus().equalsIgnoreCase("Dropped")
                    && enrollment.getOffering().getSemester().equals("Fall 2025")) {

                if (currentIndex == selectedIndex) {
                    selectedCourseOffering = enrollment.getOffering();

                    fieldInstructor.setText(selectedCourseOffering.getFaculty().getPerson().getFullName());
                    fieldSemester.setText(selectedCourseOffering.getSemester());
                    fieldCredits.setText(String.valueOf(selectedCourseOffering.getCourse().getCreditHours()));

                    loadAssignments();
                    clearSubmissionDetails();

                    break;
                }
                currentIndex++;
            }
        }
    }

    private void loadAssignments() {
        if (selectedCourseOffering == null) {
            return;
        }

        currentAssignments = selectedCourseOffering.getAssignments();

        if (currentAssignments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No assignments have been created for this course yet.",
                    "No Assignments",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        populateAssignmentsTable();
    }

    private void populateAssignmentsTable() {
        DefaultTableModel model = (DefaultTableModel) tblAssignment.getModel();
        model.setRowCount(0);

        for (Assignment assignment : currentAssignments) {
            // Determine status based on ACTUAL submissions
            String status = determineAssignmentStatus(assignment);

            Object[] row = new Object[5];
            row[0] = assignment.getAssignmentId();
            row[1] = assignment.getTitle();
            row[2] = dateFormat.format(assignment.getDueDate());
            row[3] = assignment.getMaxPoints();
            row[4] = status;

            model.addRow(row);
        }

        // Make table non-editable
        tblAssignment.setDefaultEditor(Object.class, null);

        // Add row selection listener
        tblAssignment.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onAssignmentSelected();
            }
        });
    }

    private String determineAssignmentStatus(Assignment assignment) {

        Submission submission = assignment.findSubmissionByStudent(
                studentProfile.getPerson().getPersonId()
        );

        if (submission != null) {

            return submission.getStatus();
        } else {

            if (assignment.isOverdue()) {
                return "Overdue";
            } else {
                return "Open";
            }
        }
    }

    private void onAssignmentSelected() {
        int selectedRow = tblAssignment.getSelectedRow();

        if (selectedRow < 0) {
            clearSubmissionDetails();
            return;
        }

        selectedAssignment = currentAssignments.get(selectedRow);
        loadSubmissionDetails(selectedAssignment);
    }

    private void loadSubmissionDetails(Assignment assignment) {
        jTextField1.setText(assignment.getTitle());

        Submission submission = assignment.findSubmissionByStudent(
                studentProfile.getPerson().getPersonId()
        );

        if (submission == null) {

            fieldStatus.setText(assignment.isOverdue() ? "Overdue" : "Open");
            fieldStatus.setForeground(assignment.isOverdue()
                    ? new Color(220, 53, 69) : new Color(253, 126, 20));
            fieldSubmittedOn.setText("Not submitted");
            fieldGrade.setText("N/A");
            fieldPercentage.setText("N/A");
            fieldFeedback.setText("");
            return;
        }

        String status = submission.getStatus();
        fieldStatus.setText(status);

        switch (status) {
            case "Graded":
                fieldStatus.setForeground(new Color(40, 167, 69));
                break;
            case "Submitted":
                fieldStatus.setForeground(new Color(0, 123, 255));
                break;
            default:
                fieldStatus.setForeground(Color.BLACK);
                break;
        }

        fieldSubmittedOn.setText(dateTimeFormat.format(submission.getSubmissionDate()));

        if (status.equals("Graded")) {
            fieldGrade.setText(submission.getPointsEarned() + "/" + assignment.getMaxPoints());
            double percentage = submission.getPercentage();
            fieldPercentage.setText(String.format("%.1f%%", percentage));

            if (percentage >= 90) {
                fieldPercentage.setForeground(new Color(40, 167, 69));
            } else if (percentage >= 80) {
                fieldPercentage.setForeground(new Color(0, 123, 255));
            } else if (percentage >= 70) {
                fieldPercentage.setForeground(new Color(253, 126, 20));
            } else {
                fieldPercentage.setForeground(new Color(220, 53, 69));
            }

            fieldFeedback.setText(submission.getFeedback() != null
                    ? submission.getFeedback() : "No feedback provided.");
        } else {
            fieldGrade.setText("Pending");
            fieldPercentage.setText("N/A");
            fieldFeedback.setText("Your submission is being reviewed.");
        }
    }

    private void clearSubmissionDetails() {
        jTextField1.setText("");
        fieldSubmittedOn.setText("");
        fieldStatus.setText("");
        fieldGrade.setText("");
        fieldPercentage.setText("");
        fieldFeedback.setText("");
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
        jLabel3 = new javax.swing.JLabel();
        cmbSelectCourse = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        fieldInstructor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        fieldSemester = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        fieldCredits = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAssignment = new javax.swing.JTable();
        btnViewDetails = new javax.swing.JButton();
        btnSubmitAssignment = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        fieldStatus = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        fieldPercentage = new javax.swing.JTextField();
        fieldGrade = new javax.swing.JTextField();
        fieldSubmittedOn = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        fieldFeedback = new javax.swing.JTextField();

        setBackground(new java.awt.Color(215, 204, 200));

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Course Work Management");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("My Enrolled Courses:");

        jLabel3.setText("Select Course:");

        cmbSelectCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSelectCourseActionPerformed(evt);
            }
        });

        jLabel4.setText("Instructor:");

        jLabel5.setText("Semester:");

        jLabel6.setText("Credits:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Assignments:");

        tblAssignment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "AssignmentID", "Title", "Due Date", "Max Points", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAssignment);

        btnViewDetails.setText("View Details");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnSubmitAssignment.setText("Submit Assignment");
        btnSubmitAssignment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitAssignmentActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Submission Details:");

        jLabel9.setText("Assignment:");

        jLabel10.setText("Submitted On:");

        jLabel11.setText("Grade");

        jLabel12.setText("Status");

        jLabel13.setText("Percentage");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Feedback");

        fieldFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldFeedbackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBack)
                                .addGap(199, 199, 199)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(fieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(fieldSubmittedOn, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(30, 30, 30)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(fieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(fieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbSelectCourse, 0, 190, Short.MAX_VALUE)
                            .addComponent(fieldInstructor))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fieldSemester)
                            .addComponent(fieldCredits, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSubmitAssignment, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(fieldPercentage, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBack)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbSelectCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(fieldCredits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fieldInstructor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(fieldSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnViewDetails)
                    .addComponent(btnSubmitAssignment)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(fieldSubmittedOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(fieldPercentage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(fieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        StudentWorkAreaJPanel dashboard = new StudentWorkAreaJPanel(business, studentProfile, cardSequencePanel);
        cardSequencePanel.add(dashboard, "StudentDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "StudentDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }//GEN-LAST:event_btnBackActionPerformed

    private void fieldFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldFeedbackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldFeedbackActionPerformed

    private void cmbSelectCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSelectCourseActionPerformed
        loadCourseDetails();
    }//GEN-LAST:event_cmbSelectCourseActionPerformed

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        if (selectedAssignment == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an assignment to view details",
                    "No Assignment Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String details = "Assignment Details\n\n"
                + "ID: " + selectedAssignment.getAssignmentId() + "\n"
                + "Title: " + selectedAssignment.getTitle() + "\n"
                + "Description: " + selectedAssignment.getDescription() + "\n"
                + "Due Date: " + dateTimeFormat.format(selectedAssignment.getDueDate()) + "\n"
                + "Max Points: " + selectedAssignment.getMaxPoints() + "\n"
                + "Status: " + fieldStatus.getText() + "\n\n"
                + "Course: " + selectedCourseOffering.getCourse().getCourseId() + "\n"
                + "Instructor: " + fieldInstructor.getText();

        JOptionPane.showMessageDialog(this,
                details,
                "Assignment Details",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void btnSubmitAssignmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitAssignmentActionPerformed
        if (selectedAssignment == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an assignment to submit",
                    "No Assignment Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Submission existingSubmission = selectedAssignment.findSubmissionByStudent(
                studentProfile.getPerson().getPersonId()
        );

        if (existingSubmission != null) {
            JOptionPane.showMessageDialog(this,
                    "This assignment has already been submitted.\nStatus: " + existingSubmission.getStatus(),
                    "Already Submitted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedAssignment.isOverdue()) {
            int response = JOptionPane.showConfirmDialog(this,
                    "This assignment is overdue!\n"
                    + "Due Date: " + dateFormat.format(selectedAssignment.getDueDate()) + "\n\n"
                    + "Late submissions may receive reduced credit.\n"
                    + "Do you want to submit anyway?",
                    "Overdue Assignment",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Submit assignment: " + selectedAssignment.getTitle() + "?\n\n"
                + "Once submitted, you cannot modify your submission.\n"
                + "Make sure you have completed all requirements.",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {

            Submission newSubmission = new Submission(studentProfile, selectedAssignment);
            selectedAssignment.addSubmission(newSubmission);

            JOptionPane.showMessageDialog(this,
                    "Assignment submitted successfully!\n\n"
                    + "Submitted: " + dateTimeFormat.format(newSubmission.getSubmissionDate()) + "\n"
                    + "Your instructor will grade this assignment soon.",
                    "Submission Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            populateAssignmentsTable();
            loadSubmissionDetails(selectedAssignment);
        }
    }//GEN-LAST:event_btnSubmitAssignmentActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        if (selectedCourseOffering != null) {
            loadAssignments();
            clearSubmissionDetails();

            JOptionPane.showMessageDialog(this,
                    "Assignments refreshed successfully!",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSubmitAssignment;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cmbSelectCourse;
    private javax.swing.JTextField fieldCredits;
    private javax.swing.JTextField fieldFeedback;
    private javax.swing.JTextField fieldGrade;
    private javax.swing.JTextField fieldInstructor;
    private javax.swing.JTextField fieldPercentage;
    private javax.swing.JTextField fieldSemester;
    private javax.swing.JTextField fieldStatus;
    private javax.swing.JTextField fieldSubmittedOn;
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
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tblAssignment;
    // End of variables declaration//GEN-END:variables
}
