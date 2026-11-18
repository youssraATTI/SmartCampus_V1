package com.smartcampus.config;

import com.smartcampus.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  LoginService loginService(@Value("${smartcampus.auth.max-failed-attempts:3}") int maxFailed) {
    return new LoginService(maxFailed);
  }
}