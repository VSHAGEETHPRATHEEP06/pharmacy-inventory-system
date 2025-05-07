package com.pharmacy.inventory.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to explicitly define component scanning for the application
 */
@Configuration
@ComponentScan(basePackages = {
    "com.pharmacy.inventory.controller",
    "com.pharmacy.inventory.service",
    "com.pharmacy.inventory.service.impl",
    "com.pharmacy.inventory.repository",
    "com.pharmacy.inventory.config",
    "com.pharmacy.inventory.security"
})
public class ComponentScanConfig {
    // No additional configuration needed
}
