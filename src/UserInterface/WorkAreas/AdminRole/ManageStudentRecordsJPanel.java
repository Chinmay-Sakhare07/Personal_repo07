/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package UserInterface.WorkAreas.AdminRole;

import Business.Business;
import Business.Finance.Payment;
import Business.Person.Person;
import Business.Profiles.StudentProfile;
import Business.Transcripts.Transcript;
import Business.Transcripts.TranscriptEntry;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pranjalpatil
 */
/**
 * Creates new form ManageStudentRecords1
 */
public class ManageStudentRecordsJPanel extends javax.swing.JPanel {

    private Business business;
    private JPanel cardSequencePanel;
    private StudentProfile selectedStudent;
    private DecimalFormat gpaFormat;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public ManageStudentRecordsJPanel(Business business, JPanel cardSequencePanel) {
        this.business = business;
        this.cardSequencePanel = cardSequencePanel;
        this.gpaFormat = new DecimalFormat("0.00");
        this.currencyFormat = NumberFormat.getCurrencyInstance(java.util.Locale.US);
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        initComponents();

        setupUI();
        loadStudents();
    }

    private void setupUI() {
        fieldName.setEditable(false);
        fieldId.setEditable(false);
        fieldMajor.setEditable(false);
        fieldCreditsEarned.setEditable(false);
        fieldStanding.setEditable(false);
        fieldBalanceDue.setEditable(false);

        fieldName.setBackground(new Color(220, 220, 220));
        fieldId.setBackground(new Color(220, 220, 220));
        fieldMajor.setBackground(new Color(220, 220, 220));
        fieldCreditsEarned.setBackground(new Color(220, 220, 220));
        fieldStanding.setBackground(new Color(220, 220, 220));
        fieldBalanceDue.setBackground(new Color(220, 220, 220));

        clearStudentInfo();
    }

    private void loadStudents() {
        cmbBoxStudent.removeAllItems();

        ArrayList<StudentProfile> students = business.getStudentDirectory().getStudentList();

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No students found in the system",
                    "No Students",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (StudentProfile student : students) {
            String display = student.getPerson().getPersonId() + " - "
                    + student.getPerson().getFullName();
            cmbBoxStudent.addItem(display);
        }

        if (cmbBoxStudent.getItemCount() > 0) {
            cmbBoxStudent.setSelectedIndex(0);
            onStudentSelected();
        }
    }

    private void onStudentSelected() {
        int selectedIndex = cmbBoxStudent.getSelectedIndex();

        if (selectedIndex < 0) {
            clearStudentInfo();
            return;
        }

        selectedStudent = business.getStudentDirectory().getStudentList().get(selectedIndex);
        loadStudentDetails();
        loadTranscriptTable();
        loadPaymentsTable();
    }

    private void loadStudentDetails() {
        if (selectedStudent == null) {
            return;
        }

        Person person = selectedStudent.getPerson();
        Transcript transcript = selectedStudent.getTranscript();

        fieldName.setText(person.getFullName());
        fieldId.setText(person.getPersonId());
        fieldMajor.setText(person.getDepartment() != null ? person.getDepartment() : "N/A");

        double gpa = transcript.calculateOverallGPA();
        fieldCreditsEarned.setText(gpaFormat.format(gpa));

        if (gpa >= 3.7) {
            fieldCreditsEarned.setForeground(new Color(34, 139, 34));
        } else if (gpa >= 3.0) {
            fieldCreditsEarned.setForeground(new Color(40, 167, 69));
        } else if (gpa >= 2.7) {
            fieldCreditsEarned.setForeground(new Color(253, 126, 20));
        } else {
            fieldCreditsEarned.setForeground(new Color(220, 53, 69));
        }

        int credits = transcript.getTotalCredits();
        String standing = transcript.determineStanding();
        fieldStanding.setText(credits + " / 32 - " + standing);

        switch (standing) {
            case "Good Standing":
                fieldStanding.setForeground(new Color(40, 167, 69));
                break;
            case "Academic Warning":
                fieldStanding.setForeground(new Color(253, 126, 20));
                break;
            case "Academic Probation":
                fieldStanding.setForeground(new Color(220, 53, 69));
                break;
        }

        double balance = selectedStudent.getAccount().getOutstandingBalance();
        fieldBalanceDue.setText(currencyFormat.format(balance));

        if (balance > 0) {
            fieldBalanceDue.setForeground(new Color(220, 53, 69));
        } else {
            fieldBalanceDue.setForeground(new Color(40, 167, 69));
        }
    }

    private void loadTranscriptTable() {
        DefaultTableModel model = (DefaultTableModel) tblTranscripts.getModel();
        model.setRowCount(0);

        if (selectedStudent == null) {
            return;
        }

        ArrayList<TranscriptEntry> entries = selectedStudent.getTranscript().getEntries();

        for (TranscriptEntry entry : entries) {
            Object[] row = new Object[6];
            row[0] = entry.getOffering().getCourse().getCourseId();
            row[1] = entry.getOffering().getCourse().getCourseName();
            row[2] = entry.getCreditHours();
            row[3] = entry.getGrade();
            row[4] = entry.getTerm();
            row[5] = "Completed";

            model.addRow(row);
        }

        tblTranscripts.setDefaultEditor(Object.class, null);
    }

    private void loadPaymentsTable() {
        DefaultTableModel model = (DefaultTableModel) tblPayments.getModel();
        model.setRowCount(0);

        if (selectedStudent == null) {
            return;
        }

        ArrayList<Payment> payments = selectedStudent.getAccount().getPaymentHistory();

        for (Payment payment : payments) {
            Object[] row = new Object[6];
            row[0] = dateFormat.format(payment.getDate());
            row[1] = payment.getType();
            row[2] = currencyFormat.format(payment.getAmount());
            row[3] = payment.getStatus();
            row[4] = dateFormat.format(payment.getDate());
            row[5] = payment.getStatus();

            model.addRow(row);
        }

        tblPayments.setDefaultEditor(Object.class, null);
    }

    private void clearStudentInfo() {
        fieldName.setText("");
        fieldId.setText("");
        fieldMajor.setText("");
        fieldCreditsEarned.setText("");
        fieldStanding.setText("");
        fieldBalanceDue.setText("");

        DefaultTableModel transcriptModel = (DefaultTableModel) tblTranscripts.getModel();
        transcriptModel.setRowCount(0);

        DefaultTableModel paymentsModel = (DefaultTableModel) tblPayments.getModel();
        paymentsModel.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        lblPersonMgm = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        cmbBoxStudent = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnRecordPayment = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        fieldName = new javax.swing.JTextField();
        fieldId = new javax.swing.JTextField();
        fieldMajor = new javax.swing.JTextField();
        fieldStanding = new javax.swing.JTextField();
        fieldCreditsEarned = new javax.swing.JTextField();
        fieldBalanceDue = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        transcriptTab = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTranscripts = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPayments = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnApplyFee = new javax.swing.JButton();
        btnExportPdf = new javax.swing.JButton();
        btnPrintTranscript = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(800, 600));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        lblPersonMgm.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblPersonMgm.setForeground(new java.awt.Color(255, 255, 255));
        lblPersonMgm.setText("Student Records");

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(lblPersonMgm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBack)
                .addGap(53, 53, 53))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPersonMgm)
                    .addComponent(btnBack))
                .addGap(17, 17, 17))
        );

        jSplitPane1.setDividerLocation(230);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        cmbBoxStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxStudentActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel2.setText("Student Info");

        jLabel3.setText("Name");

        jLabel5.setText("ID");

        jLabel7.setText("Major");

        jLabel9.setText("Email");

        jLabel12.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel12.setText("Academic Summary");

        jLabel13.setText("GPA");

        jLabel15.setText("Credits Earned");

        jLabel17.setText("Standing");

        jLabel19.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel19.setText("Finanacial Summary");

        jLabel20.setText("Balance Due");

        btnRecordPayment.setText("Record Payment");
        btnRecordPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordPaymentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(fieldStanding, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel18))
                                    .addComponent(jLabel16)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(31, 31, 31)
                                .addComponent(fieldBalanceDue, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(fieldMajor, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fieldId, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldCreditsEarned, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel13)
                            .addComponent(jLabel17)
                            .addComponent(jLabel20)
                            .addComponent(jLabel3))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbBoxStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnRecordPayment)
                        .addGap(27, 27, 27))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(cmbBoxStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel11)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(fieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(fieldMajor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(fieldCreditsEarned, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(fieldStanding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(fieldBalanceDue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRecordPayment)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setBackground(new java.awt.Color(102, 255, 255));

        tblTranscripts.setBackground(new java.awt.Color(204, 255, 255));
        tblTranscripts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Credits", "Grade", "Term", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTranscripts);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );

        transcriptTab.addTab("Transcript", jPanel6);

        tblPayments.setBackground(new java.awt.Color(204, 255, 255));
        tblPayments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Payment ID", "Date", "Amount", "Method", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblPayments);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );

        transcriptTab.addTab("Payments", jPanel7);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(transcriptTab, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(transcriptTab, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        jPanel4.setBackground(new java.awt.Color(0, 204, 204));

        btnApplyFee.setText("Apply Fee");
        btnApplyFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyFeeActionPerformed(evt);
            }
        });

        btnExportPdf.setText("Export PDF");
        btnExportPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPdfActionPerformed(evt);
            }
        });

        btnPrintTranscript.setText("Print Transcript");
        btnPrintTranscript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintTranscriptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(219, 219, 219)
                .addComponent(btnApplyFee)
                .addGap(18, 18, 18)
                .addComponent(btnExportPdf)
                .addGap(18, 18, 18)
                .addComponent(btnPrintTranscript)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApplyFee, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrintTranscript, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        AdminWorkAreaPanel dashboard = new AdminWorkAreaPanel(business, cardSequencePanel);
        cardSequencePanel.add(dashboard, "AdminDashboard");
        ((java.awt.CardLayout) cardSequencePanel.getLayout()).show(cardSequencePanel, "AdminDashboard");
        cardSequencePanel.revalidate();
        cardSequencePanel.repaint();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnApplyFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyFeeActionPerformed
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String feeAmountStr = JOptionPane.showInputDialog(this,
                "Enter fee amount to apply for " + selectedStudent.getPerson().getFullName() + ":",
                "Apply Fee",
                JOptionPane.QUESTION_MESSAGE);

        if (feeAmountStr == null || feeAmountStr.trim().isEmpty()) {
            return;
        }

        double feeAmount;
        try {
            feeAmount = Double.parseDouble(feeAmountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric amount",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (feeAmount <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Fee amount must be greater than zero",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Apply fee of " + currencyFormat.format(feeAmount) + " to "
                + selectedStudent.getPerson().getFullName() + "?",
                "Confirm Fee",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            selectedStudent.getAccount().billTuition(feeAmount);

            JOptionPane.showMessageDialog(this,
                    "Fee applied successfully!\n\n"
                    + "Amount: " + currencyFormat.format(feeAmount) + "\n"
                    + "New Balance: " + currencyFormat.format(selectedStudent.getAccount().getOutstandingBalance()),
                    "Fee Applied",
                    JOptionPane.INFORMATION_MESSAGE);

            loadStudentDetails();
            loadPaymentsTable();
        }
    }//GEN-LAST:event_btnApplyFeeActionPerformed

    private void btnExportPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPdfActionPerformed
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "PDF Export functionality would be implemented here.\n\n"
                + "This would export the transcript for:\n"
                + selectedStudent.getPerson().getFullName() + " ("
                + selectedStudent.getPerson().getPersonId() + ")\n\n"
                + "For now, use 'Print Transcript' to view the formatted transcript.",
                "Export PDF",
                JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_btnExportPdfActionPerformed

    private void btnPrintTranscriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintTranscriptActionPerformed
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Transcript transcript = selectedStudent.getTranscript();
        ArrayList<TranscriptEntry> entries = transcript.getEntries();

        if (entries.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Student has no transcript entries to print",
                    "No Transcript",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder transcriptText = new StringBuilder();
        transcriptText.append("NORTHEASTERN UNIVERSITY\n");
        transcriptText.append("OFFICIAL TRANSCRIPT\n\n");
        transcriptText.append("Student: ").append(selectedStudent.getPerson().getFullName()).append("\n");
        transcriptText.append("ID: ").append(selectedStudent.getPerson().getPersonId()).append("\n");
        transcriptText.append("Department: ").append(selectedStudent.getPerson().getDepartment()).append("\n\n");
        transcriptText.append("═══════════════════════════════════════════════════\n\n");

        String currentTerm = "";
        for (TranscriptEntry entry : entries) {
            if (!entry.getTerm().equals(currentTerm)) {
                if (!currentTerm.isEmpty()) {
                    transcriptText.append("\n");
                }
                currentTerm = entry.getTerm();
                transcriptText.append(currentTerm).append("\n");
                transcriptText.append("───────────────────────────────────────────────\n");
            }

            transcriptText.append(String.format("%-10s %-35s %2d cr  %3s\n",
                    entry.getOffering().getCourse().getCourseId(),
                    entry.getOffering().getCourse().getCourseName(),
                    entry.getCreditHours(),
                    entry.getGrade()));
        }

        transcriptText.append("\n═══════════════════════════════════════════════════\n");
        transcriptText.append("Total Credits: ").append(transcript.getTotalCredits()).append(" / 32\n");
        transcriptText.append("Overall GPA: ").append(gpaFormat.format(transcript.calculateOverallGPA())).append("\n");
        transcriptText.append("Academic Standing: ").append(transcript.determineStanding()).append("\n");
        transcriptText.append("Ready to Graduate: ").append(selectedStudent.isReadyToGraduate() ? "Yes" : "No").append("\n");

        JOptionPane.showMessageDialog(this,
                new javax.swing.JScrollPane(new javax.swing.JTextArea(transcriptText.toString(), 20, 50)),
                "Transcript Preview",
                JOptionPane.PLAIN_MESSAGE);

    }//GEN-LAST:event_btnPrintTranscriptActionPerformed

    private void btnRecordPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordPaymentActionPerformed
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double currentBalance = selectedStudent.getAccount().getOutstandingBalance();

        if (currentBalance <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Student has no outstanding balance",
                    "No Balance",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String paymentAmountStr = JOptionPane.showInputDialog(this,
                "Record payment for " + selectedStudent.getPerson().getFullName() + "\n"
                + "Outstanding Balance: " + currencyFormat.format(currentBalance) + "\n\n"
                + "Enter payment amount:",
                "Record Payment",
                JOptionPane.QUESTION_MESSAGE);

        if (paymentAmountStr == null || paymentAmountStr.trim().isEmpty()) {
            return;
        }

        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(paymentAmountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric amount",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (paymentAmount <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Payment amount must be greater than zero",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (paymentAmount > currentBalance) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Payment amount (" + currencyFormat.format(paymentAmount)
                    + ") exceeds balance (" + currencyFormat.format(currentBalance) + ").\n\n"
                    + "Do you want to apply the full balance instead?",
                    "Amount Exceeds Balance",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                paymentAmount = currentBalance;
            } else {
                return;
            }
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Record payment of " + currencyFormat.format(paymentAmount) + " for "
                + selectedStudent.getPerson().getFullName() + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            selectedStudent.getAccount().makePayment(paymentAmount);

            JOptionPane.showMessageDialog(this,
                    "Payment recorded successfully!\n\n"
                    + "Amount: " + currencyFormat.format(paymentAmount) + "\n"
                    + "New Balance: " + currencyFormat.format(selectedStudent.getAccount().getOutstandingBalance()),
                    "Payment Recorded",
                    JOptionPane.INFORMATION_MESSAGE);

            loadStudentDetails();
            loadPaymentsTable();
        }

    }//GEN-LAST:event_btnRecordPaymentActionPerformed

    private void cmbBoxStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxStudentActionPerformed
        onStudentSelected();

    }//GEN-LAST:event_cmbBoxStudentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyFee;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnExportPdf;
    private javax.swing.JButton btnPrintTranscript;
    private javax.swing.JButton btnRecordPayment;
    private javax.swing.JComboBox<String> cmbBoxStudent;
    private javax.swing.JTextField fieldBalanceDue;
    private javax.swing.JTextField fieldCreditsEarned;
    private javax.swing.JTextField fieldId;
    private javax.swing.JTextField fieldMajor;
    private javax.swing.JTextField fieldName;
    private javax.swing.JTextField fieldStanding;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblPersonMgm;
    private javax.swing.JTable tblPayments;
    private javax.swing.JTable tblTranscripts;
    private javax.swing.JTabbedPane transcriptTab;
    // End of variables declaration//GEN-END:variables
}
