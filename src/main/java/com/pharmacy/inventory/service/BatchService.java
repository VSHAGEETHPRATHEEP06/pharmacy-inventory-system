package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Batch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchService {
    List<Batch> getAllBatches();
    Optional<Batch> getBatchById(UUID id);
    Optional<Batch> getBatchByBatchNumber(String batchNumber);
    List<Batch> getBatchesByMedicineId(UUID medicineId);
    List<Batch> findBatchesExpiringBefore(LocalDate date);
    List<Batch> findBatchesExpiringInOneMonth();
    List<Batch> findBatchesExpiringInThreeMonths();
    Batch addBatch(Batch batch);
    Batch updateBatch(UUID id, Batch batch);
    void deleteBatch(UUID id);
}
