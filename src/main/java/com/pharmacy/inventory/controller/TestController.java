package com.pharmacy.inventory.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.inventory.dto.response.UserSummaryDTO;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.UserRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserSummaryDTO> userSummaries = users.stream()
            .map(user -> new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet())
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userSummaries);
    }
}
