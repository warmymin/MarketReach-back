package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 전체 고객 조회
     */
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 목록 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 페이지별 고객 조회
     */
    @GetMapping("/page")
    public ResponseEntity<?> getCustomersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Customer> customers = customerService.getCustomers(PageRequest.of(page, size));
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", customers.getContent(),
                "totalElements", customers.getTotalElements(),
                "totalPages", customers.getTotalPages(),
                "currentPage", customers.getNumber()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 목록 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * ID로 고객 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {
        try {
            return customerService.getCustomerById(id)
                    .map(customer -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", customer
                    )))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 전화번호로 고객 검색
     */
    @GetMapping("/search/phone")
    public ResponseEntity<?> searchCustomersByPhone(@RequestParam String phone) {
        try {
            List<Customer> customers = customerService.searchCustomersByPhone(phone);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 검색 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 이름으로 고객 검색
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> searchCustomersByName(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.searchCustomersByName(name);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 검색 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 동 코드로 고객 검색
     */
    @GetMapping("/search/dong")
    public ResponseEntity<?> getCustomersByDongCode(@RequestParam String dongCode) {
        try {
            List<Customer> customers = customerService.getCustomersByDongCode(dongCode);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 검색 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 위치 기반 고객 조회
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> getCustomersNearLocation(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) Integer radiusM) {
        try {
            // radius 파라미터가 있으면 radiusM으로 변환, 없으면 기본값 사용
            Integer finalRadiusM = radiusM;
            if (radius != null && radiusM == null) {
                finalRadiusM = (int) (radius * 1000); // km를 m로 변환
            }
            if (finalRadiusM == null) {
                finalRadiusM = 5000; // 기본값 5km
            }
            
            List<Customer> customers = customerService.getCustomersNearLocation(lat, lng, finalRadiusM);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "customers", customers,
                "count", customers.size()
            ));
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "위치 기반 고객 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 고객 생성
     */
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        try {
            Customer created = customerService.createCustomer(customer);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "고객이 성공적으로 생성되었습니다.",
                "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 생성 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 고객 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID id, @RequestBody Customer customer) {
        try {
            Customer updated = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "고객 정보가 성공적으로 수정되었습니다.",
                "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 수정 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 고객 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
        try {
            boolean deleted = customerService.deleteCustomer(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "고객이 성공적으로 삭제되었습니다."
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "고객 삭제 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
