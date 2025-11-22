//
//package com.traffic.ui;
//
//import com.traffic.camera.LiveTrafficDetection;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.time.LocalDate;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.util.Vector;
//
//public class TrafficDashboard extends JFrame {
//    private JPanel signalLightPanel;
//    private JLabel vehicleCountLabel;
//    private JLabel statusLabel;
//    private JTextField currentLocationField;  // Editable location under vehicle count
//    private JTextField filterLocationField;   // Filtering input
//    private JTextField dateField;
//    private JTable resultTable;
//    private DefaultTableModel tableModel;
//    private JTextArea plateLogArea;
//    private JLabel videoLabel;
//
//    public TrafficDashboard() {
//        setTitle("AI Based Traffic Flow System");
//        setSize(1150, 730);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        JPanel mainPanel = new JPanel(null);
//
//        JLabel titleLabel = new JLabel("AI BASED TRAFFIC FLOW SYSTEM");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setBounds(300, 10, 500, 30);
//        mainPanel.add(titleLabel);
//
//        // ✅ Traffic signal light panel
//        JLabel signalText = new JLabel("Signal Light");
//        signalText.setBounds(880, 50, 100, 20);
//        mainPanel.add(signalText);
//
//        signalLightPanel = new JPanel();
//        signalLightPanel.setBounds(900, 70, 30, 30);
//        signalLightPanel.setBackground(Color.GREEN);
//        mainPanel.add(signalLightPanel);
//
//        vehicleCountLabel = new JLabel("Vehicle: 0");
//        vehicleCountLabel.setFont(new Font("Arial", Font.BOLD, 20));
//        vehicleCountLabel.setBounds(880, 120, 200, 30);
//        mainPanel.add(vehicleCountLabel);
//
//        JLabel currentLocLabel = new JLabel("Current Location:");
//        currentLocLabel.setBounds(880, 160, 180, 20);
//        mainPanel.add(currentLocLabel);
//
//        currentLocationField = new JTextField();
//        currentLocationField.setBounds(880, 180, 180, 25);
//        mainPanel.add(currentLocationField);
//
//        JLabel filterLocLabel = new JLabel("Location (for Filter):");
//        filterLocLabel.setBounds(880, 210, 180, 20);
//        mainPanel.add(filterLocLabel);
//
//        filterLocationField = new JTextField();
//        filterLocationField.setBounds(880, 230, 180, 25);
//        mainPanel.add(filterLocationField);
//
//        JLabel dateLabel = new JLabel("Date:");
//        dateLabel.setBounds(880, 260, 80, 20);
//        mainPanel.add(dateLabel);
//
//        dateField = new JTextField(LocalDate.now().toString());
//        dateField.setBounds(880, 280, 180, 25);
//        mainPanel.add(dateField);
//
//        JButton submitBtn = new JButton("Submit");
//        submitBtn.setBounds(880, 320, 180, 30);
//        mainPanel.add(submitBtn);
//
//        JButton startButton = new JButton("Start Camera");
//        startButton.setBounds(280, 640, 150, 30);
//        mainPanel.add(startButton);
//
//        JButton stopButton = new JButton("Stop Camera");
//        stopButton.setBounds(450, 640, 150, 30);
//        mainPanel.add(stopButton);
//
//        String[] columns = {"Time", "Vehicle Type", "Number Plate"};
//        tableModel = new DefaultTableModel(columns, 0);
//        resultTable = new JTable(tableModel);
//        JScrollPane scrollPane = new JScrollPane(resultTable);
//        scrollPane.setBounds(10, 370, 830, 230);
//        mainPanel.add(scrollPane);
//
//        videoLabel = new JLabel();
//        videoLabel.setBounds(10, 50, 830, 300);
//        videoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        mainPanel.add(videoLabel);
//
//        JLabel logLabel = new JLabel("Detected Number Plates:");
//        logLabel.setBounds(880, 370, 200, 20);
//        mainPanel.add(logLabel);
//
//        plateLogArea = new JTextArea();
//        plateLogArea.setEditable(false);
//        JScrollPane logScrollPane = new JScrollPane(plateLogArea);
//        logScrollPane.setBounds(880, 390, 230, 190);
//        mainPanel.add(logScrollPane);
//
//        add(mainPanel);
//        detectLocation();
//
//        startButton.addActionListener((ActionEvent e) -> {
//            new Thread(() -> {
//                LiveTrafficDetection.setVehicleCountLabel(vehicleCountLabel);
//                LiveTrafficDetection.setStatusLabel(statusLabel);
//                LiveTrafficDetection.setVideoLabel(videoLabel);
//                LiveTrafficDetection.setPlateLogArea(plateLogArea);
//                LiveTrafficDetection.setDetectedLocation(currentLocationField.getText());
//                LiveTrafficDetection.setSignalPanel(signalLightPanel);
//                LiveTrafficDetection.startCamera();
//            }).start();
//        });
//
//        stopButton.addActionListener(e -> LiveTrafficDetection.stopCamera());
//        submitBtn.addActionListener(e -> fetchHistory());
//
//        setVisible(true);
//    }
//
//    private void detectLocation() {
//        try {
//            URL url = new URL("http://ip-api.com/json");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = in.readLine()) != null) response.append(line);
//            in.close();
//            JSONObject json = new JSONObject(response.toString());
//            String city = json.getString("city");
//            currentLocationField.setText(city);
//            LiveTrafficDetection.setDetectedLocation(city);
//        } catch (Exception e) {
//            currentLocationField.setText("Unknown");
//        }
//    }
//
//    private void fetchHistory() {
//        try {
//            String loc = filterLocationField.getText();
//            String date = dateField.getText();
//            URL url = new URL("http://localhost:8080/api/traffic/details?location=" + loc + "&date=" + date);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            con.setRequestProperty("Accept", "application/json");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) response.append(inputLine);
//            in.close();
//
//            JSONArray jsonArray = new JSONArray(response.toString());
//            tableModel.setRowCount(0);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject obj = jsonArray.getJSONObject(i);
//                Vector<String> row = new Vector<>();
//                row.add(obj.getString("timestamp"));
//                row.add(obj.getString("vehicleType"));
//                row.add(obj.getString("numberPlate"));
//                tableModel.addRow(row);
//            }
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error fetching history: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        new TrafficDashboard();
//    }
//}


package com.traffic.ui;

import com.traffic.camera.LiveTrafficDetection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Vector;

public class TrafficDashboard extends JFrame {
    private JPanel signalLightPanel;
    private JLabel vehicleCountLabel;
    private JLabel statusLabel;
    private JTextField currentLocationField;  // Editable location under vehicle count
    private JTextField filterLocationField;   // Filtering input
    private JTextField dateField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea plateLogArea;
    private JLabel videoLabel;

    public TrafficDashboard() {
        setTitle("AI Based Traffic Flow System");
        setSize(1150, 730);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(null);

        JLabel titleLabel = new JLabel("AI BASED TRAFFIC FLOW SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(300, 10, 500, 30);
        mainPanel.add(titleLabel);

        JLabel signalText = new JLabel("Signal Light");
        signalText.setBounds(880, 50, 100, 20);
        mainPanel.add(signalText);

        signalLightPanel = new JPanel();
        signalLightPanel.setBounds(900, 70, 30, 30);
        signalLightPanel.setBackground(Color.GREEN);
        mainPanel.add(signalLightPanel);

        vehicleCountLabel = new JLabel("Vehicle: 0");
        vehicleCountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        vehicleCountLabel.setBounds(880, 120, 200, 30);
        mainPanel.add(vehicleCountLabel);

        JLabel currentLocLabel = new JLabel("Current Location:");
        currentLocLabel.setBounds(880, 160, 180, 20);
        mainPanel.add(currentLocLabel);

        currentLocationField = new JTextField();
        currentLocationField.setBounds(880, 180, 180, 25);
        mainPanel.add(currentLocationField);

        JLabel filterLocLabel = new JLabel("Location (for Filter):");
        filterLocLabel.setBounds(880, 210, 180, 20);
        mainPanel.add(filterLocLabel);

        filterLocationField = new JTextField();
        filterLocationField.setBounds(880, 230, 180, 25);
        mainPanel.add(filterLocationField);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(880, 260, 80, 20);
        mainPanel.add(dateLabel);

        dateField = new JTextField(LocalDate.now().toString());
        dateField.setBounds(880, 280, 180, 25);
        mainPanel.add(dateField);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBounds(880, 320, 180, 30);
        mainPanel.add(submitBtn);

        JButton startButton = new JButton("Start Camera");
        startButton.setBounds(280, 640, 150, 30);
        mainPanel.add(startButton);

        JButton stopButton = new JButton("Stop Camera");
        stopButton.setBounds(450, 640, 150, 30);
        mainPanel.add(stopButton);

        String[] columns = {"Time", "Vehicle Type", "Number Plate"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(10, 370, 830, 230);
        mainPanel.add(scrollPane);

        videoLabel = new JLabel();
        videoLabel.setBounds(10, 50, 830, 300);
        videoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mainPanel.add(videoLabel);

        JLabel logLabel = new JLabel("Detected Number Plates:");
        logLabel.setBounds(880, 370, 200, 20);
        mainPanel.add(logLabel);

        plateLogArea = new JTextArea();
        plateLogArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(plateLogArea);
        logScrollPane.setBounds(880, 390, 230, 190);
        mainPanel.add(logScrollPane);

        add(mainPanel);
        detectLocation();

        startButton.addActionListener((ActionEvent e) -> {
            new Thread(() -> {
                LiveTrafficDetection.setVehicleCountLabel(vehicleCountLabel);
                LiveTrafficDetection.setStatusLabel(statusLabel);
                LiveTrafficDetection.setVideoLabel(videoLabel);
                LiveTrafficDetection.setPlateLogArea(plateLogArea);
                LiveTrafficDetection.setDetectedLocation(currentLocationField.getText());
                LiveTrafficDetection.setSignalPanel(signalLightPanel);
                LiveTrafficDetection.startCamera();
            }).start();
        });

        stopButton.addActionListener(e -> LiveTrafficDetection.stopCamera());
        submitBtn.addActionListener(e -> fetchHistory());

        setVisible(true);
    }

    private void detectLocation() {
        SwingUtilities.invokeLater(() -> {
            currentLocationField.setText("Detecting...");
        });

        new Thread(() -> {
            try {
                URL url = new URL("http://ip-api.com/json");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                JSONObject json = new JSONObject(response.toString());
                String city = json.getString("city");

                SwingUtilities.invokeLater(() -> {
                    currentLocationField.setText(city);
                    LiveTrafficDetection.setDetectedLocation(city);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    currentLocationField.setText("Unknown");
                    LiveTrafficDetection.setDetectedLocation("Unknown");
                });
            }
        }).start();
    }

    private void fetchHistory() {
        try {
            String loc = filterLocationField.getText();
            String date = dateField.getText();
            URL url = new URL("http://localhost:8080/api/traffic/details?location=" + loc + "&date=" + date);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            tableModel.setRowCount(0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Vector<String> row = new Vector<>();
                row.add(obj.getString("timestamp"));
                row.add(obj.getString("vehicleType"));
                row.add(obj.getString("numberPlate"));
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching history: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new TrafficDashboard();
    }
}

