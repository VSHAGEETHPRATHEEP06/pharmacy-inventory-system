package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Sale;
import com.pharmacy.inventory.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
    List<SaleItem> findBySale(Sale sale);
    List<SaleItem> findByMedicine(Medicine medicine);
    
    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.medicine.id = :medicineId")
    Integer getTotalSoldQuantityByMedicineId(UUID medicineId);
    
    @Query("SELECT si.medicine.id, SUM(si.quantity) as totalSold FROM SaleItem si " +
           "JOIN si.sale s WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY si.medicine.id ORDER BY totalSold DESC")
    List<Object[]> findTopSellingMedicines(
            LocalDateTime startDate, 
            LocalDateTime endDate);
}
