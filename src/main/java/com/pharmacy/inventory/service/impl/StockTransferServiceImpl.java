package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.StockTransfer;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.BranchRepository;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.StockTransferRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.StockTransferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of StockTransferService
 */
@Service
public class StockTransferServiceImpl implements StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final BranchRepository branchRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    @Autowired
    public StockTransferServiceImpl(
            StockTransferRepository stockTransferRepository,
            BranchRepository branchRepository,
            MedicineRepository medicineRepository,
            UserRepository userRepository) {
        this.stockTransferRepository = stockTransferRepository;
        this.branchRepository = branchRepository;
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<StockTransfer> getAllStockTransfers() {
        return stockTransferRepository.findAll();
    }

    @Override
    public Optional<StockTransfer> getStockTransferById(UUID id) {
        return stockTransferRepository.findById(id);
    }

    @Override
    public List<StockTransfer> getStockTransfersBySourceBranchId(UUID branchId) {
        return stockTransferRepository.findByFromBranchId(branchId);
    }

    @Override
    public List<StockTransfer> getStockTransfersByDestinationBranchId(UUID branchId) {
        return stockTransferRepository.findByToBranchId(branchId);
    }

    @Override
    @Transactional
    public StockTransfer createStockTransfer(StockTransferRequest request) {
        // Fetch related entities
        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Source branch not found"));
                
        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Destination branch not found"));
                
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found"));
                
        // Get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User requestingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verify source branch has enough stock
        // Implementation would depend on your inventory management logic
        // For simplicity, we'll skip this check here

        // Create new stock transfer
        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setFromBranch(fromBranch);
        stockTransfer.setToBranch(toBranch);
        stockTransfer.setMedicine(medicine);
        stockTransfer.setQuantity(request.getQuantity());
        stockTransfer.setRequestDate(LocalDateTime.now());
        stockTransfer.setStatus(StockTransfer.TransferStatus.PENDING);
        stockTransfer.setRequestedBy(requestingUser);

        return stockTransferRepository.save(stockTransfer);
    }

    @Override
    @Transactional
    public StockTransfer updateStockTransfer(UUID id, StockTransferRequest request) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock transfer not found with ID: " + id));
        
        // Fetch related entities
        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Source branch not found"));
                
        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new EntityNotFoundException("Destination branch not found"));
                
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found"));
        
        // Update stock transfer details
        stockTransfer.setFromBranch(fromBranch);
        stockTransfer.setToBranch(toBranch);
        stockTransfer.setMedicine(medicine);
        stockTransfer.setQuantity(request.getQuantity());
        
        // Only allow updates if transfer is still pending
        if (stockTransfer.getStatus() != StockTransfer.TransferStatus.PENDING) {
            throw new IllegalStateException("Cannot update a processed stock transfer");
        }
        
        return stockTransferRepository.save(stockTransfer);
    }
    
    @Override
    @Transactional
    public void deleteStockTransfer(UUID id) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock transfer not found with ID: " + id));
        
        // Only allow deletion if transfer is still pending
        if (stockTransfer.getStatus() != StockTransfer.TransferStatus.PENDING) {
            throw new IllegalStateException("Cannot delete a processed stock transfer");
        }
        
        stockTransferRepository.delete(stockTransfer);
    }

    @Override
    @Transactional
    public StockTransfer processStockTransfer(UUID id, String status) {
        StockTransfer stockTransfer = stockTransferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock transfer not found"));

        // Check if transfer is already processed
        if (stockTransfer.getStatus() != StockTransfer.TransferStatus.PENDING) {
            throw new IllegalStateException("Stock transfer already processed");
        }

        // Get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User processingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update the transfer status
        if ("COMPLETED".equalsIgnoreCase(status)) {
            stockTransfer.setStatus(StockTransfer.TransferStatus.COMPLETED);
            // Here you would update the inventory levels in both branches
            // Implementation depends on your inventory management logic
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            stockTransfer.setStatus(StockTransfer.TransferStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        stockTransfer.setCompletionDate(LocalDateTime.now());
        stockTransfer.setProcessedBy(processingUser);

        return stockTransferRepository.save(stockTransfer);
    }
}
