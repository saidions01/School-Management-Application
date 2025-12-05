// backend/src/main/java/com/school/config/DataInitializer.java
@Component
@RequiredArgsConstructor
public class DataInitializer {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void init() {
        // Create default admin if not exists
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
            System.out.println("Default admin created - username: admin, password: admin123");
        }
    }
}