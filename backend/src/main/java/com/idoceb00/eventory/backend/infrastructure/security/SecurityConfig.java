package com.idoceb00.eventory.backend.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${app.cors.allowed-origins:http://localhost:3000}")
  private String allowedOrigins;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityContextRepository securityContextRepository() {
    return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    (request, response, authException) -> {
                      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                      response.setContentType("application/json");
                      response
                          .getWriter()
                          .write(
                              "{\"status\":401,\"title\":\"Unauthorized\",\"type\":\"about:blank\","
                                  + "\"detail\":\"Authentication required\"}");
                    }))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login")
                    .permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/equipment")
                    .hasRole("ADMINISTRATOR")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.PATCH, "/api/equipment/*/stock")
                    .hasRole("ADMINISTRATOR")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST, "/api/equipment/*/decatalogue")
                    .hasRole("ADMINISTRATOR")
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST, "/api/equipment/*/recatalogue")
                    .hasRole("ADMINISTRATOR")
                    .anyRequest()
                    .authenticated())
        .securityContext(ctx -> ctx.securityContextRepository(securityContextRepository));
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(java.util.Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(
        java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
