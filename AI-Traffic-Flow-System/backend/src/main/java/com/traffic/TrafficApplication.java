package com.traffic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.traffic.ui.TrafficDashboard;

import javax.swing.*;

@SpringBootApplication
public class TrafficApplication {

    public static void main(String[] args) {
        // Start Spring Boot backend
        ConfigurableApplicationContext context = SpringApplication.run(TrafficApplication.class, args);
        System.out.println("✅ Spring Boot backend started...");

        // Start Swing UI
        SwingUtilities.invokeLater(() -> {
            TrafficDashboard dashboard = new TrafficDashboard();
            dashboard.setVisible(true);
        });
        System.out.println("Headless? " + java.awt.GraphicsEnvironment.isHeadless());

    }
}
