package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Branch;
import com.pharmacy.inventory.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/branches")
@Tag(name = "Branch Management", description = "APIs for managing branches in multi-branch pharmacy setup")
public class BranchController {

    private final BranchService branchService;
    
    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    @Operation(summary = "Get all branches", description = "Retrieves a list of all pharmacy branches")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch by ID", description = "Retrieves branch details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Branch> getBranchById(@PathVariable UUID id) {
        return branchService.getBranchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get branch by name", description = "Retrieves branch details by its name")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Branch> getBranchByName(@PathVariable String name) {
        return branchService.getBranchByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/main")
    @Operation(summary = "Get main branch", description = "Retrieves the main branch details")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Branch> getMainBranch() {
        return branchService.getMainBranch()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new branch", description = "Creates a new pharmacy branch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> createBranch(@Valid @RequestBody Branch branch) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(branch));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update branch", description = "Updates details of an existing branch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBranch(@PathVariable UUID id, @Valid @RequestBody Branch branch) {
        Branch updatedBranch = branchService.updateBranch(id, branch);
        if (updatedBranch != null) {
            return ResponseEntity.ok(updatedBranch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Branch not found with ID: " + id));
    }

    @PatchMapping("/{id}/set-as-main")
    @Operation(summary = "Set as main branch", description = "Sets a branch as the main branch")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setAsMainBranch(@PathVariable UUID id) {
        Branch updatedBranch = branchService.setAsMainBranch(id);
        if (updatedBranch != null) {
            return ResponseEntity.ok(updatedBranch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Branch not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete branch", description = "Removes a branch from the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBranch(@PathVariable UUID id) {
        try {
            branchService.deleteBranch(id);
            return ResponseEntity.ok(new MessageResponse("Branch deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete branch: " + e.getMessage()));
        }
    }
}
