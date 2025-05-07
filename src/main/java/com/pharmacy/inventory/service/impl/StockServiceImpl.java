package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Stock;
import com.pharmacy.inventory.repository.BatchRepository;
import com.pharmacy.inventory.repository.BranchRepository;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.StockRepository;
import com.pharmacy.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MedicineRepository medicineRepository;
    private final BatchRepository batchRepository;
    private final BranchRepository branchRepository;
    
    @Autowired
    public StockServiceImpl(StockRepository stockRepository, MedicineRepository medicineRepository, 
                           BatchRepository batchRepository, BranchRepository branchRepository) {
        this.stockRepository = stockRepository;
        this.medicineRepository = medicineRepository;
        this.batchRepository = batchRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<Stock> getStockById(UUID id) {
        return stockRepository.findById(id);
    }

    @Override
    public List<Stock> getStockByMedicineId(UUID medicineId) {
        Optional<Medicine> medicine = medicineRepository.findById(medicineId);
        return medicine.map(stockRepository::findByMedicine).orElse(List.of());
    }

    @Override
    public Optional<Stock> getStockByBatchId(UUID batchId) {
        Optional<Batch> batch = batchRepository.findById(batchId);
        return batch.flatMap(stockRepository::findByBatch);
    }

    @Override
    public List<Stock> getStockByBranchId(UUID branchId) {
        return stockRepository.findByBranchId(branchId);
    }

    @Override
    public List<Stock> getLowStockItems() {
        return stockRepository.findLowStockItems();
    }

    @Override
    public List<Stock> getStockByMedicineCategory(String category) {
        return stockRepository.findStockByMedicineCategory(category);
    }

    @Override
    public List<Stock> getStockExpiringInThreeMonths() {
        return stockRepository.findStockExpiringInThreeMonths();
    }

    @Override
    @Transactional
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    @Transactional
    public Stock updateStock(UUID id, Stock updatedStock) {
        Optional<Stock> existingStock = stockRepository.findById(id);
        
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setMedicine(updatedStock.getMedicine());
            stock.setBatch(updatedStock.getBatch());
            stock.setCurrentQuantity(updatedStock.getCurrentQuantity());
            
            return stockRepository.save(stock);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deleteStock(UUID id) {
        stockRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean updateStockQuantity(UUID stockId, int quantity) {
        Optional<Stock> stockOptional = stockRepository.findById(stockId);
        
        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            stock.setCurrentQuantity(quantity);
            stockRepository.save(stock);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public boolean transferStock(StockTransferRequest transferRequest) {
        try {
            // Verify both branches exist
            Optional<Branch> fromBranchOpt = branchRepository.findById(transferRequest.getFromBranchId());
            Optional<Branch> toBranchOpt = branchRepository.findById(transferRequest.getToBranchId());
            Optional<Medicine> medicineOpt = medicineRepository.findById(transferRequest.getMedicineId());
            
            if (fromBranchOpt.isEmpty() || toBranchOpt.isEmpty() || medicineOpt.isEmpty()) {
                return false;
            }
            
            Branch fromBranch = fromBranchOpt.get();
            Branch toBranch = toBranchOpt.get();
            Medicine medicine = medicineOpt.get();
            int quantityToTransfer = transferRequest.getQuantity();
            
            // Find stock at source branch with enough quantity
            List<Stock> sourceStocks = stockRepository.findByBranchIdAndCurrentQuantityGreaterThan(
                    fromBranch.getId(), 0);
                    
            int availableQuantity = 0;
            for (Stock stock : sourceStocks) {
                if (stock.getMedicine().getId().equals(medicine.getId())) {
                    availableQuantity += stock.getCurrentQuantity();
                }
            }
            
            if (availableQuantity < quantityToTransfer) {
                return false; // Not enough stock available
            }
            
            // Find or create stock at destination branch
            List<Stock> destinationStocks = stockRepository.findByBranchId(toBranch.getId());
            Stock destinationStock = null;
            
            // Look for existing stock of this medicine at destination
            for (Stock stock : destinationStocks) {
                if (stock.getMedicine().getId().equals(medicine.getId())) {
                    destinationStock = stock;
                    break;
                }
            }
            
            int remainingToTransfer = quantityToTransfer;
            
            // Reduce stock at source branch
            for (Stock sourceStock : sourceStocks) {
                if (sourceStock.getMedicine().getId().equals(medicine.getId()) && remainingToTransfer > 0) {
                    int currentQuantity = sourceStock.getCurrentQuantity();
                    int transferFromThisStock = Math.min(currentQuantity, remainingToTransfer);
                    
                    sourceStock.setCurrentQuantity(currentQuantity - transferFromThisStock);
                    stockRepository.save(sourceStock);
                    
                    remainingToTransfer -= transferFromThisStock;
                    
                    // Use the same batch for destination if no existing stock found
                    if (destinationStock == null) {
                        destinationStock = new Stock();
                        destinationStock.setMedicine(medicine);
                        destinationStock.setBatch(sourceStock.getBatch());
                        destinationStock.setBranch(toBranch);
                        destinationStock.setCurrentQuantity(transferFromThisStock);
                        stockRepository.save(destinationStock);
                    } else {
                        // Add to existing stock at destination
                        destinationStock.setCurrentQuantity(destinationStock.getCurrentQuantity() + transferFromThisStock);
                        stockRepository.save(destinationStock);
                    }
                    
                    if (remainingToTransfer == 0) {
                        break;
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}
