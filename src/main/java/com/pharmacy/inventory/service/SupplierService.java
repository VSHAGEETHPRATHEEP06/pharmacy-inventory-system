package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Optional<Supplier> getSupplierById(UUID id);
    List<Supplier> findSuppliersByName(String name);
    List<Supplier> findSuppliersWithMinimumRating(BigDecimal minRating);
    Supplier addSupplier(Supplier supplier);
    Supplier updateSupplier(UUID id, Supplier supplier);
    void deleteSupplier(UUID id);
}
