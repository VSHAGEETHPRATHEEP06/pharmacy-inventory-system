package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.service.MedicineService;
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
@RequestMapping("/api/medicines")
@Tag(name = "Medicine Management", description = "APIs for managing medicines in the pharmacy inventory")
public class MedicineController {

    private final MedicineService medicineService;
    
    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    @Operation(summary = "Get all medicines", description = "Retrieves a list of all medicines in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieves medicine details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable UUID id) {
        return medicineService.getMedicineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search medicines by name", description = "Searches for medicines whose names contain the provided search term")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Medicine>> searchMedicinesByName(@RequestParam String name) {
        return ResponseEntity.ok(medicineService.findMedicinesByName(name));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Find medicines by category", description = "Retrieves all medicines belonging to a specific category")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Medicine>> findMedicinesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(medicineService.findMedicinesByCategory(category));
    }

    @GetMapping("/manufacturer/{manufacturer}")
    @Operation(summary = "Find medicines by manufacturer", description = "Retrieves all medicines produced by a specific manufacturer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Medicine>> findMedicinesByManufacturer(@PathVariable String manufacturer) {
        return ResponseEntity.ok(medicineService.findMedicinesByManufacturer(manufacturer));
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "Find medicines expiring in 3 months", description = "Retrieves all medicines that will expire within the next 3 months")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Medicine>> findMedicinesExpiringInThreeMonths() {
        return ResponseEntity.ok(medicineService.findMedicinesExpiringInThreeMonths());
    }

    @PostMapping
    @Operation(summary = "Add a new medicine", description = "Creates a new medicine in the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<Medicine> addMedicine(@Valid @RequestBody Medicine medicine) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.addMedicine(medicine));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medicine", description = "Updates details of an existing medicine")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PHARMACIST') or hasRole('ADMIN')")
    public ResponseEntity<?> updateMedicine(@PathVariable UUID id, @Valid @RequestBody Medicine medicine) {
        Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
        if (updatedMedicine != null) {
            return ResponseEntity.ok(updatedMedicine);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Medicine not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medicine", description = "Removes a medicine from the inventory")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedicine(@PathVariable UUID id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok(new MessageResponse("Medicine deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete medicine: " + e.getMessage()));
        }
    }
}
