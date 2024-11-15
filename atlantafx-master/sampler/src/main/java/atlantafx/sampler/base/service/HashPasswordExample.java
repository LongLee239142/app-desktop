package atlantafx.sampler.base.service;

import atlantafx.sampler.base.util.PasswordUtils;

public class HashPasswordExample {
    public static void main(String[] args) {
        // The plaintext password to hash
        String plainPassword = "0000";

        // Generate the hash
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        // Output the hashed password
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
    }
}
