package com.example.demo.controller;

import com.example.demo.entity.Company;
import com.example.demo.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    /**
     * 회사 생성 API
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@Valid @RequestBody Company company) {
        try {
            Company createdCompany = companyService.createCompany(company);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회사가 성공적으로 생성되었습니다.");
            response.put("data", createdCompany);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 생성 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 모든 회사 조회 API
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        try {
            List<Company> companies = companyService.getAllCompanies();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 특정 회사 조회 API
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompanyById(@PathVariable UUID id) {
        try {
            Optional<Company> company = companyService.getCompanyById(id);
            
            if (company.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", company.get());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "회사를 찾을 수 없습니다.");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 회사명으로 검색 API
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String industry) {
        try {
            List<Company> companies;
            
            if (name != null && !name.trim().isEmpty()) {
                companies = companyService.searchCompaniesByName(name);
            } else if (industry != null && !industry.trim().isEmpty()) {
                companies = companyService.searchCompaniesByIndustry(industry);
            } else {
                companies = companyService.getAllCompanies();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companies);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 검색 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 회사 정보 수정 API
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompany(
            @PathVariable UUID id, 
            @Valid @RequestBody Company companyDetails) {
        try {
            Company updatedCompany = companyService.updateCompany(id, companyDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회사 정보가 성공적으로 수정되었습니다.");
            response.put("data", updatedCompany);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 수정 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 회사 삭제 API
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCompany(@PathVariable UUID id) {
        try {
            companyService.deleteCompany(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회사가 성공적으로 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회사 삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 회사 통계 정보 조회 API
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCompanyStats() {
        try {
            List<Object[]> companiesWithStats = companyService.getCompaniesWithStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", companiesWithStats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
