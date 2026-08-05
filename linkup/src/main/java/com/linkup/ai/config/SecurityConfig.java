package com.linkup.ai.config;

import com.linkup.ai.security.JwtAuthFilter;
import com.linkup.ai.security.JwtUtil;
import com.linkup.ai.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
@EnableMethodSecurity
public class SecurityConfig {

    public SecurityConfig() {
    }

    // PasswordEncoder is provided by AppConfig to avoid bean duplication

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http, JwtUtil jwtUtil, com.linkup.ai.service.UserService userService, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil);
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider(userService, passwordEncoder))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // allow frames for H2 console
        http.headers().frameOptions().sameOrigin();

        return http.build();
    }
}
