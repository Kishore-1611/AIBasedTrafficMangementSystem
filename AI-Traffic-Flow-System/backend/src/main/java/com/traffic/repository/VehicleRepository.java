//package com.traffic.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import com.traffic.model.VehicleData;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface VehicleRepository extends JpaRepository<VehicleData, Long> {
//
//   
//
//    List<VehicleData> findByLocationAndTimestampBetween(
//            String location, LocalDateTime start, LocalDateTime end
//    );
//    Optional<VehicleData> findTopByOrderByIdDesc();
//}

package com.traffic.repository;

import com.traffic.model.VehicleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleData, Long> {

    @Query("SELECT v FROM VehicleData v WHERE v.location = :location AND DATE(v.timestamp) = :date")
    List<VehicleData> findByLocationAndDate(String location, LocalDate date);
}

