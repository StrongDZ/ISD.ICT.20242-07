package com.example.aims.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.aims.security.AuthEntryPointJwt;
import com.example.aims.security.AuthTokenFilter;
import com.example.aims.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public API endpoints - MUST come first to take precedence
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/user/add").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/rush-order/check/**").permitAll()
                        .requestMatchers("/api/cancel-order/**").permitAll()
                        .requestMatchers("/api/place-order/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/orders/**").permitAll()
                        .requestMatchers("/api/manager/**").authenticated()
                        
                        // Static resources
                        .requestMatchers(
                                "/", "/index.html", "/favicon.ico", "/manifest.json",
                                "/logo192.png", "/logo512.png", "/static/**")
                        .permitAll()
                        .requestMatchers("/actuator/**", "/v3/**", "/webjars/**",
                                "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**")
                        .permitAll()
                        
                        // User endpoints require authentication
                        .requestMatchers("/api/user/**").authenticated()
                        
                        // Any other request requires authentication - this comes LAST
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer ignoreResources() {
        return webSecurity -> webSecurity
                .ignoring()
                .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js",
                        "/swagger-ui*/**", "/favicon.ico");
    }

}
