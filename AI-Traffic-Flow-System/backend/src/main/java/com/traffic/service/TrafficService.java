//
//
//
//package com.traffic.service;
//
//import com.traffic.dto.VehicleDTO;
//import com.traffic.model.VehicleData;
//import com.traffic.repository.VehicleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import jakarta.transaction.Transactional;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class TrafficService {
//
//    @Autowired
//    private VehicleRepository repository;
//
//    // ❌ Removed vehicleCount save logic
//    public void saveTrafficData(int count, String location) {
//        System.out.println("ℹ️ Received traffic count (" + count + ") for display only. No DB storage.");
//    }
//
//    @Transactional
//    public void saveDetailedVehicleList(List<VehicleDTO> vehicles, String fallbackLocation) {
//        List<VehicleData> entities = vehicles.stream()
//            .filter(dto -> dto.getVehicleType() != null && !dto.getVehicleType().isBlank())
//            .map(dto -> {
//                VehicleData entity = new VehicleData();
//                entity.setVehicleType(dto.getVehicleType());
//                entity.setNumberPlate(dto.getNumberPlate());
//                // ❌ Do not store vehicleCount
//                entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
//                entity.setLocation(fallbackLocation);
//                return entity;
//            })
//            .collect(Collectors.toList());
//
//        if (!entities.isEmpty()) {
//            repository.saveAll(entities);
//            System.out.println("✅ Saved " + entities.size() + " detailed entries at location: " + fallbackLocation);
//            entities.forEach(e -> System.out.println("   ↪ " + e.getVehicleType() + " | " + e.getNumberPlate()));
//        } else {
//            System.out.println("⚠️ Skipped saving: No valid vehicle entries.");
//        }
//    }
//
//    // ❌ Do not return stored vehicle count (since it’s not stored anymore)
//    public int getLatestVehicleCount() {
//        return 0; // or optionally throw UnsupportedOperationException
//    }
//
//    public List<VehicleData> getTrafficHistory(String location, LocalDate date) {
//        LocalDateTime start = date.atStartOfDay();
//        LocalDateTime end = start.plusDays(1);
//        return repository.findByLocationAndTimestampBetween(location, start, end);
//    }
//
//    public List<VehicleData> getDetailedVehicleHistory(String location, LocalDate date) {
//        LocalDateTime start = date.atStartOfDay();
//        LocalDateTime end = start.plusDays(1);
//        return repository.findByLocationAndTimestampBetween(location, start, end);
//    }
//}
//

package com.traffic.service;

import com.traffic.dto.VehicleDTO;
import com.traffic.model.VehicleData;
import com.traffic.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrafficService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public void saveVehicleDetails(List<VehicleDTO> vehicleDTOList) {
        List<VehicleData> vehicleDataList = vehicleDTOList.stream().map(dto -> {
            VehicleData data = new VehicleData();
            data.setVehicleType(dto.getVehicleType());
            data.setNumberPlate(dto.getNumberPlate());
            data.setTimestamp(dto.getTimestamp());
            data.setLocation(dto.getLocation());
            return data;
        }).collect(Collectors.toList());

        vehicleRepository.saveAll(vehicleDataList);
    }

    public List<VehicleData> getVehicleDetails(String location, LocalDate date) {
        return vehicleRepository.findByLocationAndDate(location, date);
    }
}

