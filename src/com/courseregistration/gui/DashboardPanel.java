package com.courseregistration.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.courseregistration.dao.DBConfig;

public class DashboardPanel extends JPanel implements Refreshable {
    private JLabel studentCountLabel;
    private JLabel courseCountLabel;
    private JLabel registrationCountLabel;
    private JLabel seatsLabel;
    private JPanel seatsGridPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        initUI();
        refresh();
    }

    private void initUI() {
       
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("COURSE REGISTRATION SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       
        studentCountLabel = createStatLabel("0");
        courseCountLabel = createStatLabel("0");
        registrationCountLabel = createStatLabel("0");
        seatsLabel = createStatLabel("0");

        statsPanel.add(createStatCard("Total Students", studentCountLabel));
        statsPanel.add(createStatCard("Total Courses", courseCountLabel));
        statsPanel.add(createStatCard("Active Registrations", registrationCountLabel));
        statsPanel.add(createStatCard("Available Seats", seatsLabel));

        mainPanel.add(statsPanel, BorderLayout.NORTH);

      
        seatsGridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        seatsGridPanel.setBorder(BorderFactory.createTitledBorder("Course Availability"));
        JScrollPane gridScrollPane = new JScrollPane(seatsGridPanel);
        gridScrollPane.setPreferredSize(new Dimension(0, 200));
        mainPanel.add(gridScrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

      
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel statusLabel = new JLabel("Ready");
        footerPanel.add(statusLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private int students, courses, registrations, seats;
            private List<CourseAvailability> courseAvailability;

            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DBConfig.getConnection()) {
                   
                    students = getCount(conn, "SELECT COUNT(*) FROM student");
                    courses = getCount(conn, "SELECT COUNT(*) FROM course");
                    registrations = getCount(conn, "SELECT COUNT(*) FROM registration");
                    seats = getSum(conn, "SELECT SUM(seat_available) FROM course");

                    
                    courseAvailability = getCourseAvailability(conn);
                }
                return null;
            }

            @Override
            protected void done() {
               
                studentCountLabel.setText(String.valueOf(students));
                courseCountLabel.setText(String.valueOf(courses));
                registrationCountLabel.setText(String.valueOf(registrations));
                seatsLabel.setText(String.valueOf(seats));

               
                updateCourseGrid(courseAvailability);
            }
        };
        worker.execute();
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 36));
        label.setForeground(new Color(70, 130, 180));
        return label;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(100, 100, 100));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getSum(Connection conn, String sql) throws SQLException {
        return getCount(conn, sql); // Same implementation for this use case
    }

    private List<CourseAvailability> getCourseAvailability(Connection conn) throws SQLException {
        List<CourseAvailability> availability = new ArrayList<>();
        String sql = "SELECT c.course_code, c.name, c.seat_available, " +
                     "COUNT(r.id) as registered " +
                     "FROM course c LEFT JOIN registration r " +
                     "ON c.course_code = r.course_code " +
                     "GROUP BY c.course_code, c.name, c.seat_available " +
                     "ORDER BY c.name";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                availability.add(new CourseAvailability(
                    rs.getString("course_code"),
                    rs.getString("name"),
                    rs.getInt("seat_available"),
                    rs.getInt("registered")
                ));
            }
        }
        return availability;
    }

    private void updateCourseGrid(List<CourseAvailability> courses) {
        seatsGridPanel.removeAll();
        
        for (CourseAvailability course : courses) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

         
            JLabel courseLabel = new JLabel(
                "<html><b>" + course.name + "</b><br>" +
                "Code: " + course.code + "</html>");

           
            String seatsText = "<html>Seats: " + course.availableSeats + "/" + 
                             (course.availableSeats + course.registered) + "<br>" +
                             (course.availableSeats > 0 ? 
                              "<font color='green'>Available</font>" : 
                              "<font color='red'>Full</font>") + "</html>";
            
            JLabel seatsLabel = new JLabel(seatsText);
            seatsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            card.add(courseLabel, BorderLayout.NORTH);
            card.add(seatsLabel, BorderLayout.SOUTH);
            seatsGridPanel.add(card);
        }
        
        seatsGridPanel.revalidate();
        seatsGridPanel.repaint();
    }

    private static class CourseAvailability {
        String code;
        String name;
        int availableSeats;
        int registered;

        public CourseAvailability(String code, String name, int availableSeats, int registered) {
            this.code = code;
            this.name = name;
            this.availableSeats = availableSeats;
            this.registered = registered;
        }
    }
}