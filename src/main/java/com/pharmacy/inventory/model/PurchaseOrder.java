package com.pharmacy.inventory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> purchaseItems = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
    
    public PurchaseOrder() {
    }
    
    public PurchaseOrder(UUID id, Supplier supplier, LocalDateTime orderDate, PurchaseStatus status,
                       List<PurchaseItem> purchaseItems, Branch branch) {
        this.id = id;
        this.supplier = supplier;
        this.orderDate = orderDate;
        this.status = status;
        this.purchaseItems = purchaseItems;
        this.branch = branch;
    }
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public PurchaseStatus getStatus() {
        return status;
    }
    
    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }
    
    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }
    
    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }
    
    public Branch getBranch() {
        return branch;
    }
    
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    
    // Helper method to add a purchase item
    public void addPurchaseItem(PurchaseItem item) {
        purchaseItems.add(item);
        item.setPurchaseOrder(this);
    }
    
    // Helper method to remove a purchase item
    public void removePurchaseItem(PurchaseItem item) {
        purchaseItems.remove(item);
        item.setPurchaseOrder(null);
    }
    
    public enum PurchaseStatus {
        PENDING, COMPLETED, CANCELLED
    }
}
