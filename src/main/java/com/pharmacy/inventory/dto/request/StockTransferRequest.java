package com.pharmacy.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for stock transfer between branches.
 */
public class StockTransferRequest {
    
    @NotNull(message = "Source branch ID cannot be null")
    private UUID fromBranchId;
    
    @NotNull(message = "Destination branch ID cannot be null")
    private UUID toBranchId;
    
    @NotNull(message = "Medicine ID cannot be null")
    private UUID medicineId;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Getters and Setters
    public UUID getFromBranchId() {
        return fromBranchId;
    }

    public void setFromBranchId(UUID fromBranchId) {
        this.fromBranchId = fromBranchId;
    }

    public UUID getToBranchId() {
        return toBranchId;
    }

    public void setToBranchId(UUID toBranchId) {
        this.toBranchId = toBranchId;
    }

    public UUID getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(UUID medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
