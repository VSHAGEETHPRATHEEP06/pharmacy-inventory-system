package com.pharmacy.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    
    @Column(name = "dosage")
    private String dosage;
    
    @Column(name = "frequency")
    private String frequency;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "instructions", length = 500)
    private String instructions;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "is_dispensed")
    private Boolean isDispensed = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by")
    private User dispensedBy;
    
    @Column(name = "dispensed_quantity")
    private Integer dispensedQuantity = 0;
    
    // Explicit getter and setter methods
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Prescription getPrescription() {
        return prescription;
    }
    
    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
    
    public Medicine getMedicine() {
        return medicine;
    }
    
    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Boolean getIsDispensed() {
        return isDispensed;
    }
    
    public void setIsDispensed(Boolean isDispensed) {
        this.isDispensed = isDispensed;
    }
    
    public User getDispensedBy() {
        return dispensedBy;
    }
    
    public void setDispensedBy(User dispensedBy) {
        this.dispensedBy = dispensedBy;
    }
    
    public Integer getDispensedQuantity() {
        return dispensedQuantity;
    }
    
    public void setDispensedQuantity(Integer dispensedQuantity) {
        this.dispensedQuantity = dispensedQuantity;
    }
}
