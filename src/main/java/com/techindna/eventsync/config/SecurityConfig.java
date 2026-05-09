package com.techindna.eventsync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenProvider tokenProvider) {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/events/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/events/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/events/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/events/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/speakers/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/speakers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/speakers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/speakers/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/rooms/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/rooms/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET,"/sessions/**").permitAll()
                    .requestMatchers(HttpMethod.POST,"/sessions/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,"/sessions/**").hasRole("ADMIN")

                .requestMatchers("/auth/login").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
