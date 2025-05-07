package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    
    List<Prescription> findByPatientNameContainingIgnoreCase(String patientName);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId")
    List<Prescription> findByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :startDate AND :endDate")
    List<Prescription> findByPrescriptionDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.branch.id = :branchId")
    List<Prescription> findByBranchId(@Param("branchId") UUID branchId);
    
    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :startDate AND :endDate AND p.branch.id = :branchId")
    List<Prescription> findByPrescriptionDateBetweenAndBranchId(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") UUID branchId);
}
