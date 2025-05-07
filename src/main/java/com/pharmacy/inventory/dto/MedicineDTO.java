package com.pharmacy.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class MedicineDTO {
    private UUID id;
    private String name;
    private String description;
    private String manufacturer;
    private String category;
    private String dosageForm;
    private String strength;
    private String packSize;
    private BigDecimal price;
    private Boolean requiresPrescription;
    
    public MedicineDTO() {
    }
    
    public MedicineDTO(UUID id, String name, String description, String manufacturer, 
                      String category, String dosageForm, String strength, String packSize, 
                      BigDecimal price, Boolean requiresPrescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.category = category;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.packSize = packSize;
        this.price = price;
        this.requiresPrescription = requiresPrescription;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDosageForm() {
        return dosageForm;
    }
    
    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }
    
    public String getStrength() {
        return strength;
    }
    
    public void setStrength(String strength) {
        this.strength = strength;
    }
    
    public String getPackSize() {
        return packSize;
    }
    
    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Boolean getRequiresPrescription() {
        return requiresPrescription;
    }
    
    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }
}
