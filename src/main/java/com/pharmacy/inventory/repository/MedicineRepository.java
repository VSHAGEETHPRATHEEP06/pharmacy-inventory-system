package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, UUID> {
    List<Medicine> findByNameContainingIgnoreCase(String name);
    List<Medicine> findByCategory(String category);
    List<Medicine> findByManufacturer(String manufacturer);
    
    @Query("SELECT m FROM Medicine m JOIN Batch b ON m.id = b.medicine.id " +
           "WHERE b.expiryDate <= CURRENT_DATE + 90")
    List<Medicine> findMedicinesExpiringInThreeMonths();
}
