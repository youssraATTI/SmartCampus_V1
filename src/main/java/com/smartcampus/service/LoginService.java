package com.smartcampus.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de connexion (AUTH). Complété par SC-11 ; tests dans SC-14.
 */
public class LoginService {

    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final int maxFailedAttempts;

    public LoginService() {
        this(3);
    }

    public LoginService(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
        registerUser("etudiant@ensa.ma", "motdepasse123", "ETUDIANT");
        registerUser("admin@ensa.ma", "admin12345", "ADMIN");
        registerUser("prof@ensa.ma", "prof12345", "PROFESSEUR");
    }

    public void registerUser(String email, String password) {
        registerUser(email, password, "ETUDIANT");
    }

    public void registerUser(String email, String password, String role) {
        users.put(email, new UserAccount(password, role));
    }

    public LoginResult login(String email, String password) {
        if (email == null || email.isBlank()) {
            return LoginResult.failure("L'e-mail est obligatoire.");
        }
        if (password == null || password.isBlank()) {
            return LoginResult.failure("Le mot de passe est obligatoire.");
        }

        UserAccount account = users.get(email);
        if (account != null && account.password().equals(password)) {
            failedAttempts.remove(email);
            return LoginResult.ok(account.role());
        }

        int attempts = failedAttempts.merge(email, 1, Integer::sum);
        if (attempts >= maxFailedAttempts) {
            return LoginResult.failure("Compte temporairement bloqué après plusieurs tentatives.");
        }
        return LoginResult.failure("E-mail ou mot de passe incorrect.");
    }

    public int getFailedAttempts(String email) {
        return failedAttempts.getOrDefault(email, 0);
    }

    public record UserAccount(String password, String role) {}

    public record LoginResult(boolean success, String message, String role) {
        public static LoginResult ok(String role) {
            return new LoginResult(true, null, role);
        }

        public static LoginResult failure(String message) {
            return new LoginResult(false, message, null);
        }
    }
}
