import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "Admin@123";
        String userPassword = "User@123";
        
        String adminHash = encoder.encode(adminPassword);
        String userHash = encoder.encode(userPassword);
        
        System.out.println("\n=== BCRYPT PASSWORD HASHES ===\n");
        System.out.println("Admin Password: " + adminPassword);
        System.out.println("Admin Hash: " + adminHash);
        System.out.println("\nUser Password: " + userPassword);
        System.out.println("User Hash: " + userHash);
        
        System.out.println("\n=== SQL INSERT COMMANDS ===\n");
        System.out.println("DELETE FROM users;");
        System.out.println("\nINSERT INTO users (email, password, full_name, role, is_active)");
        System.out.println("VALUES ('admin@codecognition.ai', '" + adminHash + "', 'Admin User', 'ADMIN', TRUE);");
        System.out.println("\nINSERT INTO users (email, password, full_name, role, is_active)");
        System.out.println("VALUES ('user@codecognition.ai', '" + userHash + "', 'Demo Customer', 'USER', TRUE);");
    }
}
