package com.smartcampus.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleAccessServiceTest {

  private final RoleAccessService service = new RoleAccessService();

  @Test
  void studentResource_allowedForAllRoles() {
    assertTrue(service.canAccess(UserRole.ETUDIANT, "student"));
    assertTrue(service.canAccess(UserRole.ADMIN, "student"));
  }

  @Test
  void adminResource_forbiddenForStudent() {
    assertFalse(service.canAccess(UserRole.ETUDIANT, "admin"));
    assertTrue(service.canAccess(UserRole.ADMIN, "admin"));
  }
}