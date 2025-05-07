package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.PurchaseItem;
import com.pharmacy.inventory.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, UUID> {
    List<PurchaseItem> findByPurchaseOrder(PurchaseOrder purchaseOrder);
    List<PurchaseItem> findByMedicine(Medicine medicine);
    
    @Query("SELECT SUM(pi.quantity) FROM PurchaseItem pi WHERE pi.medicine.id = :medicineId")
    Integer getTotalPurchasedQuantityByMedicineId(UUID medicineId);
}
