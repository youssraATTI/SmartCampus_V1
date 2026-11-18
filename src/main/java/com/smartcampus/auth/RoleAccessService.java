package com.smartcampus.auth;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleAccessService {

    public boolean canAccess(UserRole userRole, String resource) {
        if (userRole == null || resource == null) {
            return false;
        }
        return switch (resource) {
            case "student" -> Set.of(UserRole.ETUDIANT, UserRole.PROFESSEUR, UserRole.PERSONNEL, UserRole.ADMIN)
                    .contains(userRole);
            case "professor" -> Set.of(UserRole.PROFESSEUR, UserRole.ADMIN).contains(userRole);
            case "admin" -> userRole == UserRole.ADMIN;
            default -> false;
        };
    }
}