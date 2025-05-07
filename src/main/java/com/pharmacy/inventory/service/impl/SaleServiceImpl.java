package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Sale;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.SaleRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository, UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public Optional<Sale> getSaleById(UUID id) {
        return saleRepository.findById(id);
    }

    @Override
    public List<Sale> getSalesByUserId(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(saleRepository::findByUser).orElse(List.of());
    }

    @Override
    public List<Sale> getSalesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findSalesInDateRange(startDate, endDate);
    }

    @Override
    public List<Sale> getSalesByCustomerName(String customerName) {
        return saleRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }

    @Override
    public Double getSalesTotalInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.getSalesTotalInDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public Sale createSale(Sale sale) {
        return saleRepository.save(sale);
    }

    @Override
    @Transactional
    public Sale updateSale(Sale updatedSale) {
        UUID id = updatedSale.getId();
        Optional<Sale> existingSale = saleRepository.findById(id);
        
        if (existingSale.isPresent()) {
            Sale sale = existingSale.get();
            sale.setUser(updatedSale.getUser());
            sale.setTotalAmount(updatedSale.getTotalAmount());
            sale.setCustomerName(updatedSale.getCustomerName());
            sale.setBranch(updatedSale.getBranch());
            
            // Clear and add all items from the updated sale
            sale.getSaleItems().clear();
            sale.getSaleItems().addAll(updatedSale.getSaleItems());
            
            return saleRepository.save(sale);
        }
        
        return saleRepository.save(updatedSale); // If not found, create a new one
    }

    @Override
    @Transactional
    public void deleteSale(UUID id) {
        saleRepository.deleteById(id);
    }
}
