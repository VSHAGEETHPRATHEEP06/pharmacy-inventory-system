package com.pharmacy.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Pharmacy Inventory Management API",
        version = "1.0",
        description = "REST APIs for Pharmacy Inventory Management System"
    )
)
public class PharmacyInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmacyInventoryApplication.class, args);
    }
}
