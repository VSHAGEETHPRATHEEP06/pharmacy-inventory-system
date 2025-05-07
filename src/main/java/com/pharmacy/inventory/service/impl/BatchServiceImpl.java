package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.repository.BatchRepository;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final MedicineRepository medicineRepository;

    @Autowired
    public BatchServiceImpl(BatchRepository batchRepository, MedicineRepository medicineRepository) {
        this.batchRepository = batchRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    @Override
    public Optional<Batch> getBatchById(UUID id) {
        return batchRepository.findById(id);
    }

    @Override
    public Optional<Batch> getBatchByBatchNumber(String batchNumber) {
        return batchRepository.findByBatchNumber(batchNumber);
    }

    @Override
    public List<Batch> getBatchesByMedicineId(UUID medicineId) {
        Optional<Medicine> medicine = medicineRepository.findById(medicineId);
        return medicine.map(batchRepository::findByMedicine).orElse(List.of());
    }

    @Override
    public List<Batch> findBatchesExpiringBefore(LocalDate date) {
        return batchRepository.findByExpiryDateBefore(date);
    }

    @Override
    public List<Batch> findBatchesExpiringInOneMonth() {
        return batchRepository.findBatchesExpiringInOneMonth();
    }

    @Override
    public List<Batch> findBatchesExpiringInThreeMonths() {
        return batchRepository.findBatchesExpiringInThreeMonths();
    }

    @Override
    @Transactional
    public Batch addBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    @Override
    @Transactional
    public Batch updateBatch(UUID id, Batch updatedBatch) {
        Optional<Batch> existingBatch = batchRepository.findById(id);
        
        if (existingBatch.isPresent()) {
            Batch batch = existingBatch.get();
            batch.setBatchNumber(updatedBatch.getBatchNumber());
            batch.setMedicine(updatedBatch.getMedicine());
            batch.setManufactureDate(updatedBatch.getManufactureDate());
            batch.setExpiryDate(updatedBatch.getExpiryDate());
            batch.setQuantity(updatedBatch.getQuantity());
            
            return batchRepository.save(batch);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deleteBatch(UUID id) {
        batchRepository.deleteById(id);
    }
}
