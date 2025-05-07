package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    
    @Autowired
    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Override
    public Optional<Medicine> getMedicineById(UUID id) {
        return medicineRepository.findById(id);
    }

    @Override
    public List<Medicine> findMedicinesByName(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Medicine> findMedicinesByCategory(String category) {
        return medicineRepository.findByCategory(category);
    }

    @Override
    public List<Medicine> findMedicinesByManufacturer(String manufacturer) {
        return medicineRepository.findByManufacturer(manufacturer);
    }
    
    @Override
    public List<Medicine> findMedicinesExpiringInThreeMonths() {
        return medicineRepository.findMedicinesExpiringInThreeMonths();
    }

    @Override
    @Transactional
    public Medicine addMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    @Transactional
    public Medicine updateMedicine(UUID id, Medicine updatedMedicine) {
        Optional<Medicine> existingMedicine = medicineRepository.findById(id);
        
        if (existingMedicine.isPresent()) {
            Medicine medicine = existingMedicine.get();
            medicine.setName(updatedMedicine.getName());
            medicine.setCategory(updatedMedicine.getCategory());
            medicine.setManufacturer(updatedMedicine.getManufacturer());
            medicine.setUnitPrice(updatedMedicine.getUnitPrice());
            medicine.setPrescriptionRequired(updatedMedicine.getPrescriptionRequired());
            
            return medicineRepository.save(medicine);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deleteMedicine(UUID id) {
        medicineRepository.deleteById(id);
    }
}
