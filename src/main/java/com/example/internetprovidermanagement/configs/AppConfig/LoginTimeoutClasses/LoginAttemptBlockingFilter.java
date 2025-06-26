package com.example.internetprovidermanagement.configs.AppConfig.LoginTimeoutClasses;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginAttemptBlockingFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final ObjectMapper objectMapper;

    private static final String LOGIN_PROCESSING_URL = "/api/auth/login";


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("POST") && request.getServletPath().equals(LOGIN_PROCESSING_URL)) {
            Optional<LoginAttemptService.LockoutDetails> lockoutDetailsOpt = loginAttemptService.getLockoutDetails(request);

            if (lockoutDetailsOpt.isPresent()) {
                LoginAttemptService.LockoutDetails lockoutDetails = lockoutDetailsOpt.get();
                Duration remainingDuration = lockoutDetails.getRemainingLockoutDuration();

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                Map<String, Object> data = new HashMap<>();
                data.put("error", "Too Many Requests");
                long minutes = remainingDuration.toMinutes();
                long seconds = remainingDuration.minusMinutes(minutes).getSeconds();
                data.put("message", String.format(
                        "You have been temporarily locked out due to too many failed login attempts. Please try again in %d minutes and %d seconds.",
                        minutes, seconds
                ));
                data.put("reason", lockoutDetails.getReason());
                data.put("retryAfterSeconds", remainingDuration.getSeconds());

                response.setHeader("Retry-After", String.valueOf(remainingDuration.getSeconds()));
                response.getWriter().write(objectMapper.writeValueAsString(data));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}