package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.StockTransfer;
import com.pharmacy.inventory.service.StockTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing stock transfers between branches
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/stock-transfers")
@Tag(name = "Stock Transfer Management", description = "APIs for managing stock transfers between branches")
public class StockTransferController {

    private final StockTransferService stockTransferService;

    @Autowired
    public StockTransferController(StockTransferService stockTransferService) {
        this.stockTransferService = stockTransferService;
    }

    @GetMapping
    @Operation(summary = "Get all stock transfers", description = "Retrieves a list of all stock transfers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<StockTransfer>> getAllStockTransfers() {
        return ResponseEntity.ok(stockTransferService.getAllStockTransfers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock transfer by ID", description = "Retrieves stock transfer details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'MANAGER', 'ADMIN')")
    public ResponseEntity<StockTransfer> getStockTransferById(@PathVariable UUID id) {
        return stockTransferService.getStockTransferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a stock transfer", description = "Creates a new stock transfer between branches")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> createStockTransfer(@Valid @RequestBody StockTransferRequest stockTransferRequest) {
        try {
            StockTransfer stockTransfer = stockTransferService.createStockTransfer(stockTransferRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(stockTransfer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to create stock transfer: " + e.getMessage()));
        }
    }

    @GetMapping("/from-branch/{branchId}")
    @Operation(summary = "Get stock transfers by source branch", description = "Retrieves all stock transfers from a specific branch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<StockTransfer>> getStockTransfersBySourceBranch(@PathVariable UUID branchId) {
        List<StockTransfer> transfers = stockTransferService.getStockTransfersBySourceBranchId(branchId);
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/to-branch/{branchId}")
    @Operation(summary = "Get stock transfers by destination branch", description = "Retrieves all stock transfers to a specific branch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<StockTransfer>> getStockTransfersByDestinationBranch(@PathVariable UUID branchId) {
        List<StockTransfer> transfers = stockTransferService.getStockTransfersByDestinationBranchId(branchId);
        return ResponseEntity.ok(transfers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a stock transfer", description = "Updates an existing stock transfer by its ID")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateStockTransfer(@PathVariable UUID id, @Valid @RequestBody StockTransferRequest stockTransferRequest) {
        try {
            StockTransfer updatedStockTransfer = stockTransferService.updateStockTransfer(id, stockTransferRequest);
            return ResponseEntity.ok(updatedStockTransfer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update stock transfer: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stock transfer", description = "Deletes a stock transfer by its ID")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStockTransfer(@PathVariable UUID id) {
        try {
            stockTransferService.deleteStockTransfer(id);
            return ResponseEntity.ok(new MessageResponse("Stock transfer deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete stock transfer: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/process")
    @Operation(summary = "Process a stock transfer", description = "Updates the status of a stock transfer to either COMPLETED or REJECTED")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> processStockTransfer(@PathVariable UUID id, @RequestParam String status) {
        try {
            StockTransfer processedTransfer = stockTransferService.processStockTransfer(id, status);
            return ResponseEntity.ok(processedTransfer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to process stock transfer: " + e.getMessage()));
        }
    }
}
