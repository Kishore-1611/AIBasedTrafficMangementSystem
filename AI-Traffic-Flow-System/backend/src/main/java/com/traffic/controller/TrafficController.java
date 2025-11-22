//
//package com.traffic.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import com.traffic.service.TrafficService;
//import com.traffic.model.VehicleData;
//import com.traffic.dto.VehicleDTO;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/traffic")
//@CrossOrigin
//public class TrafficController {
//
//    @Autowired
//    private TrafficService trafficService;
//
//    @GetMapping("/ping")
//    public String ping() {
//        return "✅ Server is up and running!";
//    }
//
//    @PostMapping("/save")
//    public String saveTrafficData(@RequestParam int count,
//                                   @RequestParam String location) {
//        trafficService.saveTrafficData(count, location);
//        return "✅ Traffic data saved: " + count + " vehicles at " + location;
//    }
//
//    @PostMapping("/save/details")
//    public String saveDetailedVehicleData(@RequestBody List<VehicleDTO> vehicles,
//                                          @RequestParam String location) {
//        System.out.println("🔍 Received " + vehicles.size() + " vehicle DTOs at: " + location);
//        for (VehicleDTO dto : vehicles) {
//            System.out.println("➡ " + dto.getVehicleType() + ", " + dto.getNumberPlate() +
//                    ", " + dto.getVehicleCount() + ", " + dto.getTimestamp());
//        }
//
//        trafficService.saveDetailedVehicleList(vehicles, location);
//        return "✅ Saved " + vehicles.size() + " detailed vehicle entries.";
//    }
//
//    @GetMapping("/latest")
//    public int getLatestTrafficData() {
//        return trafficService.getLatestVehicleCount();
//    }
//
//    @GetMapping("/history")
//    public List<VehicleData> getTrafficHistory(@RequestParam String location,
//                                               @RequestParam String date) {
//        LocalDate parsedDate = LocalDate.parse(date);
//        return trafficService.getTrafficHistory(location, parsedDate);
//    }
//
//    @GetMapping("/details")
//    public List<VehicleData> getDetailedHistory(@RequestParam String location,
//                                                @RequestParam String date) {
//        LocalDate parsedDate = LocalDate.parse(date);
//        return trafficService.getDetailedVehicleHistory(location, parsedDate);
//    }
//}
//

package com.traffic.controller;

import com.traffic.dto.VehicleDTO;
import com.traffic.model.VehicleData;
import com.traffic.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@CrossOrigin(origins = "*")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @PostMapping("/save/details")
    public String saveVehicleDetails(@RequestBody List<VehicleDTO> vehicleDTOList) {
        trafficService.saveVehicleDetails(vehicleDTOList);
        return "Vehicle data saved successfully!";
    }

    @GetMapping("/details")
    public List<VehicleData> getVehicleDetails(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return trafficService.getVehicleDetails(location, date);
    }
} 

