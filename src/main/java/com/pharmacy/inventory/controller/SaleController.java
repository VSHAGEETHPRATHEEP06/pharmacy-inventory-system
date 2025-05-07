package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.request.SaleRequest;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Sale;
import com.pharmacy.inventory.model.SaleItem;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.BranchRepository;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.SaleService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales Management", description = "APIs for managing sales in the pharmacy inventory")
public class SaleController {

    private final SaleService saleService;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final BranchRepository branchRepository;
    
    @Autowired
    public SaleController(SaleService saleService, UserRepository userRepository, 
                         MedicineRepository medicineRepository, BranchRepository branchRepository) {
        this.saleService = saleService;
        this.userRepository = userRepository;
        this.medicineRepository = medicineRepository;
        this.branchRepository = branchRepository;
    }

    @GetMapping
    @Operation(summary = "Get all sales", description = "Retrieves a list of all sales transactions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SALESPERSON', 'PHARMACIST', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID", description = "Retrieves sale details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SALESPERSON', 'PHARMACIST', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Sale> getSaleById(@PathVariable UUID id) {
        return saleService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get sales by user ID", description = "Retrieves all sales made by a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Sale>> getSalesByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(saleService.getSalesByUserId(userId));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get sales in date range", description = "Retrieves all sales within a specified date range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<Sale>> getSalesInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(saleService.getSalesInDateRange(startDate, endDate));
    }

    @GetMapping("/customer")
    @Operation(summary = "Get sales by customer name", description = "Retrieves all sales for a specific customer")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SALESPERSON', 'PHARMACIST', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<Sale>> getSalesByCustomerName(@RequestParam String customerName) {
        return ResponseEntity.ok(saleService.getSalesByCustomerName(customerName));
    }

    @GetMapping("/total")
    @Operation(summary = "Get sales total in date range", description = "Calculates the total sales amount within a specified date range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Double> getSalesTotalInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(saleService.getSalesTotalInDateRange(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "Create a new sale", description = "Records a new sale transaction in the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SALESPERSON', 'PHARMACIST', 'ADMIN')")
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleRequest saleRequest) {
        try {
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(saleRequest.getUser_id());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }
            
            // Validate branch exists
            Optional<Branch> branchOpt = branchRepository.findById(saleRequest.getBranch_id());
            if (branchOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Branch not found"));
            }
            
            // Create sale entity
            Sale sale = new Sale();
            sale.setUser(userOpt.get());
            
            // Safely handle BigDecimal to Double conversion
            if (saleRequest.getTotal_amount() != null) {
                sale.setTotalAmount(saleRequest.getTotal_amount().doubleValue());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Total amount cannot be null"));
            }
            
            sale.setCustomerName(saleRequest.getCustomer_name());
            sale.setBranch(branchOpt.get());
            sale.setSaleDate(LocalDateTime.now());
            
            // Process sale items
            List<SaleItem> saleItems = new ArrayList<>();
            
            if (saleRequest.getSale_items() == null || saleRequest.getSale_items().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Sale must have at least one item"));
            }
            
            for (SaleRequest.SaleItemRequest itemRequest : saleRequest.getSale_items()) {
                // Validate medicine exists
                Optional<Medicine> medicineOpt = medicineRepository.findById(itemRequest.getMedicine_id());
                if (medicineOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Medicine not found with ID: " + itemRequest.getMedicine_id()));
                }
                
                SaleItem saleItem = new SaleItem();
                saleItem.setSale(sale);
                saleItem.setMedicine(medicineOpt.get());
                
                // Validate quantity
                if (itemRequest.getQuantity() <= 0) {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Quantity must be positive for medicine: " + medicineOpt.get().getName()));
                }
                saleItem.setQuantity(itemRequest.getQuantity());
                
                // Safely handle BigDecimal to Double conversion
                if (itemRequest.getTotal_price() != null) {
                    saleItem.setTotalPrice(itemRequest.getTotal_price().doubleValue());
                } else {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Total price cannot be null for medicine: " + medicineOpt.get().getName()));
                }
                
                saleItems.add(saleItem);
            }
            
            sale.setSaleItems(saleItems);
            
            // Save sale with items
            Sale savedSale = saleService.createSale(sale);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to create sale: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sale", description = "Updates details of an existing sale")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'ADMIN')")
    public ResponseEntity<?> updateSale(@PathVariable UUID id, @Valid @RequestBody SaleRequest saleRequest) {
        try {
            // Check if sale exists
            Optional<Sale> existingSaleOpt = saleService.getSaleById(id);
            if (existingSaleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Sale not found with ID: " + id));
            }
            
            Sale existingSale = existingSaleOpt.get();
            
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(saleRequest.getUser_id());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }
            
            // Validate branch exists
            Optional<Branch> branchOpt = branchRepository.findById(saleRequest.getBranch_id());
            if (branchOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Branch not found"));
            }
            
            // Update sale
            existingSale.setUser(userOpt.get());
            
            // Safely handle BigDecimal to Double conversion
            if (saleRequest.getTotal_amount() != null) {
                existingSale.setTotalAmount(saleRequest.getTotal_amount().doubleValue());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Total amount cannot be null"));
            }
            
            existingSale.setCustomerName(saleRequest.getCustomer_name());
            existingSale.setBranch(branchOpt.get());
            
            // Clear existing items and add new ones
            existingSale.getSaleItems().clear();
            
            if (saleRequest.getSale_items() == null || saleRequest.getSale_items().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Sale must have at least one item"));
            }
            
            for (SaleRequest.SaleItemRequest itemRequest : saleRequest.getSale_items()) {
                // Validate medicine exists
                Optional<Medicine> medicineOpt = medicineRepository.findById(itemRequest.getMedicine_id());
                if (medicineOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Medicine not found with ID: " + itemRequest.getMedicine_id()));
                }
                
                SaleItem saleItem = new SaleItem();
                saleItem.setSale(existingSale);
                saleItem.setMedicine(medicineOpt.get());
                
                // Validate quantity
                if (itemRequest.getQuantity() <= 0) {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Quantity must be positive for medicine: " + medicineOpt.get().getName()));
                }
                saleItem.setQuantity(itemRequest.getQuantity());
                
                // Safely handle BigDecimal to Double conversion
                if (itemRequest.getTotal_price() != null) {
                    saleItem.setTotalPrice(itemRequest.getTotal_price().doubleValue());
                } else {
                    return ResponseEntity.badRequest().body(
                        new MessageResponse("Total price cannot be null for medicine: " + medicineOpt.get().getName()));
                }
                
                existingSale.getSaleItems().add(saleItem);
            }
            
            // Save updated sale
            Sale updatedSale = saleService.updateSale(existingSale);
            return ResponseEntity.ok(updatedSale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to update sale: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sale", description = "Deletes a sale record from the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSale(@PathVariable UUID id) {
        try {
            // Check if sale exists
            if (saleService.getSaleById(id).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Sale not found with ID: " + id));
            }
            
            saleService.deleteSale(id);
            return ResponseEntity.ok(new MessageResponse("Sale deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete sale: " + e.getMessage()));
        }
    }
}
