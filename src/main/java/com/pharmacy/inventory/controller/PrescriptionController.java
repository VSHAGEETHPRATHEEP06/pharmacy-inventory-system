package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.PrescriptionDTO;
import com.pharmacy.inventory.dto.PrescriptionItemDTO;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Prescription;
import com.pharmacy.inventory.model.PrescriptionItem;
import com.pharmacy.inventory.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/prescriptions")
@Tag(name = "Prescription Management", description = "APIs for managing prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    @Operation(summary = "Get all prescriptions", description = "Retrieves a list of all prescriptions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves prescription details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable UUID id) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(id);
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/patient/{name}")
    @Operation(summary = "Search prescriptions by patient name", description = "Retrieves prescriptions by patient name")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<List<Prescription>> searchPrescriptionsByPatientName(@PathVariable String name) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientName(name));
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Search prescriptions by date range", description = "Retrieves prescriptions issued between two dates")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDateRange(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "Create prescription", description = "Creates a new prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Prescription> createPrescription(@Valid @RequestBody PrescriptionDTO prescriptionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.createPrescription(prescriptionDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update prescription", description = "Updates an existing prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Prescription> updatePrescription(
            @PathVariable UUID id, 
            @Valid @RequestBody Prescription updatedPrescription) {
        Prescription prescription = prescriptionService.updatePrescription(id, updatedPrescription);
        
        if (prescription != null) {
            return ResponseEntity.ok(prescription);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prescription", description = "Deletes a prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePrescription(@PathVariable UUID id) {
        boolean deleted = prescriptionService.deletePrescription(id);
        
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("Prescription deleted successfully"));
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{prescriptionId}/items")
    @Operation(summary = "Add prescription item", description = "Adds a prescription item to a prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionItem> addPrescriptionItem(
            @PathVariable UUID prescriptionId,
            @Valid @RequestBody PrescriptionItemDTO prescriptionItemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.addPrescriptionItem(prescriptionId, prescriptionItemDTO));
    }

    @GetMapping("/{prescriptionId}/items")
    @Operation(summary = "Get prescription items", description = "Retrieves all items for a prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<List<PrescriptionItem>> getPrescriptionItems(@PathVariable UUID prescriptionId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionItems(prescriptionId));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update prescription item", description = "Updates an existing prescription item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<PrescriptionItem> updatePrescriptionItem(
            @PathVariable UUID itemId, 
            @Valid @RequestBody PrescriptionItem updatedItem) {
        PrescriptionItem item = prescriptionService.updatePrescriptionItem(itemId, updatedItem);
        
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete prescription item", description = "Deletes a prescription item")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePrescriptionItem(@PathVariable UUID itemId) {
        boolean deleted = prescriptionService.deletePrescriptionItem(itemId);
        
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("Prescription item deleted successfully"));
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{prescriptionId}/upload-image")
    @Operation(summary = "Upload prescription image", description = "Uploads an image for a prescription")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<MessageResponse> uploadPrescriptionImage(
            @PathVariable UUID prescriptionId,
            @RequestParam("file") MultipartFile file) {
        try {
            String filename = prescriptionService.uploadPrescriptionImage(prescriptionId, file);
            return ResponseEntity.ok(new MessageResponse("Image uploaded successfully: " + filename));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to upload image: " + e.getMessage()));
        }
    }

    @PostMapping("/items/{itemId}/dispense")
    @Operation(summary = "Dispense prescription item", description = "Marks a prescription item as dispensed")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<PrescriptionItem> dispensePrescriptionItem(
            @PathVariable UUID itemId,
            @RequestParam UUID userId,
            @RequestParam Integer quantity) {
        PrescriptionItem item = prescriptionService.dispenseItem(itemId, userId, quantity);
        
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        
        return ResponseEntity.notFound().build();
    }
}
