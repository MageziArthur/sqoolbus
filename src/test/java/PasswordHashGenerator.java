import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        
        // Test if the existing hash works
        String existingHash = "$2a$10$rQ5x5rV4sXrOj9Wm5EQcF.m5MeZf9kM6XHv9pR3hL7dJ8wCq2Zx9e";
        boolean matches = encoder.matches(password, existingHash);
        System.out.println("Does existing hash match 'admin123'? " + matches);
        
        // Test if the new hash works
        boolean newMatches = encoder.matches(password, hashedPassword);
        System.out.println("Does new hash match 'admin123'? " + newMatches);
    }
}