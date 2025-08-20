package com.example.demo.controller;

import com.example.demo.entity.Campaign;
import com.example.demo.entity.Company;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Delivery;
import com.example.demo.entity.TargetingLocation;
import com.example.demo.service.CampaignService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.DeliveryService;
import com.example.demo.service.TargetingLocationService;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.CampaignRepository;
import java.util.Arrays;
import com.example.demo.repository.TargetingLocationRepository;
import com.example.demo.repository.DeliveryRepository;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private CampaignService campaignService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private TargetingLocationService targetingLocationService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private DeliveryService deliveryService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private TargetingLocationRepository targetingLocationRepository;
    
    @Autowired
    private DeliveryRepository deliveryRepository;

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "JSON 데이터가 성공적으로 수신되었습니다.");
        response.put("received", data);
        return response;
    }

    @PostMapping("/simple")
    public Map<String, Object> simple(@RequestBody String data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "문자열 데이터가 성공적으로 수신되었습니다.");
        response.put("received", data);
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "테스트 컨트롤러가 정상 작동합니다.");
        return response;
    }

    /**
     * 간단한 캠페인 생성 테스트
     */
    @PostMapping("/create-campaign")
    public Map<String, Object> createSimpleCampaign(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "캠페인 생성 요청이 성공적으로 수신되었습니다.");
        response.put("received", data);
        return response;
    }

    /**
     * 위치 기반 타겟팅 생성 테스트
     */
    @PostMapping("/create-targeting-location")
    public Map<String, Object> createTargetingLocation(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "위치 기반 타겟팅 생성 요청이 성공적으로 수신되었습니다.");
        response.put("received", data);
        return response;
    }

    /**
     * 종합 더미 데이터 생성
     */
    @PostMapping("/create-dummy-data")
    public Map<String, Object> createDummyData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 회사 생성
            Company company1 = new Company();
            company1.setName("KT Corporation");
            company1.setBusinessNumber("123-45-67890");
            company1.setAddress("서울특별시 강남구 테헤란로 152");
            company1.setPhone("02-1234-5678");
            company1.setEmail("contact@kt.com");
            company1 = companyService.createCompany(company1);
            
            Company company2 = new Company();
            company2.setName("SK Telecom");
            company2.setBusinessNumber("987-65-43210");
            company2.setAddress("서울특별시 중구 을지로 65");
            company2.setPhone("02-9876-5432");
            company2.setEmail("contact@sk.com");
            company2 = companyService.createCompany(company2);
            
            // 2. 고객 생성 (위치 정보 포함)
            List<Customer> customers = new ArrayList<>();
            
            // 강남역 근처 고객들
            customers.add(createCustomer("김철수", "010-1111-1111", 37.498000, 127.027600, "1168010100"));
            customers.add(createCustomer("이영희", "010-1111-1112", 37.497500, 127.028000, "1168010100"));
            customers.add(createCustomer("박민수", "010-1111-1113", 37.498500, 127.027000, "1168010100"));
            
            // 홍대입구 근처 고객들
            customers.add(createCustomer("최지영", "010-2222-2221", 37.557000, 126.925000, "1144012400"));
            customers.add(createCustomer("정수민", "010-2222-2222", 37.556500, 126.925500, "1144012400"));
            
            // 신촌역 근처 고객들
            customers.add(createCustomer("한미영", "010-3333-3331", 37.555000, 126.936000, "1144012400"));
            customers.add(createCustomer("송태호", "010-3333-3332", 37.555500, 126.936500, "1144012400"));
            
            // 3. 위치 기반 타겟팅 생성
            TargetingLocation targeting1 = new TargetingLocation();
            targeting1.setName("강남역 핫플");
            targeting1.setCenterLat(37.498000);
            targeting1.setCenterLng(127.027600);
            targeting1.setRadiusM(5000); // 5km
            targeting1.setMemo("강남역 기준 5km 반경");
            targeting1.setCompany(company1);
            targeting1 = targetingLocationService.createTargetingLocation(targeting1);
            
            TargetingLocation targeting2 = new TargetingLocation();
            targeting2.setName("홍대 문화지구");
            targeting2.setCenterLat(37.557000);
            targeting2.setCenterLng(126.925000);
            targeting2.setRadiusM(3000); // 3km
            targeting2.setMemo("홍대입구 기준 3km 반경");
            targeting2.setCompany(company2);
            targeting2 = targetingLocationService.createTargetingLocation(targeting2);
            
            // 4. 캠페인 생성
            Campaign campaign1 = new Campaign();
            campaign1.setName("강남역 핫플 프로모션");
            campaign1.setMessage("강남역 근처 고객 대상 특별 할인");
            campaign1.setTargetingLocation(targeting1);
            campaign1.setDescription("강남역 근처 고객 대상 특별 할인");

            campaign1.setStatus("ACTIVE");
            campaign1.setCompany(company1);
            campaign1 = campaignService.createCampaign(campaign1);
            
            Campaign campaign2 = new Campaign();
            campaign2.setName("홍대 문화축제");
            campaign2.setMessage("홍대입구 근처 고객 대상 문화 이벤트");
            campaign2.setTargetingLocation(targeting2);
            campaign2.setDescription("홍대입구 근처 고객 대상 문화 이벤트");

            campaign2.setStatus("ACTIVE");
            campaign2.setCompany(company2);
            campaign2 = campaignService.createCampaign(campaign2);
            

            
            // 5. 배송 데이터 생성
            for (Customer customer : customers) {
                Delivery delivery = new Delivery(campaign1, customer);
                delivery.setStatus(Delivery.DeliveryStatus.SENT);
                delivery.setMessageTextSent("안녕하세요! 특별한 혜택을 확인해보세요.");
                delivery.setCreatedAt(LocalDateTime.now());
                delivery.setSentAt(LocalDateTime.now().plusMinutes(5));
                deliveryRepository.save(delivery);
            }
            
            response.put("success", true);
            response.put("message", "더미 데이터가 성공적으로 생성되었습니다.");
            response.put("data", Map.of(
                "companies", 2,
                "customers", customers.size(),
                "campaigns", 2,
                "targetingLocations", 2,
                "deliveries", customers.size()
            ));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "더미 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    // 기존 캠페인들을 타겟팅 위치와 연결
    @PostMapping("/fix-campaign-targeting")
    public ResponseEntity<?> fixCampaignTargeting() {
        try {
            // 강남역 핫플 타겟팅 ID
            UUID gangnamTargetingId = UUID.fromString("04d785e1-b422-4b60-a362-c0aa11dec957");
            // 홍대 문화지구 타겟팅 ID
            UUID hongdaeTargetingId = UUID.fromString("c69c20e6-b090-4756-8351-d76846220597");
            // 테스트 타겟팅 ID
            UUID testTargetingId = UUID.fromString("74d686be-32fd-4eb4-9dc4-03dc0cb32608");
            // 강남역 핫플입니다 타겟팅 ID
            UUID gangnam2TargetingId = UUID.fromString("7bb476b8-95c7-413e-9156-637acadca224");
            
            // 캠페인들을 타겟팅과 연결
            List<Campaign> campaigns = campaignRepository.findAll();
            int updatedCount = 0;
            
            for (Campaign campaign : campaigns) {
                if (campaign.getTargetingLocation() == null) {
                    // 캠페인 이름에 따라 적절한 타겟팅 할당
                    if (campaign.getName().contains("강남") || campaign.getName().contains("핫플")) {
                        if (campaign.getName().contains("핫플입니다")) {
                            campaign.setTargetingLocation(targetingLocationRepository.findById(gangnam2TargetingId).orElse(null));
                        } else {
                            campaign.setTargetingLocation(targetingLocationRepository.findById(gangnamTargetingId).orElse(null));
                        }
                    } else if (campaign.getName().contains("홍대") || campaign.getName().contains("문화")) {
                        campaign.setTargetingLocation(targetingLocationRepository.findById(hongdaeTargetingId).orElse(null));
                    } else if (campaign.getName().contains("테스트")) {
                        campaign.setTargetingLocation(targetingLocationRepository.findById(testTargetingId).orElse(null));
                    } else {
                        // 기본값으로 강남역 타겟팅 사용
                        campaign.setTargetingLocation(targetingLocationRepository.findById(gangnamTargetingId).orElse(null));
                    }
                    
                    // 상태를 DRAFT로 변경
                    campaign.setStatus("DRAFT");
                    campaignRepository.save(campaign);
                    updatedCount++;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", updatedCount + "개의 캠페인이 타겟팅 위치와 연결되었습니다.",
                "updatedCount", updatedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "캠페인 타겟팅 연결 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    private Customer createCustomer(String name, String phone, Double lat, Double lng, String dongCode) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setLat(lat);
        customer.setLng(lng);
        customer.setDongCode(dongCode);
        return customerService.createCustomer(customer);
    }
}
