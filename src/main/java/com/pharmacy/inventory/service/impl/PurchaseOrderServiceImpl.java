package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.PurchaseOrder;
import com.pharmacy.inventory.model.Supplier;
import com.pharmacy.inventory.repository.PurchaseOrderRepository;
import com.pharmacy.inventory.repository.SupplierRepository;
import com.pharmacy.inventory.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    
    @Autowired
    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                 SupplierRepository supplierRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Optional<PurchaseOrder> getPurchaseOrderById(UUID id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(UUID supplierId) {
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        return supplier.map(purchaseOrderRepository::findBySupplier).orElse(List.of());
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrder.PurchaseStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseOrderRepository.findOrdersInDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrder updatePurchaseOrder(UUID id, PurchaseOrder updatedPurchaseOrder) {
        Optional<PurchaseOrder> existingOrder = purchaseOrderRepository.findById(id);
        
        if (existingOrder.isPresent()) {
            PurchaseOrder purchaseOrder = existingOrder.get();
            purchaseOrder.setSupplier(updatedPurchaseOrder.getSupplier());
            purchaseOrder.setStatus(updatedPurchaseOrder.getStatus());
            purchaseOrder.setPurchaseItems(updatedPurchaseOrder.getPurchaseItems());
            
            return purchaseOrderRepository.save(purchaseOrder);
        }
        
        return null;
    }

    @Override
    @Transactional
    public PurchaseOrder updatePurchaseOrderStatus(UUID id, PurchaseOrder.PurchaseStatus status) {
        Optional<PurchaseOrder> existingOrder = purchaseOrderRepository.findById(id);
        
        if (existingOrder.isPresent()) {
            PurchaseOrder purchaseOrder = existingOrder.get();
            purchaseOrder.setStatus(status);
            
            return purchaseOrderRepository.save(purchaseOrder);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deletePurchaseOrder(UUID id) {
        purchaseOrderRepository.deleteById(id);
    }
}
