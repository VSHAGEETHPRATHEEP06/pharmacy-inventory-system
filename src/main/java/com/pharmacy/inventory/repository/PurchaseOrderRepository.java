package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.PurchaseOrder;
import com.pharmacy.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findBySupplier(Supplier supplier);
    List<PurchaseOrder> findByStatus(PurchaseOrder.PurchaseStatus status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findOrdersInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
            
    // Multi-branch support
    List<PurchaseOrder> findByBranchId(UUID branchId);
    
    // Methods for reporting
    List<PurchaseOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<PurchaseOrder> findByOrderDateBetweenAndBranchId(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            UUID branchId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId AND po.orderDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findBySupplierIdAndDateRange(
            @Param("supplierId") UUID supplierId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
            
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.branch.id = :branchId")
    List<PurchaseOrder> findByStatusAndBranchId(
            @Param("status") PurchaseOrder.PurchaseStatus status,
            @Param("branchId") UUID branchId);
            
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.branch.id = :branchId AND po.status = :status")
    Long countByBranchIdAndStatus(
            @Param("branchId") UUID branchId,
            @Param("status") PurchaseOrder.PurchaseStatus status);
}
