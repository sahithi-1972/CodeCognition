package com.codecognition;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        String adminPass = "Admin@123";
        String userPass = "User@123";
        
        String adminHash = encoder.encode(adminPass);
        String userHash = encoder.encode(userPass);
        
        System.out.println("Admin Password: " + adminPass);
        System.out.println("Admin Hash: " + adminHash);
        System.out.println("Verify Admin: " + encoder.matches(adminPass, adminHash));
        System.out.println();
        System.out.println("User Password: " + userPass);
        System.out.println("User Hash: " + userHash);
        System.out.println("Verify User: " + encoder.matches(userPass, userHash));
        System.out.println();
        System.out.println("INSERT INTO users (email, password, full_name, role, is_active) VALUES");
        System.out.println("('admin@codecognition.ai', '" + adminHash + "', 'Admin User', 'ADMIN', TRUE),");
        System.out.println("('user@codecognition.ai', '" + userHash + "', 'Demo Customer', 'USER', TRUE);");
    }
}
