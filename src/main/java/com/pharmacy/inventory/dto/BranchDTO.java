package com.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private UUID id;
    private String name;
    private String address;
    private String contactPhone;
    private String email;
    private Boolean isMainBranch;
}
