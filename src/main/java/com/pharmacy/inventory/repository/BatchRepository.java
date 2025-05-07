package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    List<Batch> findByMedicine(Medicine medicine);
    Optional<Batch> findByBatchNumber(String batchNumber);
    List<Batch> findByExpiryDateBefore(LocalDate date);
    
    @Query("SELECT b FROM Batch b WHERE b.expiryDate <= CURRENT_DATE + 30")
    List<Batch> findBatchesExpiringInOneMonth();
    
    @Query("SELECT b FROM Batch b WHERE b.expiryDate <= CURRENT_DATE + 90")
    List<Batch> findBatchesExpiringInThreeMonths();
    
    List<Batch> findByExpiryDateLessThanEqual(LocalDate date);
    
    @Query("SELECT b FROM Batch b JOIN Stock s ON b.id = s.batch.id WHERE b.expiryDate <= :expiryDate AND s.branch.id = :branchId")
    List<Batch> findExpiringBatchesByBranchId(@Param("expiryDate") LocalDate expiryDate, @Param("branchId") UUID branchId);
    
    @Query("SELECT b FROM Batch b WHERE b.expiryDate BETWEEN :startDate AND :endDate")
    List<Batch> findByExpiryDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
