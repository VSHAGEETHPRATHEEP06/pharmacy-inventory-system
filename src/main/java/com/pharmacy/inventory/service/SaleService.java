package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Sale;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleService {
    List<Sale> getAllSales();
    Optional<Sale> getSaleById(UUID id);
    List<Sale> getSalesByUserId(UUID userId);
    List<Sale> getSalesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Sale> getSalesByCustomerName(String customerName);
    Double getSalesTotalInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Sale createSale(Sale sale);
    Sale updateSale(Sale sale);
    void deleteSale(UUID id);
}
