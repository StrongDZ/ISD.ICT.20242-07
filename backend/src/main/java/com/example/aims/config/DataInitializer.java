package com.example.aims.config;

import com.example.aims.model.Users;
import com.example.aims.repository.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsersRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
            Users admin = new Users();
            admin.setId(UUID.randomUUID().toString());
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMINISTRATOR");
            userRepository.save(admin);
            System.out.println("Admin user created");
        }

        // Create product manager if not exists
        if (!userRepository.existsByUsername("manager")) {
            Users manager = new Users();
            manager.setId(UUID.randomUUID().toString());
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole("PRODUCT_MANAGER");
            userRepository.save(manager);
            System.out.println("Manager user created");
        }

        // Create customer if not exists
        if (!userRepository.existsByUsername("customer")) {
            Users customer = new Users();
            customer.setId(UUID.randomUUID().toString());
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setRole("CUSTOMER");
            userRepository.save(customer);
            System.out.println("Customer user created");
        }
    }
}