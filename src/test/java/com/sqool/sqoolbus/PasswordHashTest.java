package com.sqool.sqoolbus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordHashTest {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void generatePasswordHash() {
        String password = "admin123";
        String hashedPassword = passwordEncoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        
        // Test if the existing hash works
        String existingHash = "$2a$10$rQ5x5rV4sXrOj9Wm5EQcF.m5MeZf9kM6XHv9pR3hL7dJ8wCq2Zx9e";
        boolean matches = passwordEncoder.matches(password, existingHash);
        System.out.println("Does existing hash match 'admin123'? " + matches);
        
        // Test if the new hash works
        boolean newMatches = passwordEncoder.matches(password, hashedPassword);
        System.out.println("Does new hash match 'admin123'? " + newMatches);
    }
}