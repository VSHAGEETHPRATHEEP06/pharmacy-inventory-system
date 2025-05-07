package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Supplier;
import com.pharmacy.inventory.repository.SupplierRepository;
import com.pharmacy.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    
    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Optional<Supplier> getSupplierById(UUID id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> findSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Supplier> findSuppliersWithMinimumRating(BigDecimal minRating) {
        return supplierRepository.findSuppliersWithMinimumRating(minRating);
    }

    @Override
    @Transactional
    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(UUID id, Supplier updatedSupplier) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(id);
        
        if (existingSupplier.isPresent()) {
            Supplier supplier = existingSupplier.get();
            supplier.setName(updatedSupplier.getName());
            supplier.setContactEmail(updatedSupplier.getContactEmail());
            supplier.setContactPhone(updatedSupplier.getContactPhone());
            supplier.setRating(updatedSupplier.getRating());
            
            return supplierRepository.save(supplier);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }
}
