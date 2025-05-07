package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {
    List<PrescriptionItem> findByPrescriptionId(UUID prescriptionId);
    
    List<PrescriptionItem> findByPrescriptionIdAndIsDispensed(UUID prescriptionId, Boolean isDispensed);
    
    List<PrescriptionItem> findByMedicineId(UUID medicineId);
    
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.prescription.id = :prescriptionId AND pi.medicine.id = :medicineId")
    List<PrescriptionItem> findByPrescriptionIdAndMedicineId(@Param("prescriptionId") UUID prescriptionId, @Param("medicineId") UUID medicineId);
}
