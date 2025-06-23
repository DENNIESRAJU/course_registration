package com.courseregistration.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import com.courseregistration.dao.DBConfig;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel; 
    public MainFrame() {
        setTitle("Course Registration System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
       
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
       
        tabbedPane = new JTabbedPane();
        
      
        dashboardPanel = new DashboardPanel();
        StudentPanel studentPanel = new StudentPanel(this); 
        CoursePanel coursePanel = new CoursePanel(this);    
        RegistrationPanel registrationPanel = new RegistrationPanel(this);
        
      
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Registrations", registrationPanel);
        
    
        setJMenuBar(createMenuBar());
        add(tabbedPane);
    }
    
    
    public void refreshDashboard() {
        dashboardPanel.refresh();
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
     
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem resetDBItem = new JMenuItem("Reset Database");
        resetDBItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to reset the database? All data will be lost!",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                DBConfig.resetDatabase();
                refreshDashboard(); // Refresh dashboard immediately
                JOptionPane.showMessageDialog(this, "Database has been reset and dashboard refreshed!");
            }
        });
        toolsMenu.add(resetDBItem);
        
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Course Registration System\nVersion 1.0\n\nDeveloped by Your Name",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}