//
//package com.traffic.dto;
//
//import java.time.LocalDateTime;
//
///**
// * Data Transfer Object for vehicle detection results
// */
//public class VehicleDTO {
//
//    private String vehicleType;
//    private String numberPlate;
//    private int vehicleCount = 1; // Default 1 vehicle per detection
//    private LocalDateTime timestamp;
//    private String location;
//
//    // --- Getters & Setters ---
//
//    public String getVehicleType() {
//        return vehicleType;
//    }
//
//    public void setVehicleType(String vehicleType) {
//        this.vehicleType = vehicleType;
//    }
//
//    public String getNumberPlate() {
//        return numberPlate;
//    }
//
//    public void setNumberPlate(String numberPlate) {
//        this.numberPlate = numberPlate;
//    }
//
//    public int getVehicleCount() {
//        return vehicleCount;
//    }
//
//    public void setVehicleCount(int vehicleCount) {
//        this.vehicleCount = vehicleCount;
//    }
//
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    // --- Optional: Debug-friendly toString ---
//    @Override
//    public String toString() {
//        return "VehicleDTO{" +
//                "vehicleType='" + vehicleType + '\'' +
//                ", numberPlate='" + numberPlate + '\'' +
//                ", vehicleCount=" + vehicleCount +
//                ", timestamp=" + timestamp +
//                ", location='" + location + '\'' +
//                '}';
//    }
//}
//

package com.traffic.dto;

import java.time.LocalDateTime;

public class VehicleDTO {

    private String vehicleType;
    private String numberPlate;
    private LocalDateTime timestamp;
    private String location;

    // Getters and Setters

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

