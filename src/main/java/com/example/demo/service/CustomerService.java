package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * 고객 생성
     */
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    /**
     * 전체 고객 조회
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    /**
     * 페이지별 고객 조회
     */
    public Page<Customer> getCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
    
    /**
     * ID로 고객 조회
     */
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }
    
    /**
     * 전화번호로 고객 검색
     */
    public List<Customer> searchCustomersByPhone(String phone) {
        return customerRepository.findByPhoneContaining(phone);
    }
    
    /**
     * 이름으로 고객 검색
     */
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * 동 코드로 고객 검색
     */
    public List<Customer> getCustomersByDongCode(String dongCode) {
        return customerRepository.findByDongCode(dongCode);
    }
    
    /**
     * 위치 기반 고객 조회
     */
    public List<Customer> getCustomersNearLocation(Double lat, Double lng, Integer radiusM) {
        return customerRepository.findCustomersNearLocation(lat, lng, radiusM);
    }
    
    /**
     * 고객 정보 수정
     */
    public Customer updateCustomer(UUID id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("고객을 찾을 수 없습니다."));
        
        customer.setName(customerDetails.getName());
        customer.setPhone(customerDetails.getPhone());
        customer.setLat(customerDetails.getLat());
        customer.setLng(customerDetails.getLng());
        customer.setDongCode(customerDetails.getDongCode());
        
        return customerRepository.save(customer);
    }
    
    /**
     * 고객 삭제
     */
    public boolean deleteCustomer(UUID id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 고객 개수 조회
     */
    public long getCustomersCount() {
        return customerRepository.count();
    }
}
