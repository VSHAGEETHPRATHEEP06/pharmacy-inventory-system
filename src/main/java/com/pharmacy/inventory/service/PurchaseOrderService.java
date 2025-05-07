package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.PurchaseOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderService {
    List<PurchaseOrder> getAllPurchaseOrders();
    Optional<PurchaseOrder> getPurchaseOrderById(UUID id);
    List<PurchaseOrder> getPurchaseOrdersBySupplier(UUID supplierId);
    List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrder.PurchaseStatus status);
    List<PurchaseOrder> getPurchaseOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
    PurchaseOrder updatePurchaseOrder(UUID id, PurchaseOrder purchaseOrder);
    PurchaseOrder updatePurchaseOrderStatus(UUID id, PurchaseOrder.PurchaseStatus status);
    void deletePurchaseOrder(UUID id);
}
