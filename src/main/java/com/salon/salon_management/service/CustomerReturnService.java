package com.salon.salon_management.service;

import com.salon.salon_management.entity.CustomerReturn;
import com.salon.salon_management.repository.CustomerReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerReturnService {

    @Autowired
    private CustomerReturnRepository customerReturnRepository;

    @Autowired
    private com.salon.salon_management.service.ProductService productService;

    public CustomerReturn createCustomerReturn(CustomerReturn customerReturn) {
        CustomerReturn savedReturn = customerReturnRepository.save(customerReturn);

        if (savedReturn.getProduct() != null && savedReturn.getQuantity() != null) {
            // Assuming return means we add back to stock OR we discard?
            // Usually returns might go back to stock if undamaged, or waste.
            // Let's assume standard retail flow: Return increases stock (customer gives
            // back).
            // Wait, previous context: PO (buying) increases stock. Return (from customer)
            // increases stock?
            // "CustomerReturn": Customer gives back product. Stock should INCREASE.
            // "PurchaseOrder": We buy from Supplier. Stock INCREASES.
            // "Sales" (not implemented yet, but implied by 'Feedback'): Stock DECREASES.
            // Since we don't have 'Sales', I'll just implement Return as increasing stock
            // for now.
            // Wait, if it's damaged it wouldn't.
            // Let's implement it as stock INCREASE for simplicity, assuming restockable.

            productService.updateStock(savedReturn.getProduct().getProductId(), savedReturn.getQuantity());
        }
        return savedReturn;
    }

    public List<CustomerReturn> getAllCustomerReturns() {
        return customerReturnRepository.findAll();
    }

    public Optional<CustomerReturn> getCustomerReturnById(Long id) {
        return customerReturnRepository.findById(id);
    }
}
