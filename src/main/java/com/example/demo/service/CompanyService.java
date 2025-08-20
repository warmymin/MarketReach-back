package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    /**
     * 회사 생성
     */
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }
    
    /**
     * 모든 회사 조회
     */
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
    
    /**
     * 회사 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<Company> getCompanyById(UUID id) {
        return companyRepository.findById(id);
    }
    
    /**
     * 회사명으로 검색
     */
    @Transactional(readOnly = true)
    public List<Company> searchCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * 업종으로 검색
     */
    @Transactional(readOnly = true)
    public List<Company> searchCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustryContainingIgnoreCase(industry);
    }
    
    /**
     * 회사 정보 수정
     */
    public Company updateCompany(UUID id, Company companyDetails) {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            company.setName(companyDetails.getName());
            company.setIndustry(companyDetails.getIndustry());
            return companyRepository.save(company);
        }
        throw new RuntimeException("회사를 찾을 수 없습니다: " + id);
    }
    
    /**
     * 회사 삭제
     */
    public void deleteCompany(UUID id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else {
            throw new RuntimeException("회사를 찾을 수 없습니다: " + id);
        }
    }
    
    /**
     * 회사 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return companyRepository.existsById(id);
    }
    
    /**
     * 회사 통계 정보 조회
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCompaniesWithStats() {
        return companyRepository.findCompaniesWithStats();
    }
    
    /**
     * 회사 개수 조회
     */
    @Transactional(readOnly = true)
    public long getCompaniesCount() {
        return companyRepository.count();
    }
    
    /**
     * 특정 회사의 캠페인 수 조회
     */
    @Transactional(readOnly = true)
    public Long getCampaignCountByCompanyId(UUID companyId) {
        return companyRepository.countCampaignsByCompanyId(companyId);
    }
}
