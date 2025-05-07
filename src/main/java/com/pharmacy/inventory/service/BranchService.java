package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchService {
    List<Branch> getAllBranches();
    Optional<Branch> getBranchById(UUID id);
    Optional<Branch> getBranchByName(String name);
    Optional<Branch> getMainBranch();
    Branch createBranch(Branch branch);
    Branch updateBranch(UUID id, Branch branch);
    void deleteBranch(UUID id);
    Branch setAsMainBranch(UUID id);
}
