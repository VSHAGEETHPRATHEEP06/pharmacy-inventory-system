package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.service.BarcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/barcodes")
@Tag(name = "Barcode Management", description = "APIs for generating barcodes and QR codes")
public class BarcodeController {

    private final BarcodeService barcodeService;
    
    @Autowired
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate barcode", description = "Generates a barcode for given content")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public void generateBarcode(@RequestParam String content,
                               @RequestParam(defaultValue = "300") int width,
                               @RequestParam(defaultValue = "100") int height,
                               HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        barcodeService.generateBarcode(content, width, height, response.getOutputStream());
    }

    @PostMapping("/create")
    @Operation(summary = "Create barcode", description = "Creates and returns a barcode image as Base64 string")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<?> createBarcode(@RequestParam String content,
                                         @RequestParam(defaultValue = "300") int width,
                                         @RequestParam(defaultValue = "100") int height) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            barcodeService.generateBarcode(content, width, height, outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return ResponseEntity.ok(base64Image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to generate barcode: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/medicine/{medicineId}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate medicine barcode", description = "Generates a barcode for a medicine")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public void generateMedicineBarcode(@PathVariable String medicineId,
                                       HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        barcodeService.generateMedicineBarcode(medicineId, response.getOutputStream());
    }

    @PostMapping(value = "/batch/{batchId}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate batch barcode", description = "Generates a barcode for a batch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public void generateBatchBarcode(@PathVariable String batchId,
                                    HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        barcodeService.generateBatchBarcode(batchId, response.getOutputStream());
    }

    @GetMapping(value = "/qr/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Generate QR code", description = "Generates a QR code for given content")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public void generateQRCode(@RequestParam String content,
                              @RequestParam(defaultValue = "250") int width,
                              @RequestParam(defaultValue = "250") int height,
                              HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        barcodeService.generateQRCode(content, width, height, response.getOutputStream());
    }

    @PostMapping("/qr/create")
    @Operation(summary = "Create QR code", description = "Creates and returns a QR code image as Base64 string")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<?> createQRCode(@RequestParam String content,
                                        @RequestParam(defaultValue = "250") int width,
                                        @RequestParam(defaultValue = "250") int height) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            barcodeService.generateQRCode(content, width, height, outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return ResponseEntity.ok(base64Image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to generate QR code: " + e.getMessage()));
        }
    }
}
