package com.smartcampus.auth;

import java.util.Locale;

public enum UserRole {
    ETUDIANT,
    PROFESSEUR,
    PERSONNEL,
    ADMIN;

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role manquant.");
        }
        return UserRole.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}