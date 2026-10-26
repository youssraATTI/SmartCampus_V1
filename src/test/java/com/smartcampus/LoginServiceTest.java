package com.smartcampus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    
    @Test
    void testSuccessfulLogin() {
        LoginService service = new LoginService();
        assertTrue(service.authenticate("etudiant@ensak.ac.ma", "SmartCampus2026!"));
    }
}