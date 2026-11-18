package com.smartcampus.web;

import com.smartcampus.auth.JwtTokenService;
import com.smartcampus.auth.RoleAccessService;
import com.smartcampus.auth.UserRole;
import com.smartcampus.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

  private final LoginService loginService;
  private final JwtTokenService jwtTokenService;
  private final RoleAccessService roleAccessService;

  public AuthController(
      LoginService loginService, JwtTokenService jwtTokenService, RoleAccessService roleAccessService) {
    this.loginService = loginService;
    this.jwtTokenService = jwtTokenService;
    this.roleAccessService = roleAccessService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest body) {
    LoginService.LoginResult result = loginService.login(body.email(), body.password());
    if (!result.success()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", result.message()));
    }
    UserRole role = UserRole.fromString(result.role());
    String token = jwtTokenService.generate(body.email(), role);
    return ResponseEntity.ok(Map.of("token", token, "role", role.name()));
  }

  @GetMapping("/student/home")
  public ResponseEntity<?> studentHome(@RequestHeader(value = "Authorization", required = false) String auth) {
    return protectedResource(auth, "student", "Espace etudiant");
  }

  @GetMapping("/professor/courses")
  public ResponseEntity<?> professorCourses(@RequestHeader(value = "Authorization", required = false) String auth) {
    return protectedResource(auth, "professor", "Cours professeur");
  }

  @GetMapping("/admin/dashboard")
  public ResponseEntity<?> adminDashboard(@RequestHeader(value = "Authorization", required = false) String auth) {
    return protectedResource(auth, "admin", "Dashboard admin");
  }

  private ResponseEntity<?> protectedResource(String authHeader, String resource, String label) {
    JwtTokenService.JwtClaims claims = parseBearer(authHeader);
    if (claims == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token manquant ou invalide."));
    }
    if (!roleAccessService.canAccess(claims.role(), resource)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Acces refuse pour ce role."));
    }
    return ResponseEntity.ok(Map.of("resource", resource, "label", label, "role", claims.role().name()));
  }

  private JwtTokenService.JwtClaims parseBearer(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    String token = authHeader.substring("Bearer ".length()).trim();
    try {
      return jwtTokenService.validate(token);
    } catch (Exception ex) {
      return null;
    }
  }

  public record LoginRequest(String email, String password) {}
}