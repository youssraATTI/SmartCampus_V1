package com.smartcampus.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenServiceTest {

  @Test
  void generateThenValidate_returnsSubjectAndRole() {
    JwtTokenService service = new JwtTokenService("test-secret-minimum-32-characters-long", 600);
    String token = service.generate("samira@ensa.ma", UserRole.ADMIN);
    JwtTokenService.JwtClaims claims = service.validate(token);
    assertEquals("samira@ensa.ma", claims.email());
    assertEquals(UserRole.ADMIN, claims.role());
  }

  @Test
  void validate_withTamperedToken_throws() {
    JwtTokenService service = new JwtTokenService("test-secret-minimum-32-characters-long", 600);
    String token = service.generate("user@ensa.ma", UserRole.ETUDIANT) + "x";
    assertThrows(Exception.class, () -> service.validate(token));
  }
}