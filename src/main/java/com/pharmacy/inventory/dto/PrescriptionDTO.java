package com.pharmacy.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PrescriptionDTO {
    private UUID id;
    private String prescriptionNumber;
    private String patientName;
    private String patientContact;
    private String prescribedBy;
    private LocalDateTime prescriptionDate;
    private UserDTO doctor;
    private String notes;
    private List<PrescriptionItemDTO> prescriptionItems;
    private BranchDTO branch;
    
    public PrescriptionDTO() {
    }
    
    public PrescriptionDTO(UUID id, String prescriptionNumber, String patientName, String patientContact, 
                          String prescribedBy, LocalDateTime prescriptionDate, UserDTO doctor, String notes, 
                          List<PrescriptionItemDTO> prescriptionItems, BranchDTO branch) {
        this.id = id;
        this.prescriptionNumber = prescriptionNumber;
        this.patientName = patientName;
        this.patientContact = patientContact;
        this.prescribedBy = prescribedBy;
        this.prescriptionDate = prescriptionDate;
        this.doctor = doctor;
        this.notes = notes;
        this.prescriptionItems = prescriptionItems;
        this.branch = branch;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }
    
    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getPatientContact() {
        return patientContact;
    }
    
    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }
    
    public String getPrescribedBy() {
        return prescribedBy;
    }
    
    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
    }
    
    public LocalDateTime getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(LocalDateTime prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public UserDTO getDoctor() {
        return doctor;
    }
    
    public void setDoctor(UserDTO doctor) {
        this.doctor = doctor;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<PrescriptionItemDTO> getPrescriptionItems() {
        return prescriptionItems;
    }
    
    public void setPrescriptionItems(List<PrescriptionItemDTO> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }
    
    public BranchDTO getBranch() {
        return branch;
    }
    
    public void setBranch(BranchDTO branch) {
        this.branch = branch;
    }
}
