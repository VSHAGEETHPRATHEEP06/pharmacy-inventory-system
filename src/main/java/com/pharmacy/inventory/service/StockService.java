package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.model.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockService {
    List<Stock> getAllStock();
    Optional<Stock> getStockById(UUID id);
    List<Stock> getStockByMedicineId(UUID medicineId);
    Optional<Stock> getStockByBatchId(UUID batchId);
    List<Stock> getLowStockItems();
    List<Stock> getStockByMedicineCategory(String category);
    List<Stock> getStockExpiringInThreeMonths();
    Stock addStock(Stock stock);
    Stock updateStock(UUID id, Stock stock);
    void deleteStock(UUID id);
    boolean updateStockQuantity(UUID stockId, int quantity);
    
    // New methods for stock transfer and branch-specific stock operations
    List<Stock> getStockByBranchId(UUID branchId);
    boolean transferStock(StockTransferRequest transferRequest);
}
