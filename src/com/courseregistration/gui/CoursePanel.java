package com.courseregistration.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import com.courseregistration.dao.DBConfig;
import com.courseregistration.model.Course;

public class CoursePanel extends JPanel implements Refreshable {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    
    public CoursePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("Add Course");
        addButton.addActionListener(e -> showAddCourseDialog());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        
        toolBar.add(addButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
        
        
        tableModel = new DefaultTableModel(new Object[]{"ID", "Code", "Name", "Seats Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);
        
        refresh();
    }
    
    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Course", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel codeLabel = new JLabel("Course Code:");
        JTextField codeField = new JTextField();
        
        JLabel nameLabel = new JLabel("Course Name:");
        JTextField nameField = new JTextField();
        
        JLabel seatsLabel = new JLabel("Seat Limit:");
        JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            int seats = (Integer)seatsSpinner.getValue();
            
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try (Connection conn = DBConfig.getConnection()) {
                        Course course = new Course(code, name, seats);
                        addCourse(conn, course);
                    } catch (SQLException ex) {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(dialog, "Error saving course: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE));
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    refresh();
                    mainFrame.refreshDashboard();
                    dialog.dispose();
                }
            }.execute();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        panel.add(codeLabel);
        panel.add(codeField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(seatsLabel);
        panel.add(seatsSpinner);
        panel.add(new JLabel());
        panel.add(saveButton);
        panel.add(new JLabel());
        panel.add(cancelButton);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void addCourse(Connection conn, Course course) throws SQLException {
        String sql = "INSERT INTO course (course_code, name, seat_available) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getSeatLimit());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, "Course added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE));
            }
        }
    }
    
    @Override
    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                refreshCourseData();
                return null;
            }
        };
        worker.execute();
    }
    
    private void refreshCourseData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM course ORDER BY name")) {
            
            ArrayList<Object[]> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("course_code"),
                    rs.getString("name"),
                    rs.getInt("seat_available")
                });
            }
            
            SwingUtilities.invokeLater(() -> {
                for (Object[] row : rows) {
                    tableModel.addRow(row);
                }
            });
            
        } catch (SQLException e) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, 
                    "Error loading courses: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE));
        }
    }
}