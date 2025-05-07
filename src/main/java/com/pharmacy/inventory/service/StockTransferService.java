package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.model.StockTransfer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for stock transfer operations
 */
public interface StockTransferService {
    /**
     * Get all stock transfers
     * 
     * @return List of all stock transfers
     */
    List<StockTransfer> getAllStockTransfers();
    
    /**
     * Get stock transfer by ID
     * 
     * @param id Stock transfer ID
     * @return Optional containing stock transfer if found
     */
    Optional<StockTransfer> getStockTransferById(UUID id);
    
    /**
     * Get stock transfers by source branch ID
     * 
     * @param branchId Source branch ID
     * @return List of stock transfers from the specified branch
     */
    List<StockTransfer> getStockTransfersBySourceBranchId(UUID branchId);
    
    /**
     * Get stock transfers by destination branch ID
     * 
     * @param branchId Destination branch ID
     * @return List of stock transfers to the specified branch
     */
    List<StockTransfer> getStockTransfersByDestinationBranchId(UUID branchId);
    
    /**
     * Create a new stock transfer
     * 
     * @param request StockTransferRequest containing transfer details
     * @return Created StockTransfer entity
     */
    StockTransfer createStockTransfer(StockTransferRequest request);
    
    /**
     * Update an existing stock transfer
     * 
     * @param id Stock transfer ID
     * @param request Updated stock transfer details
     * @return Updated StockTransfer entity
     */
    StockTransfer updateStockTransfer(UUID id, StockTransferRequest request);
    
    /**
     * Delete a stock transfer
     * 
     * @param id Stock transfer ID
     */
    void deleteStockTransfer(UUID id);
    
    /**
     * Process a stock transfer by updating its status
     * 
     * @param id Stock transfer ID
     * @param status New status for the stock transfer (e.g., "COMPLETED", "REJECTED")
     * @return Updated StockTransfer entity
     */
    StockTransfer processStockTransfer(UUID id, String status);
}
