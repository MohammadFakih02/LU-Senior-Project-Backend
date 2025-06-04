package com.example.internetprovidermanagement.configs.AppConfig.LoginTimeoutClasses;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        loginAttemptService.loginSucceeded(request);
    }
}