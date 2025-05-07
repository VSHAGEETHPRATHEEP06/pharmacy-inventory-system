package com.pharmacy.inventory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

/**
 * Entity representing an item in a sale transaction
 */
@Entity
@Table(name = "sale_items")
public class SaleItem {
    
    /**
     * Unique identifier for the sale item
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * The sale transaction that this item belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;
    
    /**
     * The medicine being sold in this item
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    
    /**
     * The quantity of the medicine being sold
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /**
     * The total price of the sale item
     */
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    // Constructor
    public SaleItem() {
    }
    
    public SaleItem(UUID id, Sale sale, Medicine medicine, Integer quantity, Double totalPrice) {
        this.id = id;
        this.sale = sale;
        this.medicine = medicine;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Sale getSale() {
        return sale;
    }
    
    public void setSale(Sale sale) {
        this.sale = sale;
    }
    
    public Medicine getMedicine() {
        return medicine;
    }
    
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
