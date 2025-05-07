package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.model.Sale;
import com.pharmacy.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByUser(User user);
    
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findSalesInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Alias for findSalesInDateRange to match method call in ReportServiceImpl
    default List<Sale> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return findSalesInDateRange(startDate, endDate);
    }
            
    @Query("SELECT s FROM Sale s WHERE s.branch.id = :branchId")
    List<Sale> findByBranchId(@Param("branchId") UUID branchId);
    
    @Query("SELECT s FROM Sale s WHERE s.user.id = :userId")
    List<Sale> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Double calculateTotalSalesAmount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Method needed by SaleServiceImpl
    default Double getSalesTotalInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return calculateTotalSalesAmount(startDate, endDate);
    }
    
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Long countSalesInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Sale s WHERE s.branch.id = :branchId AND s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findByBranchAndDateRange(
            @Param("branchId") UUID branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Alias for findByBranchAndDateRange to match method call in ReportServiceImpl
    default List<Sale> findByCreatedAtBetweenAndBranchId(LocalDateTime startDate, LocalDateTime endDate, UUID branchId) {
        return findByBranchAndDateRange(branchId, startDate, endDate);
    }
    
    @Query("SELECT s FROM Sale s WHERE UPPER(s.customerName) LIKE UPPER(CONCAT('%', :customerName, '%'))")
    List<Sale> findByCustomerNameContainingIgnoreCase(@Param("customerName") String customerName);
}
