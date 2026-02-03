package com.salon.salon_management.controller;

import com.salon.salon_management.entity.CustomerReturn;
import com.salon.salon_management.service.CustomerReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-returns")
@CrossOrigin(origins = "*")
public class CustomerReturnController {

    @Autowired
    private CustomerReturnService customerReturnService;

    @PostMapping
    public CustomerReturn createCustomerReturn(@RequestBody CustomerReturn customerReturn) {
        return customerReturnService.createCustomerReturn(customerReturn);
    }

    @GetMapping
    public List<CustomerReturn> getAllCustomerReturns() {
        return customerReturnService.getAllCustomerReturns();
    }

    @GetMapping("/{id}")
    public CustomerReturn getCustomerReturnById(@PathVariable Long id) {
        return customerReturnService.getCustomerReturnById(id).orElse(null);
    }
}
