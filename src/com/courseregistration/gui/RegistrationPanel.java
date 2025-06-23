package com.courseregistration.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.courseregistration.dao.DBConfig;
import com.courseregistration.model.Registration;

public class RegistrationPanel extends JPanel implements Refreshable {
    private JTable registrationTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    
    public RegistrationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton registerButton = new JButton("Register Student");
        registerButton.addActionListener(e -> showRegistrationDialog());
        
        JButton deregisterButton = new JButton("Deregister Student");
        deregisterButton.addActionListener(e -> showDeregistrationDialog());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        
        toolBar.add(registerButton);
        toolBar.addSeparator();
        toolBar.add(deregisterButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
        
       
        tableModel = new DefaultTableModel(new Object[]{"ID", "Student ID", "Student Name", "Course Code", "Course Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        registrationTable = new JTable(tableModel);
        registrationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
       
        registrationTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        registrationTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        registrationTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        registrationTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        registrationTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(registrationTable);
        add(scrollPane, BorderLayout.CENTER);
        
      
        refresh();
    }
    
    @Override
    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                refreshRegistrationData();
                return null;
            }
        };
        worker.execute();
    }
    
    private void refreshRegistrationData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT r.id, r.student_id, s.name as student_name, " +
                 "r.course_code, c.name as course_name " +
                 "FROM registration r " +
                 "JOIN student s ON r.student_id = s.id " +
                 "JOIN course c ON r.course_code = c.course_code " +
                 "ORDER BY r.id DESC")) {
            
            List<Object[]> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    cleanString(rs.getString("student_name")),
                    cleanString(rs.getString("course_code")),
                    cleanString(rs.getString("course_name"))
                });
            }
            
            SwingUtilities.invokeLater(() -> {
                for (Object[] row : rows) {
                    tableModel.addRow(row);
                }
            });
        } catch (SQLException e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, "Error loading registrations: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }
    
    private String cleanString(String input) {
        return input != null ? input.trim().replaceAll("[^a-zA-Z0-9\\s]", "") : "";
    }
    
    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(mainFrame, "Register Student", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        
        panel.add(new JLabel("Student:"));
        List<String> students = getStudentsWithNames();
        JComboBox<String> studentCombo = new JComboBox<>(students.toArray(new String[0]));
        panel.add(studentCombo);
        
       
        panel.add(new JLabel("Course:"));
        List<String> courses = getCoursesWithAvailability();
        JComboBox<String> courseCombo = new JComboBox<>(courses.toArray(new String[0]));
        panel.add(courseCombo);
        
      
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");
        
        registerButton.addActionListener(e -> {
            String selectedStudent = (String)studentCombo.getSelectedItem();
            String selectedCourse = (String)courseCombo.getSelectedItem();
            
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            String courseCode = selectedCourse.split(" - ")[0];
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try (Connection conn = DBConfig.getConnection()) {
                        // First check if student is already registered for this course
                        if (isAlreadyRegistered(conn, studentId, courseCode)) {
                            SwingUtilities.invokeLater(() -> 
                                JOptionPane.showMessageDialog(dialog, 
                                    "This student is already registered for this course!",
                                    "Duplicate Registration", JOptionPane.WARNING_MESSAGE));
                            return null;
                        }
                        
                        Registration registration = new Registration(studentId, courseCode);
                        if (registerStudent(conn, registration)) {
                            SwingUtilities.invokeLater(() -> {
                                refresh();
                                mainFrame.refreshDashboard();
                                dialog.dispose();
                            });
                        }
                    } catch (SQLException ex) {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(dialog, 
                                "Registration failed: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
                    }
                    return null;
                }
            }.execute();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        panel.add(registerButton);
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private boolean isAlreadyRegistered(Connection conn, int studentId, String courseCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registration WHERE student_id = ? AND course_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, courseCode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    private void showDeregistrationDialog() {
        int selectedRow = registrationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a registration to deregister", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int registrationId = (int) tableModel.getValueAt(selectedRow, 0);
        int studentId = (int) tableModel.getValueAt(selectedRow, 1);
        String courseCode = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deregister this student?\n" +
            "Student ID: " + studentId + "\n" +
            "Course: " + courseCode,
            "Confirm Deregistration", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try (Connection conn = DBConfig.getConnection()) {
                        if (deregisterStudent(conn, registrationId, courseCode)) {
                            SwingUtilities.invokeLater(() -> {
                                refresh();
                                mainFrame.refreshDashboard();
                                JOptionPane.showMessageDialog(RegistrationPanel.this,
                                    "Deregistration successful!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }
                    } catch (SQLException ex) {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(RegistrationPanel.this,
                                "Deregistration failed: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
                    }
                    return null;
                }
            }.execute();
        }
    }
    
    private boolean registerStudent(Connection conn, Registration registration) throws SQLException {
        
        String checkSql = "SELECT seat_available FROM course WHERE course_code = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, registration.getCourseCode());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt("seat_available") <= 0) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, 
                        "No seats available for this course!",
                        "Error", JOptionPane.ERROR_MESSAGE));
                return false;
            }
        }
        
      
        String regSql = "INSERT INTO registration (student_id, course_code) VALUES (?, ?)";
        String updateSql = "UPDATE course SET seat_available = seat_available - 1 WHERE course_code = ?";
        
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement regStmt = conn.prepareStatement(regSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                
               
                regStmt.setInt(1, registration.getStudentId());
                regStmt.setString(2, registration.getCourseCode());
                regStmt.executeUpdate();
                
               
                updateStmt.setString(1, registration.getCourseCode());
                updateStmt.executeUpdate();
                
                conn.commit();
                
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, 
                        "Registration successful!",
                        "Success", JOptionPane.INFORMATION_MESSAGE));
                return true;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    private boolean deregisterStudent(Connection conn, int registrationId, String courseCode) throws SQLException {
        String deleteSql = "DELETE FROM registration WHERE id = ?";
        String updateSql = "UPDATE course SET seat_available = seat_available + 1 WHERE course_code = ?";
        
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                
                
                deleteStmt.setInt(1, registrationId);
                deleteStmt.executeUpdate();
                
               
                updateStmt.setString(1, courseCode);
                updateStmt.executeUpdate();
                
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    private List<String> getStudentsWithNames() {
        List<String> students = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM student ORDER BY name")) {
            
            while (rs.next()) {
                students.add(rs.getInt("id") + " - " + cleanString(rs.getString("name")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return students;
    }
    
    private List<String> getCoursesWithAvailability() {
        List<String> courses = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT course_code, name, seat_available FROM course ORDER BY name")) {
            
            while (rs.next()) {
                courses.add(cleanString(rs.getString("course_code")) + " - " + 
                           cleanString(rs.getString("name")) + " (" + 
                           rs.getInt("seat_available") + " seats available)");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        return courses;
    }
}