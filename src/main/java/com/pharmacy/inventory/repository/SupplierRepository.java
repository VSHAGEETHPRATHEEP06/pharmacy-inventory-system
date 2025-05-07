package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT s FROM Supplier s WHERE s.rating >= :minRating")
    List<Supplier> findSuppliersWithMinimumRating(BigDecimal minRating);
}
