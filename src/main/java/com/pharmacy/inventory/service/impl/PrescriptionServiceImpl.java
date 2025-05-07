package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.dto.PrescriptionDTO;
import com.pharmacy.inventory.dto.PrescriptionItemDTO;
import com.pharmacy.inventory.exception.ResourceNotFoundException;
import com.pharmacy.inventory.model.*;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.PrescriptionItemRepository;
import com.pharmacy.inventory.repository.PrescriptionRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;
    
    @Value("${pharmacy.prescriptions.images-path}")
    private String prescriptionImagesPath;
    
    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                 PrescriptionItemRepository prescriptionItemRepository,
                                 MedicineRepository medicineRepository,
                                 UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Prescription createPrescription(PrescriptionDTO prescriptionDTO) {
        Prescription prescription = new Prescription();
        prescription.setPatientName(prescriptionDTO.getPatientName());
        prescription.setPatientContact(prescriptionDTO.getPatientContact());
        prescription.setPrescribedBy(prescriptionDTO.getPrescribedBy());
        prescription.setPrescriptionDate(prescriptionDTO.getPrescriptionDate());
        
        // Generate a unique prescription number if not provided
        if (prescription.getPrescriptionNumber() == null || prescription.getPrescriptionNumber().isEmpty()) {
            prescription.setPrescriptionNumber(generatePrescriptionNumber());
        }
        
        if (prescriptionDTO.getDoctor() != null && prescriptionDTO.getDoctor().getId() != null) {
            userRepository.findById(prescriptionDTO.getDoctor().getId())
                .ifPresent(prescription::setDoctor);
        }
        
        return prescriptionRepository.save(prescription);
    }

    @Override
    public PrescriptionItem addPrescriptionItem(UUID prescriptionId, PrescriptionItemDTO prescriptionItemDTO) {
        Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(prescriptionId);
        if (!prescriptionOptional.isPresent()) {
            throw new ResourceNotFoundException("Prescription not found with id " + prescriptionId);
        }
        
        Optional<Medicine> medicineOptional = medicineRepository.findById(prescriptionItemDTO.getMedicine().getId());
        if (!medicineOptional.isPresent()) {
            throw new ResourceNotFoundException("Medicine not found with id " + prescriptionItemDTO.getMedicine().getId());
        }
        
        PrescriptionItem prescriptionItem = new PrescriptionItem();
        prescriptionItem.setPrescription(prescriptionOptional.get());
        prescriptionItem.setMedicine(medicineOptional.get());
        prescriptionItem.setDosage(prescriptionItemDTO.getDosage());
        prescriptionItem.setFrequency(prescriptionItemDTO.getFrequency());
        prescriptionItem.setDuration(prescriptionItemDTO.getDuration());
        prescriptionItem.setInstructions(prescriptionItemDTO.getInstructions());
        prescriptionItem.setQuantity(prescriptionItemDTO.getQuantity());
        
        return prescriptionItemRepository.save(prescriptionItem);
    }

    @Override
    @Transactional
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Override
    @Transactional
    public Prescription getPrescriptionById(UUID id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id " + id));
    }

    @Override
    @Transactional
    public List<PrescriptionItem> getPrescriptionItems(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id " + prescriptionId));
        
        return prescription.getPrescriptionItems();
    }

    @Override
    @Transactional
    public String uploadPrescriptionImage(UUID prescriptionId, MultipartFile file) throws IOException {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id " + prescriptionId));
        
        Path uploadPath = Paths.get(prescriptionImagesPath);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String filename = prescriptionId.toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        prescription.setPrescriptionImagePath(filePath.toString());
        prescriptionRepository.save(prescription);
        
        return filename;
    }

    @Override
    @Transactional
    public PrescriptionItem dispenseItem(UUID prescriptionItemId, UUID userId, Integer quantity) {
        PrescriptionItem item = prescriptionItemRepository.findById(prescriptionItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription item not found with id " + prescriptionItemId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        
        item.setDispensedBy(user);
        item.setDispensedQuantity(quantity);
        item.setIsDispensed(true);
        
        return prescriptionItemRepository.save(item);
    }

    @Override
    @Transactional
    public List<Prescription> getPrescriptionsByPatientName(String patientName) {
        return prescriptionRepository.findByPatientNameContainingIgnoreCase(patientName);
    }

    @Override
    @Transactional
    public List<Prescription> getPrescriptionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return prescriptionRepository.findByPrescriptionDateBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Prescription updatePrescription(UUID id, Prescription updatedPrescription) {
        Optional<Prescription> existingPrescription = prescriptionRepository.findById(id);
        
        if (existingPrescription.isPresent()) {
            Prescription prescription = existingPrescription.get();
            
            prescription.setPatientName(updatedPrescription.getPatientName());
            prescription.setPatientContact(updatedPrescription.getPatientContact());
            prescription.setPrescribedBy(updatedPrescription.getPrescribedBy());
            prescription.setPrescriptionDate(updatedPrescription.getPrescriptionDate());
            prescription.setNotes(updatedPrescription.getNotes());
            
            if (updatedPrescription.getDoctor() != null && updatedPrescription.getDoctor().getId() != null) {
                userRepository.findById(updatedPrescription.getDoctor().getId())
                    .ifPresent(prescription::setDoctor);
            }
            
            return prescriptionRepository.save(prescription);
        }
        
        return null;
    }

    @Override
    @Transactional
    public PrescriptionItem updatePrescriptionItem(UUID id, PrescriptionItem updatedItem) {
        Optional<PrescriptionItem> existingItemOptional = prescriptionItemRepository.findById(id);
        
        if (existingItemOptional.isPresent()) {
            PrescriptionItem existingItem = existingItemOptional.get();
            
            existingItem.setDosage(updatedItem.getDosage());
            existingItem.setFrequency(updatedItem.getFrequency());
            existingItem.setDuration(updatedItem.getDuration());
            existingItem.setInstructions(updatedItem.getInstructions());
            existingItem.setQuantity(updatedItem.getQuantity());
            
            if (updatedItem.getMedicine() != null && updatedItem.getMedicine().getId() != null) {
                medicineRepository.findById(updatedItem.getMedicine().getId())
                    .ifPresent(existingItem::setMedicine);
            }
            
            return prescriptionItemRepository.save(existingItem);
        }
        
        return null;
    }

    @Override
    @Transactional
    public boolean deletePrescription(UUID id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deletePrescriptionItem(UUID id) {
        if (prescriptionItemRepository.existsById(id)) {
            prescriptionItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Helper method to generate prescription numbers
    private String generatePrescriptionNumber() {
        return "PRESC-" + System.currentTimeMillis();
    }
}
