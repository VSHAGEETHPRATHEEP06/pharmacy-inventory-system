package com.pharmacy.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class for stock transfers between branches
 */
@Entity
@Table(name = "stock_transfers")
public class StockTransfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "from_branch_id", nullable = false)
    private Branch fromBranch;
    
    @ManyToOne
    @JoinColumn(name = "to_branch_id", nullable = false)
    private Branch toBranch;
    
    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private LocalDateTime requestDate;
    
    @Column
    private LocalDateTime completionDate;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;
    
    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;
    
    @ManyToOne
    @JoinColumn(name = "processed_by_id")
    private User processedBy;
    
    @Column(length = 500)
    private String notes;
    
    /**
     * Transfer status enum
     */
    public enum TransferStatus {
        PENDING,
        COMPLETED,
        REJECTED
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Branch getFromBranch() {
        return fromBranch;
    }
    
    public void setFromBranch(Branch fromBranch) {
        this.fromBranch = fromBranch;
    }
    
    public Branch getToBranch() {
        return toBranch;
    }
    
    public void setToBranch(Branch toBranch) {
        this.toBranch = toBranch;
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
    
    public LocalDateTime getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
    
    public LocalDateTime getCompletionDate() {
        return completionDate;
    }
    
    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }
    
    public TransferStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransferStatus status) {
        this.status = status;
    }
    
    public User getRequestedBy() {
        return requestedBy;
    }
    
    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    public User getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
