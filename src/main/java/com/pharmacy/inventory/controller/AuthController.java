package com.pharmacy.inventory.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.inventory.dto.request.LoginRequest;
import com.pharmacy.inventory.dto.request.SignupRequest;
import com.pharmacy.inventory.dto.response.JwtResponse;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.ERole;
import com.pharmacy.inventory.model.Role;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.RoleRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.security.jwt.JwtUtils;
import com.pharmacy.inventory.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("Login successful for email: {}", loginRequest.getEmail());
            
            return ResponseEntity.ok(new JwtResponse(jwt, 
                                                    userDetails.getId(), 
                                                    userDetails.getName(), 
                                                    userDetails.getEmail(), 
                                                    roles));
        } catch (BadCredentialsException e) {
            logger.error("Bad credentials for email: {}", loginRequest.getEmail(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Authentication error for email: {}", loginRequest.getEmail(), e);
            throw e;
        }
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.info("Registration attempt for email: {}", signUpRequest.getEmail());
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Registration failed - email already in use: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        
        String encodedPassword = encoder.encode(signUpRequest.getPassword());
        logger.info("Password encoded for email: {}, encoded length: {}", signUpRequest.getEmail(), encodedPassword.length());
        user.setPassword(encodedPassword);

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_SALESPERSON)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                    break;
                case "pharmacist":
                    Role modRole = roleRepository.findByName(ERole.ROLE_PHARMACIST)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(modRole);
                    break;
                case "manager":
                    Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(managerRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_SALESPERSON)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        
        logger.info("User registered successfully: {}", signUpRequest.getEmail());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
