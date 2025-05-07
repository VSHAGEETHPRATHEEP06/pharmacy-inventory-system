package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.service.BatchService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/batches")
@Tag(name = "Batch Management", description = "APIs for managing medicine batches in the pharmacy inventory")
public class BatchController {

    private final BatchService batchService;
    
    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    @Operation(summary = "Get all batches", description = "Retrieves a list of all medicine batches in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get batch by ID", description = "Retrieves batch details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Batch> getBatchById(@PathVariable UUID id) {
        return batchService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/batch-number/{batchNumber}")
    @Operation(summary = "Get batch by batch number", description = "Retrieves batch details by its batch number")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Batch> getBatchByBatchNumber(@PathVariable String batchNumber) {
        return batchService.getBatchByBatchNumber(batchNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    @Operation(summary = "Get batches by medicine ID", description = "Retrieves all batches for a specific medicine")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Batch>> getBatchesByMedicineId(@PathVariable UUID medicineId) {
        return ResponseEntity.ok(batchService.getBatchesByMedicineId(medicineId));
    }

    @GetMapping("/expiring-before")
    @Operation(summary = "Find batches expiring before a date", description = "Retrieves all batches that will expire before the specified date")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Batch>> findBatchesExpiringBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(batchService.findBatchesExpiringBefore(date));
    }

    @GetMapping("/expiring-in-one-month")
    @Operation(summary = "Find batches expiring in one month", description = "Retrieves all batches that will expire within the next month")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Batch>> findBatchesExpiringInOneMonth() {
        return ResponseEntity.ok(batchService.findBatchesExpiringInOneMonth());
    }

    @GetMapping("/expiring-in-three-months")
    @Operation(summary = "Find batches expiring in three months", description = "Retrieves all batches that will expire within the next three months")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Batch>> findBatchesExpiringInThreeMonths() {
        return ResponseEntity.ok(batchService.findBatchesExpiringInThreeMonths());
    }

    @PostMapping
    @Operation(summary = "Add a new batch", description = "Creates a new medicine batch in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<Batch> addBatch(@Valid @RequestBody Batch batch) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.addBatch(batch));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update batch", description = "Updates details of an existing batch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updateBatch(@PathVariable UUID id, @Valid @RequestBody Batch batch) {
        Batch updatedBatch = batchService.updateBatch(id, batch);
        if (updatedBatch != null) {
            return ResponseEntity.ok(updatedBatch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Batch not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete batch", description = "Removes a batch from the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBatch(@PathVariable UUID id) {
        try {
            batchService.deleteBatch(id);
            return ResponseEntity.ok(new MessageResponse("Batch deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete batch: " + e.getMessage()));
        }
    }
}
