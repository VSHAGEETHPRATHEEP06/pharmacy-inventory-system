package com.pharmacy.inventory.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "prescription_number", unique = true, nullable = false)
    private String prescriptionNumber;
    
    @Column(name = "patient_name", nullable = false)
    private String patientName;
    
    @Column(name = "patient_contact")
    private String patientContact;
    
    @Column(name = "prescribed_by")
    private String prescribedBy;
    
    @Column(name = "prescription_date")
    private LocalDateTime prescriptionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "prescription_image_path")
    private String prescriptionImagePath;
    
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> prescriptionItems = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Prescription() {
    }
    
    public Prescription(UUID id, String prescriptionNumber, String patientName, String patientContact,
                       String prescribedBy, LocalDateTime prescriptionDate, User doctor, String notes, 
                       String prescriptionImagePath, List<PrescriptionItem> prescriptionItems, Branch branch,
                       LocalDateTime createdAt) {
        this.id = id;
        this.prescriptionNumber = prescriptionNumber;
        this.patientName = patientName;
        this.patientContact = patientContact;
        this.prescribedBy = prescribedBy;
        this.prescriptionDate = prescriptionDate;
        this.doctor = doctor;
        this.notes = notes;
        this.prescriptionImagePath = prescriptionImagePath;
        this.prescriptionItems = prescriptionItems;
        this.branch = branch;
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (prescriptionDate == null) {
            prescriptionDate = LocalDateTime.now();
        }
    }
    
    // Helper method to add a prescription item
    public void addPrescriptionItem(PrescriptionItem item) {
        prescriptionItems.add(item);
        item.setPrescription(this);
    }
    
    // Helper method to remove a prescription item
    public void removePrescriptionItem(PrescriptionItem item) {
        prescriptionItems.remove(item);
        item.setPrescription(null);
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
    
    public User getDoctor() {
        return doctor;
    }
    
    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPrescriptionImagePath() {
        return prescriptionImagePath;
    }
    
    public void setPrescriptionImagePath(String prescriptionImagePath) {
        this.prescriptionImagePath = prescriptionImagePath;
    }
    
    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }
    
    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }
    
    public Branch getBranch() {
        return branch;
    }
    
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
