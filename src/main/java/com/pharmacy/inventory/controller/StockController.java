package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.request.StockTransferRequest;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Stock;
import com.pharmacy.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock Management", description = "APIs for managing stock levels in the pharmacy inventory")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @Operation(summary = "Get all stock", description = "Retrieves a list of all stock in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Stock>> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStock());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock by ID", description = "Retrieves stock details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Stock> getStockById(@PathVariable UUID id) {
        return stockService.getStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    @Operation(summary = "Get stock by medicine ID", description = "Retrieves all stock for a specific medicine")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Stock>> getStockByMedicineId(@PathVariable UUID medicineId) {
        return ResponseEntity.ok(stockService.getStockByMedicineId(medicineId));
    }

    @GetMapping("/batch/{batchId}")
    @Operation(summary = "Get stock by batch ID", description = "Retrieves stock for a specific batch")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Stock> getStockByBatchId(@PathVariable UUID batchId) {
        return stockService.getStockByBatchId(batchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get stock by branch ID", description = "Retrieves stock for a specific branch")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Stock>> getStockByBranchId(@PathVariable UUID branchId) {
        return ResponseEntity.ok(stockService.getStockByBranchId(branchId));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items", description = "Retrieves all items with low stock levels")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Stock>> getLowStockItems() {
        return ResponseEntity.ok(stockService.getLowStockItems());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get stock by medicine category", description = "Retrieves all stock for medicines in a specific category")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Stock>> getStockByMedicineCategory(@PathVariable String category) {
        return ResponseEntity.ok(stockService.getStockByMedicineCategory(category));
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "Get stock expiring soon", description = "Retrieves all stock that will expire within three months")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Stock>> getStockExpiringInThreeMonths() {
        return ResponseEntity.ok(stockService.getStockExpiringInThreeMonths());
    }

    @PostMapping
    @Operation(summary = "Add new stock", description = "Creates a new stock entry in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<Stock> addStock(@Valid @RequestBody Stock stock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.addStock(stock));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer stock between branches", description = "Transfers a specified quantity of medicine stock from one branch to another")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> transferStock(@Valid @RequestBody StockTransferRequest transferRequest) {
        boolean transferred = stockService.transferStock(transferRequest);
        if (transferred) {
            return ResponseEntity.ok(new MessageResponse("Stock transferred successfully"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Failed to transfer stock. Check if source has enough stock or if branches and medicine exist."));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update stock", description = "Updates details of an existing stock entry")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@PathVariable UUID id, @Valid @RequestBody Stock stock) {
        Stock updatedStock = stockService.updateStock(id, stock);
        if (updatedStock != null) {
            return ResponseEntity.ok(updatedStock);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Stock not found with ID: " + id));
    }

    @PatchMapping("/{id}/quantity/{quantity}")
    @Operation(summary = "Update stock quantity", description = "Updates only the quantity of an existing stock entry")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStockQuantity(@PathVariable UUID id, @PathVariable int quantity) {
        boolean updated = stockService.updateStockQuantity(id, quantity);
        if (updated) {
            return ResponseEntity.ok(new MessageResponse("Stock quantity updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Stock not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stock", description = "Removes a stock entry from the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStock(@PathVariable UUID id) {
        try {
            stockService.deleteStock(id);
            return ResponseEntity.ok(new MessageResponse("Stock deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete stock: " + e.getMessage()));
        }
    }
}
