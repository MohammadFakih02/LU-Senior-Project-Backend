package com.example.internetprovidermanagement.configs.AppConfig.Model;


import com.example.internetprovidermanagement.configs.AppConfig.Service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() { // Made static if not already, or ensure it's created before dependent beans
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simpler API auth initially
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login", "/api/auth/status").permitAll() // Allow login and status check
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests for CORS preflight
                        .requestMatchers("/api/**").authenticated() // Secure all other /api/** endpoints
                        .anyRequest().permitAll() // Allow access to non-API paths (e.g. static content, actuator if any) - adjust as needed
                )
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("username") // Ensure frontend sends 'username'
                        .passwordParameter("password") // Ensure frontend sends 'password'
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().flush();
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Authentication failed: " + exception.getMessage());
                            response.getWriter().flush();
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout successful");
                            response.getWriter().flush();
                        })
                        .deleteCookies("JSESSIONID") // Important for session invalidation
                        .invalidateHttpSession(true)
                )
                .exceptionHandling(exceptions -> exceptions
                        // This handles cases where an unauthenticated user tries to access a secured resource
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}