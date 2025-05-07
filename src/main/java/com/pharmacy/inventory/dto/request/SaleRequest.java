package com.pharmacy.inventory.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SaleRequest {
    
    @NotNull(message = "User ID is required")
    private UUID user_id;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal total_amount;
    
    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customer_name;
    
    @NotEmpty(message = "Sale must have at least one item")
    @Valid
    private List<SaleItemRequest> sale_items;
    
    @NotNull(message = "Branch ID is required")
    private UUID branch_id;
    
    public UUID getUser_id() {
        return user_id;
    }
    
    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }
    
    public BigDecimal getTotal_amount() {
        return total_amount;
    }
    
    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }
    
    public String getCustomer_name() {
        return customer_name;
    }
    
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
    
    public List<SaleItemRequest> getSale_items() {
        return sale_items;
    }
    
    public void setSale_items(List<SaleItemRequest> sale_items) {
        this.sale_items = sale_items;
    }
    
    public UUID getBranch_id() {
        return branch_id;
    }
    
    public void setBranch_id(UUID branch_id) {
        this.branch_id = branch_id;
    }
    
    public static class SaleItemRequest {
        @NotNull(message = "Medicine ID is required")
        private UUID medicine_id;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Total price is required")
        @Positive(message = "Total price must be positive")
        private BigDecimal total_price;
        
        public UUID getMedicine_id() {
            return medicine_id;
        }
        
        public void setMedicine_id(UUID medicine_id) {
            this.medicine_id = medicine_id;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public BigDecimal getTotal_price() {
            return total_price;
        }
        
        public void setTotal_price(BigDecimal total_price) {
            this.total_price = total_price;
        }
    }
}
