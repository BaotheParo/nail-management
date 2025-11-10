package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.CustomerRequestDTO;
import com.nailpos.nailposapi.exception.DuplicateResourceException;
import com.nailpos.nailposapi.exception.ResourceNotFoundException;
import com.nailpos.nailposapi.mapper.CustomerMapper;
import com.nailpos.nailposapi.model.Customer;
import com.nailpos.nailposapi.repository.CustomerRepository;
import com.nailpos.nailposapi.service.ICustomerService;
import com.nailpos.nailposapi.specification.CustomerSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Page<Customer> getCustomers(String search, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.filterBy(search);
        return customerRepository.findAll(spec,pageable);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer", "id", id));
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerRequestDTO customerResponseDTO) {
        if (customerResponseDTO.getPhone() != null && !customerResponseDTO.getPhone().isEmpty()){
            customerRepository.findByPhone(customerResponseDTO.getPhone()).ifPresent(c->{
                throw new DuplicateResourceException("Customer" , "phone", customerResponseDTO.getPhone());
            });
        }
        Customer newCustomer = customerMapper.toEntity(customerResponseDTO);
        return customerRepository.save(newCustomer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerRequestDTO customerDTO) {
        Customer customer = getCustomerById(id); // Đã bao gồm check 404

        // Kiểm tra SĐT trùng (nếu SĐT thay đổi)
        if (customerDTO.getPhone() != null && !customerDTO.getPhone().isEmpty() && !customerDTO.getPhone().equals(customer.getPhone())) {
            customerRepository.findByPhone(customerDTO.getPhone()).ifPresent(c -> {
                throw new DuplicateResourceException("Customer", "phone", customerDTO.getPhone());
            });
        }

        // Dùng mapper để cập nhật
        customerMapper.updateFromDTO(customerDTO, customer);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if(!customerRepository.existsById(id)){
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        customerRepository.deleteById(id);
    }
}
