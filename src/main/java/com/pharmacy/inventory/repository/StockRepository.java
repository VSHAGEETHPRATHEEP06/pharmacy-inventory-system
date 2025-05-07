package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    List<Stock> findByMedicine(Medicine medicine);
    Optional<Stock> findByBatch(Batch batch);
    
    @Query("SELECT s FROM Stock s WHERE s.currentQuantity <= 10")
    List<Stock> findLowStockItems();
    
    @Query("SELECT s FROM Stock s JOIN s.medicine m WHERE m.category = :category")
    List<Stock> findStockByMedicineCategory(String category);
    
    @Query("SELECT s FROM Stock s JOIN s.batch b JOIN s.medicine m " +
           "WHERE b.expiryDate <= CURRENT_DATE + 90 ORDER BY b.expiryDate ASC")
    List<Stock> findStockExpiringInThreeMonths();
    
    // Branch-specific queries
    List<Stock> findByBranchId(UUID branchId);
    
    List<Stock> findByBranchIdAndCurrentQuantityGreaterThan(UUID branchId, Integer quantity);
    
    List<Stock> findByCurrentQuantityGreaterThan(Integer quantity);
    
    @Query("SELECT s FROM Stock s WHERE s.currentQuantity <= 10 AND s.branch.id = :branchId")
    List<Stock> findLowStockItemsByBranch(@Param("branchId") UUID branchId);
    
    @Query("SELECT s FROM Stock s JOIN s.medicine m WHERE UPPER(m.name) LIKE UPPER(CONCAT('%', :keyword, '%')) AND s.branch.id = :branchId")
    List<Stock> findByMedicineNameContainingAndBranch(@Param("keyword") String keyword, @Param("branchId") UUID branchId);
}
