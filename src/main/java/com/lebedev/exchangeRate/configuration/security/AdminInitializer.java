package com.lebedev.exchangeRate.configuration.security;

import com.lebedev.exchangeRate.entity.AppUser;
import com.lebedev.exchangeRate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminInitializer {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdmin() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            AppUser adminAppUser = new AppUser();
            adminAppUser.setUsername(adminUsername);
            adminAppUser.setPassword(passwordEncoder.encode(adminPassword));
            adminAppUser.setRoles(Collections.singletonList("ADMIN"));
            userRepository.save(adminAppUser);
            log.info("Admin user created successfully.");
        } else {
            log.info("Admin user already exists.");
        }
    }
}

