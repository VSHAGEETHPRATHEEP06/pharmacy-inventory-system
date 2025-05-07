package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.repository.BranchRepository;
import com.pharmacy.inventory.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Override
    public Optional<Branch> getBranchById(UUID id) {
        return branchRepository.findById(id);
    }

    @Override
    public Optional<Branch> getBranchByName(String name) {
        return branchRepository.findByName(name);
    }

    @Override
    public Optional<Branch> getMainBranch() {
        return branchRepository.findByIsMainBranch(true);
    }

    @Override
    @Transactional
    public Branch createBranch(Branch branch) {
        // If this is marked as main branch, reset any existing main branch
        if (Boolean.TRUE.equals(branch.getIsMainBranch())) {
            resetMainBranch();
        }
        return branchRepository.save(branch);
    }

    @Override
    @Transactional
    public Branch updateBranch(UUID id, Branch updatedBranch) {
        Optional<Branch> existingBranch = branchRepository.findById(id);
        
        if (existingBranch.isPresent()) {
            Branch branch = existingBranch.get();
            branch.setName(updatedBranch.getName());
            branch.setAddress(updatedBranch.getAddress());
            branch.setContactPhone(updatedBranch.getContactPhone());
            branch.setEmail(updatedBranch.getEmail());
            
            // Handle main branch status if changing
            if (Boolean.TRUE.equals(updatedBranch.getIsMainBranch()) && 
                !Boolean.TRUE.equals(branch.getIsMainBranch())) {
                resetMainBranch();
                branch.setIsMainBranch(true);
            }
            
            return branchRepository.save(branch);
        }
        
        return null;
    }

    @Override
    @Transactional
    public void deleteBranch(UUID id) {
        branchRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Branch setAsMainBranch(UUID id) {
        Optional<Branch> branchOptional = branchRepository.findById(id);
        
        if (branchOptional.isPresent()) {
            // Reset any current main branch
            resetMainBranch();
            
            // Set this branch as main
            Branch branch = branchOptional.get();
            branch.setIsMainBranch(true);
            return branchRepository.save(branch);
        }
        
        return null;
    }
    
    private void resetMainBranch() {
        Optional<Branch> currentMainBranch = branchRepository.findByIsMainBranch(true);
        currentMainBranch.ifPresent(branch -> {
            branch.setIsMainBranch(false);
            branchRepository.save(branch);
        });
    }
}
