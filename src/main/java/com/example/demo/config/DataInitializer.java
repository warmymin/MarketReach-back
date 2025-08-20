package com.example.demo.config;

import com.example.demo.entity.Company;
import com.example.demo.entity.Customer;
import com.example.demo.service.CompanyService;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// @Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CustomerService customerService;

    // 한국 이름 데이터
    private static final List<String> KOREAN_NAMES = Arrays.asList(
        "김철수", "이영희", "박민수", "정수진", "최동욱", "한미영", "송태호", "윤지영", "강현우", "임서연",
        "조성민", "백지원", "오승호", "신혜진", "유재석", "전지현", "남궁민", "서예진", "권지용", "배수지",
        "홍길동", "김영수", "이미라", "박준호", "정다은", "최민호", "한소희", "송중기", "윤아", "강동원",
        "임시현", "조인성", "백종원", "오정연", "신동엽", "전도연", "남상미", "서강준", "권상우", "배두나",
        "홍석천", "김태희", "이병헌", "박보영", "정우성", "최지우", "한가인", "송혜교", "윤계상", "강소라"
    );

    // 서울시 행정구역 코드 (동 단위)
    private static final List<String> DONG_CODES = Arrays.asList(
        "1168010100", "1168010200", "1168010300", "1168010400", "1168010500", // 강남구
        "1168010600", "1168010700", "1168010800", "1168010900", "1168011000",
        "1168051000", "1168052000", "1168053000", "1168054000", "1168055000", // 강남구 (법정동)
        "1168056000", "1168057000", "1168058000", "1168059000", "1168060000",
        "1144010100", "1144010200", "1144010300", "1144010400", "1144010500", // 강서구
        "1144010600", "1144010700", "1144010800", "1144010900", "1144011000",
        "1144066000", "1144067000", "1144068000", "1144069000", "1144070000", // 강서구 (법정동)
        "1144071000", "1144072000", "1144073000", "1144074000", "1144075000",
        "1150010100", "1150010200", "1150010300", "1150010400", "1150010500", // 강북구
        "1150010600", "1150010700", "1150010800", "1150010900", "1150011000",
        "1150066000", "1150067000", "1150068000", "1150069000", "1150070000", // 강북구 (법정동)
        "1150071000", "1150072000", "1150073000", "1150074000", "1150075000"
    );

    // 전화번호 접두사
    private static final List<String> PHONE_PREFIXES = Arrays.asList("010", "011", "016", "017", "018", "019");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 데이터 초기화를 시작합니다...");
        
        // 회사 생성
        Company company = createCompanyIfNotExists();
        
        // 고객 데이터 생성
        createDummyCustomers(company);
        
        System.out.println("✅ 데이터 초기화가 완료되었습니다!");
    }

    private Company createCompanyIfNotExists() {
        // 기존 회사가 있는지 확인
        List<Company> existingCompanies = companyService.getAllCompanies();
        if (!existingCompanies.isEmpty()) {
            System.out.println("🏢 기존 회사 발견: " + existingCompanies.get(0).getName());
            return existingCompanies.get(0);
        }

        // 새 회사 생성
        Company company = new Company();
        company.setName("테스트 회사");
        company.setIndustry("IT");
        
        Company savedCompany = companyService.createCompany(company);
        System.out.println("🏢 새 회사 생성: " + savedCompany.getName() + " (ID: " + savedCompany.getId() + ")");
        
        return savedCompany;
    }

    private void createDummyCustomers(Company company) {
        // 기존 고객 수 확인
        List<Customer> existingCustomers = customerService.getAllCustomers();
        if (existingCustomers.size() >= 50) {
            System.out.println("👥 이미 충분한 고객 데이터가 있습니다: " + existingCustomers.size() + "명");
            return;
        }

        System.out.println("👥 더미 고객 데이터를 생성합니다...");
        
        int targetCount = 50 - existingCustomers.size();
        int createdCount = 0;

        for (int i = 0; i < targetCount; i++) {
            Customer customer = generateRandomCustomer(company);
            try {
                Customer savedCustomer = customerService.createCustomer(customer);
                createdCount++;
                if (createdCount % 10 == 0) {
                    System.out.println("✅ " + createdCount + "/" + targetCount + " 고객 생성 완료");
                }
            } catch (Exception e) {
                System.out.println("⚠️ 고객 생성 실패: " + customer.getName() + " - " + e.getMessage());
            }
        }

        System.out.println("👥 총 " + createdCount + "명의 고객이 생성되었습니다.");
    }

    private Customer generateRandomCustomer(Company company) {
        Customer customer = new Customer();
        
        // 랜덤 이름
        String name = KOREAN_NAMES.get((int) (Math.random() * KOREAN_NAMES.size()));
        customer.setName(name);
        
        // 랜덤 전화번호
        String prefix = PHONE_PREFIXES.get((int) (Math.random() * PHONE_PREFIXES.size()));
        String middle = String.format("%04d", (int) (Math.random() * 10000));
        String last = String.format("%04d", (int) (Math.random() * 10000));
        customer.setPhone(prefix + "-" + middle + "-" + last);
        
        // 서울시 내 랜덤 좌표
        double lat = 37.48 + Math.random() * (37.63 - 37.48); // 37.48 ~ 37.63
        double lng = 126.90 + Math.random() * (127.10 - 126.90); // 126.90 ~ 127.10
        customer.setLat(lat);
        customer.setLng(lng);
        
        // 랜덤 동 코드
        String dongCode = DONG_CODES.get((int) (Math.random() * DONG_CODES.size()));
        customer.setDongCode(dongCode);
        
        return customer;
    }
}
