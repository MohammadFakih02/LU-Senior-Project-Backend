package com.example.internetprovidermanagement.configs.AppConfig.Service;

import com.example.internetprovidermanagement.configs.AppConfig.Service.AppConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppConfigService appConfigService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String configuredUsername;
        String configuredPasswordHash;

        try {
            configuredUsername = appConfigService.getAdminUsername();
            configuredPasswordHash = appConfigService.getAdminPasswordHash();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Admin user configuration not found or accessible.", e);
        }

        if (username.equals(configuredUsername)) {
            return new User(configuredUsername,
                            configuredPasswordHash,
                            Collections.emptyList());
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}