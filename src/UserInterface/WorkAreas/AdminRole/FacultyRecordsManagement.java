/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.AdminRole;

import Business.Business;
import Business.Course.CourseOffering;
import Business.Course.Enrollment;
import Business.Person.Person;
import Business.Profiles.FacultyProfile;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pranjalpatil
 */
public class FacultyRecordsManagement extends javax.swing.JPanel {

    /**
     * Creates new form FacultyRecordsManagement
     */
    private Business business;
    private JPanel cardSequencePanel;
    private FacultyProfile selectedFaculty;
    private CourseOffering selectedCourseOffering;

    public FacultyRecordsManagement(Business business, JPanel cardSequencePanel) {
        this.business = business;
        this.cardSequencePanel = cardSequencePanel;

        initComponents();

        setupUI();
        loadAllFaculty();
    }

    private void setupUI() {
        fieldFacultyId.setEditable(false);
        fieldName.setEditable(false);
        fieldEmail.setEditable(false);
        fieldPhone.setEditable(false);
        fieldDepartment.setEditable(false);
        fieldTotalCourses.setEditable(false);
        fieldTotalStudent.setEditable(false);
        fieldCurrentStudents.setEditable(false);

        fieldFacultyId.setBackground(new Color(220, 220, 220));
        fieldName.setBackground(new Color(220, 220, 220));
        fieldEmail.setBackground(new Color(220, 220, 220));
        fieldPhone.setBackground(new Color(220, 220, 220));
        fieldDepartment.setBackground(new Color(220, 220, 220));
        fieldTotalCourses.setBackground(new Color(220, 220, 220));
        fieldTotalStudent.setBackground(new Color(220, 220, 220));
        fieldCurrentStudents.setBackground(new Color(220, 220, 220));

        clearFacultyDetails();
    }

    private void loadAllFaculty() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        ArrayList<FacultyProfile> facultyList = (ArrayList<FacultyProfile>) business.getFacultyDirectory().getAllFaculty();

        for (FacultyProfile faculty : facultyList) {
            Object[] row = new Object[5];
            row[0] = faculty.getPerson().getPersonId();
            row[1] = faculty.getPerson().getFullName();
            row[2] = faculty.getPerson().getEmail();
            row[3] = faculty.getDepartment() != null ? faculty.getDepartment() : "N/A";
            row[4] = faculty.getOfferings().size();

            model.addRow(row);
        }

        jTable1.setDefaultEditor(Object.class, null);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onFacultySelected();
            }
        });
    }

    private void onFacultySelected() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow < 0) {
            clearFacultyDetails();
            return;
        }

        String facultyId = (String) jTable1.getValueAt(selectedRow, 0);
        selectedFaculty = business.getFacultyDirectory().findFacultyByPersonId(facultyId);

        if (selectedFaculty != null) {
            loadFacultyDetails();
            loadTeachingAssignments();
        }
    }

    private void loadFacultyDetails() {
        if (selectedFaculty == null) {
            return;
        }

        Person person = selectedFaculty.getPerson();

        fieldFacultyId.setText(person.getPersonId());
        fieldName.setText(person.getFullName());
        fieldEmail.setText(person.getEmail());
        fieldPhone.setText(person.getPhone() != null ? person.getPhone() : "N/A");
        fieldDepartment.setText(selectedFaculty.getDepartment() != null ? selectedFaculty.getDepartment() : "N/A");

        int totalCourses = selectedFaculty.getOfferings().size();
        fieldTotalCourses.setText(String.valueOf(totalCourses));

        int totalStudentsAllTime = 0;
        int currentSemesterStudents = 0;

        for (CourseOffering offering : selectedFaculty.getOfferings()) {
            int enrolled = offering.getActiveEnrollmentCount();
            totalStudentsAllTime += enrolled;

            if (offering.getSemester().equals("Fall 2025")) {
                currentSemesterStudents += enrolled;
            }
        }

        fieldTotalStudent.setText(String.valueOf(totalStudentsAllTime));
        fieldCurrentStudents.setText(String.valueOf(currentSemesterStudents));
    }

    private void loadTeachingAssignments() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        if (selectedFaculty == null) {
            return;
        }

        for (CourseOffering offering : selectedFaculty.getOfferings()) {
            Object[] row = new Object[4];
            row[0] = offering.getCourse().getCourseId();
            row[1] = offering.getCourse().getCourseName();
            row[2] = offering.getSemester();
            row[3] = offering.getActiveEnrollmentCount();

            model.addRow(row);
        }

        jTable2.setDefaultEditor(Object.class, null);

        jTable2.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onCourseSelected();
            }
        });
    }

    private void onCourseSelected() {
        int selectedRow = jTable2.getSelectedRow();

        if (selectedRow < 0 || selectedFaculty == null) {
            selectedCourseOffering = null;
            return;
        }

        selectedCourseOffering = selectedFaculty.getOfferings().get(selectedRow);
    }

    private void clearFacultyDetails() {
        fieldFacultyId.setText("");
        fieldName.setText("");
        fieldEmail.setText("");
        fieldPhone.setText("");
        fieldDepartment.setText("");
        fieldTotalCourses.setText("");
        fieldTotalStudent.setText("");
        fieldCurrentStudents.setText("");

        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        selectedFaculty = null;
        selectedCourseOffering = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fieldPhone = new javax.swing.JTextField();
        fieldName = new javax.swing.JTextField();
        fieldEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        fieldFacultyId = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        fieldDepartment = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        fieldTotalCourses = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        fieldTotalStudent = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        fieldCurrentStudents = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel1.setText("FACULTY RECORD MANAGEMENT");

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(74, 74, 74))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setText("Faculty Records");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Email", "Department", "Courses"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));

        jLabel3.setText("Faculty Details");

        jLabel4.setText("Faculty ID");

        jLabel5.setText("Name");

        jLabel6.setText("Email");

        fieldEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldEmailActionPerformed(evt);
            }
        });

        jLabel7.setText("Phone");

        jLabel8.setText("Department");

        fieldDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldDepartmentActionPerformed(evt);
            }
        });

        jButton2.setText("Edit Faculty Info");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Delete Faculty");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(58, 58, 58))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fieldName)
                    .addComponent(fieldEmail)
                    .addComponent(fieldFacultyId, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addGap(192, 192, 192)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fieldPhone)
                    .addComponent(fieldDepartment, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addGap(89, 89, 89))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addComponent(jButton2)
                .addGap(83, 83, 83)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(fieldFacultyId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(fieldDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Semester", "Students"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel9.setText("Teaching Assignments");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));

        jLabel10.setText("Total Couses Taught: ");

        jLabel11.setText("Total Students (All Time): ");

        jLabel12.setText("Current Semester Students:");

        jButton4.setText("Assign Course");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Remove Course");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("View Student List");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldTotalCourses, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldTotalStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addGap(29, 29, 29)
                        .addComponent(fieldCurrentStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addGap(129, 129, 129)
                        .addComponent(jButton6)
                        .addGap(81, 81, 81))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(fieldTotalCourses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(fieldTotalStudent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(fieldCurrentStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 800, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fieldEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldEmailActionPerformed

    private void fieldDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldDepartmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldDepartmentActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a faculty member from the table",
                    "No Faculty Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Person person = selectedFaculty.getPerson();
        int coursesCount = selectedFaculty.getOfferings().size();

        if (coursesCount > 0) {
            int response = JOptionPane.showConfirmDialog(this,
                    "This faculty member is currently teaching " + coursesCount + " course(s).\n"
                    + "Deleting will remove all course assignments.\n\n"
                    + "Are you sure you want to continue?",
                    "Active Courses Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this faculty member?\n\n"
                + "ID: " + person.getPersonId() + "\n"
                + "Name: " + person.getFullName() + "\n"
                + "Courses Teaching: " + coursesCount + "\n\n"
                + "This action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            String facultyId = person.getPersonId();

            business.getFacultyDirectory().removeFaculty(selectedFaculty);
            business.getUserAccountDirectory().removeUserAccount(facultyId);
            business.getPersonDirectory().removePerson(facultyId);

            JOptionPane.showMessageDialog(this,
                    "Faculty member deleted successfully!",
                    "Delete Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            clearFacultyDetails();
            loadAllFaculty();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (selectedCourseOffering == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course from the Teaching Assignments table",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder studentList = new StringBuilder();
        studentList.append("STUDENT LIST\n");
        studentList.append("Course: ").append(selectedCourseOffering.getCourse().getCourseId())
                .append(" - ").append(selectedCourseOffering.getCourse().getCourseName()).append("\n");
        studentList.append("Semester: ").append(selectedCourseOffering.getSemester()).append("\n");
        studentList.append("Faculty: ").append(selectedFaculty.getPerson().getFullName()).append("\n\n");
        studentList.append("═══════════════════════════════════════════════════\n\n");

        if (selectedCourseOffering.getEnrollments().isEmpty()) {
            studentList.append("No students enrolled in this course.\n");
        } else {
            studentList.append(String.format("%-12s %-25s %-10s\n", "Student ID", "Name", "Status"));
            studentList.append("─────────────────────────────────────────────────\n");

            for (Enrollment enrollment : selectedCourseOffering.getEnrollments()) {
                if (!enrollment.getStatus().equalsIgnoreCase("Dropped")) {
                    studentList.append(String.format("%-12s %-25s %-10s\n",
                            enrollment.getStudent().getPerson().getPersonId(),
                            enrollment.getStudent().getPerson().getFullName(),
                            enrollment.getGrade() != null ? enrollment.getGrade() : "In Progress"));
                }
            }

            studentList.append("\n─────────────────────────────────────────────────\n");
            studentList.append("Total Enrolled: ").append(selectedCourseOffering.getActiveEnrollmentCount()).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                new javax.swing.JScrollPane(new javax.swing.JTextArea(studentList.toString(), 15, 50)),
                "Student List",
                JOptionPane.PLAIN_MESSAGE);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AdminWorkAreaPanel dashboard = new AdminWorkAreaPanel(business, cardSequencePanel);
        cardSequencePanel.add(dashboard, "AdminDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "AdminDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a faculty member from the table",
                    "No Faculty Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Person person = selectedFaculty.getPerson();

        String newEmail = JOptionPane.showInputDialog(this,
                "Edit Email for " + person.getFullName() + ":\n"
                + "Current: " + person.getEmail(),
                "Edit Email",
                JOptionPane.QUESTION_MESSAGE);

        if (newEmail == null || newEmail.trim().isEmpty()) {
            return;
        }

        String newPhone = JOptionPane.showInputDialog(this,
                "Edit Phone for " + person.getFullName() + ":\n"
                + "Current: " + person.getPhone(),
                "Edit Phone",
                JOptionPane.QUESTION_MESSAGE);

        if (newPhone == null || newPhone.trim().isEmpty()) {
            return;
        }

        String newDepartment = JOptionPane.showInputDialog(this,
                "Edit Department for " + person.getFullName() + ":\n"
                + "Current: " + selectedFaculty.getDepartment(),
                "Edit Department",
                JOptionPane.QUESTION_MESSAGE);

        if (newDepartment == null || newDepartment.trim().isEmpty()) {
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Update faculty information?\n\n"
                + "Faculty: " + person.getFullName() + "\n"
                + "New Email: " + newEmail + "\n"
                + "New Phone: " + newPhone + "\n"
                + "New Department: " + newDepartment,
                "Confirm Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            person.setEmail(newEmail);
            person.setPhone(newPhone);
            person.setDepartment(newDepartment);
            selectedFaculty.setDepartment(newDepartment);

            JOptionPane.showMessageDialog(this,
                    "Faculty information updated successfully!",
                    "Update Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadAllFaculty();
            loadFacultyDetails();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a faculty member first",
                    "No Faculty Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<CourseOffering> availableCourses = new ArrayList<>();

        for (CourseOffering offering : business.getCourseDirectory().getOfferings()) {
            if (offering.getSemester().equals("Fall 2025")
                    && !selectedFaculty.getOfferings().contains(offering)) {
                availableCourses.add(offering);
            }
        }

        if (availableCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No available courses to assign for Fall 2025",
                    "No Courses Available",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] courseOptions = new String[availableCourses.size()];
        for (int i = 0; i < availableCourses.size(); i++) {
            courseOptions[i] = availableCourses.get(i).getCourse().getCourseId() + " - "
                    + availableCourses.get(i).getCourse().getCourseName();
        }

        String selectedCourse = (String) JOptionPane.showInputDialog(this,
                "Select course to assign to " + selectedFaculty.getPerson().getFullName() + ":",
                "Assign Course",
                JOptionPane.QUESTION_MESSAGE,
                null,
                courseOptions,
                courseOptions[0]);

        if (selectedCourse == null) {
            return;
        }

        int selectedIndex = -1;
        for (int i = 0; i < courseOptions.length; i++) {
            if (courseOptions[i].equals(selectedCourse)) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex >= 0) {
            CourseOffering courseToAssign = availableCourses.get(selectedIndex);

            int response = JOptionPane.showConfirmDialog(this,
                    "Assign " + courseToAssign.getCourse().getCourseId() + " to "
                    + selectedFaculty.getPerson().getFullName() + "?",
                    "Confirm Assignment",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                courseToAssign.setFaculty(selectedFaculty);
                selectedFaculty.addCourseOffering(courseToAssign);

                JOptionPane.showMessageDialog(this,
                        "Course assigned successfully!",
                        "Assignment Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                loadFacultyDetails();
                loadTeachingAssignments();
                loadAllFaculty();
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (selectedFaculty == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a faculty member first",
                    "No Faculty Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCourseOffering == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course from the Teaching Assignments table",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrolledStudents = selectedCourseOffering.getActiveEnrollmentCount();

        if (enrolledStudents > 0) {
            int response = JOptionPane.showConfirmDialog(this,
                    "This course has " + enrolledStudents + " enrolled student(s).\n"
                    + "Removing faculty assignment may affect these students.\n\n"
                    + "Are you sure you want to continue?",
                    "Students Enrolled Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Remove " + selectedCourseOffering.getCourse().getCourseId() + " from "
                + selectedFaculty.getPerson().getFullName() + "'s assignments?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            selectedFaculty.removeCourseOffering(selectedCourseOffering);
            selectedCourseOffering.setFaculty(null);

            JOptionPane.showMessageDialog(this,
                    "Course removed successfully!",
                    "Removal Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadFacultyDetails();
            loadTeachingAssignments();
            loadAllFaculty();
        }
    }//GEN-LAST:event_jButton5ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fieldCurrentStudents;
    private javax.swing.JTextField fieldDepartment;
    private javax.swing.JTextField fieldEmail;
    private javax.swing.JTextField fieldFacultyId;
    private javax.swing.JTextField fieldName;
    private javax.swing.JTextField fieldPhone;
    private javax.swing.JTextField fieldTotalCourses;
    private javax.swing.JTextField fieldTotalStudent;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
