package com.codecognition;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hashes for demo users
 * Run this in any Spring Boot context or standalone
 */
public class GeneratePasswordHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        String adminPass = "Admin@123";
        String userPass = "User@123";
        
        String adminHash = encoder.encode(adminPass);
        String userHash = encoder.encode(userPass);
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        CODECOGNITION - PASSWORD HASH GENERATOR              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("ADMIN USER:");
        System.out.println("  Password: " + adminPass);
        System.out.println("  Hash: " + adminHash);
        System.out.println("\nUSER USER:");
        System.out.println("  Password: " + userPass);
        System.out.println("  Hash: " + userHash);
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          COPY-PASTE INTO MYSQL (One at a time)             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("DELETE FROM users;\n");
        
        System.out.println("INSERT INTO users (email, password, full_name, role, is_active) VALUES");
        System.out.println("('admin@codecognition.ai', '" + adminHash + "', 'Admin User', 'ADMIN', TRUE);\n");
        
        System.out.println("INSERT INTO users (email, password, full_name, role, is_active) VALUES");
        System.out.println("('user@codecognition.ai', '" + userHash + "', 'Demo Customer', 'USER', TRUE);\n");
        
        System.out.println("SELECT id, email, full_name, role FROM users;\n");
    }
}
