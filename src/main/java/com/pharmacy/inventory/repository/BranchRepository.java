package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    Optional<Branch> findByName(String name);
    Optional<Branch> findByIsMainBranch(Boolean isMainBranch);
}
