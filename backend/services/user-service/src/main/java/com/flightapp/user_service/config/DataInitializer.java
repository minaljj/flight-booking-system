package com.flightapp.user_service.config;

import java.util.Set;

import org.hibernate.validator.internal.util.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.flightapp.user_service.model.Role;
import com.flightapp.user_service.model.User;
import com.flightapp.user_service.repository.RoleRepository;
import com.flightapp.user_service.repository.UserRepository;


public class DataInitializer implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
	private static final String[] BASELINE_ROLES = {
			"ROLE_USER", "ROLE_ADMIN", "ROLE_AIRLINE_MODERATOR"
	};
	
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${app.admin.username}")
	private String adminUsername;
	@Value("${app.admin.email}")
	private String adminEmail;
	@Value("${app.admin.password}")
	private String adminPassword;
	
	@Override
	public void run(String... args) {
		seedRoles();
		seedAdminUser();
	}
    private void seedRoles() {
        for (String roleName : BASELINE_ROLES) {
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Seeded role: {}", roleName);
            }   
        }
    }
private void seedAdminUser() {
    if (userRepository.existsByUsername(adminUsername)) {
        log.info("Admin user '{}' already exists, skipping bootstrap.", adminUsername);
        return;
    }

    Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not seeded yet"));

    User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword));
    admin.setRoles(Set.of(adminRole));
    userRepository.save(admin);
    log.info("Bootstrapped admin user '{}'.", adminUsername);
    }
}
