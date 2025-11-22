//
//
//package com.traffic.model;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "traffic_data")
//public class VehicleData {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "location")
//    private String location;
//
//    @Column(name = "timestamp")
//    private LocalDateTime timestamp;
//
//    @Column(name = "vehicle_type")
//    private String vehicleType;
//
//    @Column(name = "number_plate")
//    private String numberPlate;
//
//    // --- Getters and setters ---
//
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getLocation() { return location; }
//    public void setLocation(String location) { this.location = location; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
//
//    public String getVehicleType() { return vehicleType; }
//    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
//
//    public String getNumberPlate() { return numberPlate; }
//    public void setNumberPlate(String numberPlate) { this.numberPlate = numberPlate; }
//}
//
//

package com.traffic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_data")
public class VehicleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vehicleType;
    private String numberPlate;
    private LocalDateTime timestamp;
    private String location;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

