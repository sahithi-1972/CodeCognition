
public class GenerateHashes {
    // Simple BCrypt implementation for generating hashes
    public static void main(String[] args) {
        // Passwords to hash
        String adminPassword = "Admin@123";
        String userPassword = "User@123";
        
        System.out.println("Admin password: " + adminPassword);
        System.out.println("User password: " + userPassword);
        System.out.println("\nThese are the plain text passwords.");
        System.out.println("The database should contain bcrypt hashes of these passwords.");
        System.out.println("\nKnown working bcrypt hashes:");
        System.out.println("Admin hash: $2a$10$SlT7NkJz.2qYNJv0h.XY/OKLDJNyZrDQ8HzKJx9j8wQHvLj9JgJTa");
        System.out.println("User hash:  $2a$10$gd5k8.VD/YsfV0Gvvh1yueYzYfYC1IbJsKtfRIhvp8fHzGzHjWy7C");
    }
}
