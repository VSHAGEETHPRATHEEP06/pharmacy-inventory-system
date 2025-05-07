package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.PurchaseOrder;
import com.pharmacy.inventory.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/purchase-orders")
@Tag(name = "Purchase Order Management", description = "APIs for managing purchase orders in the pharmacy inventory")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    
    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    @Operation(summary = "Get all purchase orders", description = "Retrieves a list of all purchase orders")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID", description = "Retrieves purchase order details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable UUID id) {
        return purchaseOrderService.getPurchaseOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/supplier/{supplierId}")
    @Operation(summary = "Get purchase orders by supplier", description = "Retrieves all purchase orders for a specific supplier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersBySupplier(@PathVariable UUID supplierId) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersBySupplier(supplierId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get purchase orders by status", description = "Retrieves all purchase orders with a specific status")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersByStatus(
            @PathVariable PurchaseOrder.PurchaseStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersByStatus(status));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get purchase orders in date range", description = "Retrieves all purchase orders within a specified date range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrdersInDateRange(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "Create a new purchase order", description = "Creates a new purchase order in the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@Valid @RequestBody PurchaseOrder purchaseOrder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createPurchaseOrder(purchaseOrder));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update purchase order", description = "Updates details of an existing purchase order")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePurchaseOrder(@PathVariable UUID id, @Valid @RequestBody PurchaseOrder purchaseOrder) {
        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id, purchaseOrder);
        if (updatedPurchaseOrder != null) {
            return ResponseEntity.ok(updatedPurchaseOrder);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Purchase order not found with ID: " + id));
    }

    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Update purchase order status", description = "Updates only the status of an existing purchase order")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePurchaseOrderStatus(
            @PathVariable UUID id, 
            @PathVariable PurchaseOrder.PurchaseStatus status) {
        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrderStatus(id, status);
        if (updatedPurchaseOrder != null) {
            return ResponseEntity.ok(updatedPurchaseOrder);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Purchase order not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete purchase order", description = "Removes a purchase order from the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable UUID id) {
        try {
            purchaseOrderService.deletePurchaseOrder(id);
            return ResponseEntity.ok(new MessageResponse("Purchase order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete purchase order: " + e.getMessage()));
        }
    }
}
