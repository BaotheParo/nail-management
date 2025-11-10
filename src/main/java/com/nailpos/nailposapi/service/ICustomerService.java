package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.CustomerRequestDTO;
import com.nailpos.nailposapi.dto.CustomerResponseDTO;
import com.nailpos.nailposapi.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICustomerService {
    Page<Customer> getCustomers(String search, Pageable pageable);
    Customer getCustomerById(Long id);
    Customer createCustomer(CustomerRequestDTO customerDTO);
    Customer updateCustomer(Long id, CustomerRequestDTO customerRequestDTO);
    void deleteCustomer(Long id);
}
