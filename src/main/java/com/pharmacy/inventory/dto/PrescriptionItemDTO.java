package com.pharmacy.inventory.dto;

import java.util.UUID;

public class PrescriptionItemDTO {
    private UUID id;
    private MedicineDTO medicine;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private Integer quantity;
    private Boolean isDispensed;
    private UserDTO dispensedBy;
    private Integer dispensedQuantity;
    
    public PrescriptionItemDTO() {
    }
    
    public PrescriptionItemDTO(UUID id, MedicineDTO medicine, String dosage, String frequency,
                             String duration, String instructions, Integer quantity,
                             Boolean isDispensed, UserDTO dispensedBy, Integer dispensedQuantity) {
        this.id = id;
        this.medicine = medicine;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
        this.quantity = quantity;
        this.isDispensed = isDispensed;
        this.dispensedBy = dispensedBy;
        this.dispensedQuantity = dispensedQuantity;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public MedicineDTO getMedicine() {
        return medicine;
    }
    
    public void setMedicine(MedicineDTO medicine) {
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
    
    public UserDTO getDispensedBy() {
        return dispensedBy;
    }
    
    public void setDispensedBy(UserDTO dispensedBy) {
        this.dispensedBy = dispensedBy;
    }
    
    public Integer getDispensedQuantity() {
        return dispensedQuantity;
    }
    
    public void setDispensedQuantity(Integer dispensedQuantity) {
        this.dispensedQuantity = dispensedQuantity;
    }
}
