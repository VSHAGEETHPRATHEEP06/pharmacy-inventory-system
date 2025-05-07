package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.PrescriptionDTO;
import com.pharmacy.inventory.dto.PrescriptionItemDTO;
import com.pharmacy.inventory.model.Prescription;
import com.pharmacy.inventory.model.PrescriptionItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PrescriptionService {
    
    /**
     * Create a new prescription
     * 
     * @param prescriptionDTO The prescription data to create
     * @return The created prescription
     */
    Prescription createPrescription(PrescriptionDTO prescriptionDTO);
    
    /**
     * Update an existing prescription
     * 
     * @param id The ID of the prescription to update
     * @param prescription The prescription data to update
     * @return The updated prescription
     */
    Prescription updatePrescription(UUID id, Prescription prescription);
    
    /**
     * Get a prescription by its ID
     * 
     * @param id The ID of the prescription
     * @return The prescription if found
     */
    Prescription getPrescriptionById(UUID id);
    
    /**
     * Get all prescriptions
     * 
     * @return A list of all prescriptions
     */
    List<Prescription> getAllPrescriptions();
    
    /**
     * Add a prescription item to a prescription
     * 
     * @param prescriptionId The ID of the prescription to add the item to
     * @param prescriptionItemDTO The prescription item to add
     * @return The created prescription item
     */
    PrescriptionItem addPrescriptionItem(UUID prescriptionId, PrescriptionItemDTO prescriptionItemDTO);
    
    /**
     * Update a prescription item
     * 
     * @param id The ID of the prescription item to update
     * @param prescriptionItem The prescription item data to update
     * @return The updated prescription item
     */
    PrescriptionItem updatePrescriptionItem(UUID id, PrescriptionItem prescriptionItem);
    
    /**
     * Get all prescription items for a prescription
     * 
     * @param prescriptionId The ID of the prescription
     * @return A list of prescription items
     */
    List<PrescriptionItem> getPrescriptionItems(UUID prescriptionId);
    
    /**
     * Delete a prescription
     * 
     * @param id The ID of the prescription to delete
     * @return True if deleted successfully, false otherwise
     */
    boolean deletePrescription(UUID id);
    
    /**
     * Delete a prescription item
     * 
     * @param id The ID of the prescription item to delete
     * @return True if deleted successfully, false otherwise
     */
    boolean deletePrescriptionItem(UUID id);
    
    /**
     * Dispense a prescription item
     * 
     * @param prescriptionItemId The ID of the prescription item to dispense
     * @param userId The ID of the user dispensing the item
     * @param quantity The quantity being dispensed
     * @return The updated prescription item
     */
    PrescriptionItem dispenseItem(UUID prescriptionItemId, UUID userId, Integer quantity);
    
    /**
     * Upload an image for a prescription
     * 
     * @param prescriptionId The ID of the prescription
     * @param file The image file to upload
     * @return The filename of the uploaded image
     * @throws IOException If the file cannot be saved
     */
    String uploadPrescriptionImage(UUID prescriptionId, MultipartFile file) throws IOException;
    
    /**
     * Search prescriptions by patient name
     * 
     * @param patientName The patient name to search for
     * @return A list of matching prescriptions
     */
    List<Prescription> getPrescriptionsByPatientName(String patientName);
    
    /**
     * Get prescriptions within a date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of prescriptions within the date range
     */
    List<Prescription> getPrescriptionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
