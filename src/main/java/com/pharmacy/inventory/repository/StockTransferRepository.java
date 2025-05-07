package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for StockTransfer entity
 */
@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID> {
    /**
     * Find all stock transfers from a specific branch
     * 
     * @param branchId Source branch ID
     * @return List of stock transfers
     */
    List<StockTransfer> findByFromBranchId(UUID branchId);
    
    /**
     * Find all stock transfers to a specific branch
     * 
     * @param branchId Destination branch ID
     * @return List of stock transfers
     */
    List<StockTransfer> findByToBranchId(UUID branchId);
    
    /**
     * Find all stock transfers for a specific medicine
     * 
     * @param medicineId Medicine ID
     * @return List of stock transfers
     */
    List<StockTransfer> findByMedicineId(UUID medicineId);
    
    /**
     * Find all stock transfers with a specific status
     * 
     * @param status Transfer status
     * @return List of stock transfers
     */
    List<StockTransfer> findByStatus(StockTransfer.TransferStatus status);
}
