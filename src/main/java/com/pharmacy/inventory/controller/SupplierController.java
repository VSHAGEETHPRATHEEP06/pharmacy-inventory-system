package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Supplier;
import com.pharmacy.inventory.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers in the pharmacy inventory")
public class SupplierController {

    private final SupplierService supplierService;
    
    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieves a list of all suppliers")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieves supplier details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable UUID id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers by name", description = "Searches for suppliers whose names contain the provided search term")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Supplier>> searchSuppliersByName(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.findSuppliersByName(name));
    }

    @GetMapping("/rating/{minRating}")
    @Operation(summary = "Find suppliers with minimum rating", description = "Retrieves all suppliers with a rating equal to or above the specified minimum")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Supplier>> findSuppliersWithMinimumRating(@PathVariable BigDecimal minRating) {
        return ResponseEntity.ok(supplierService.findSuppliersWithMinimumRating(minRating));
    }

    @PostMapping
    @Operation(summary = "Add a new supplier", description = "Creates a new supplier in the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Supplier> addSupplier(@Valid @RequestBody Supplier supplier) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.addSupplier(supplier));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Updates details of an existing supplier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> updateSupplier(@PathVariable UUID id, @Valid @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
        if (updatedSupplier != null) {
            return ResponseEntity.ok(updatedSupplier);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Supplier not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Removes a supplier from the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSupplier(@PathVariable UUID id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.ok(new MessageResponse("Supplier deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete supplier: " + e.getMessage()));
        }
    }
}
