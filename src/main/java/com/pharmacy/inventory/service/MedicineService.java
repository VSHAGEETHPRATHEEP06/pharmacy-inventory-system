package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Medicine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicineService {
    List<Medicine> getAllMedicines();
    Optional<Medicine> getMedicineById(UUID id);
    List<Medicine> findMedicinesByName(String name);
    List<Medicine> findMedicinesByCategory(String category);
    List<Medicine> findMedicinesByManufacturer(String manufacturer);
    List<Medicine> findMedicinesExpiringInThreeMonths();
    Medicine addMedicine(Medicine medicine);
    Medicine updateMedicine(UUID id, Medicine medicine);
    void deleteMedicine(UUID id);
}
