package com.smartcampus.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {

  @Autowired MockMvc mockMvc;

  @Test
  void login_returnsJwt() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"etudiant@ensa.ma\",\"password\":\"motdepasse123\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty())
        .andExpect(jsonPath("$.role").value("ETUDIANT"));
  }

  @Test
  void adminDashboard_studentGets403() throws Exception {
    String body =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"etudiant@ensa.ma\",\"password\":\"motdepasse123\"}"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    String token = JsonPath.read(body, "$.token");

    mockMvc
        .perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminDashboard_adminGets200() throws Exception {
    String body =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"admin@ensa.ma\",\"password\":\"admin12345\"}"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    String token = JsonPath.read(body, "$.token");

    mockMvc
        .perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resource").value("admin"));
  }
}