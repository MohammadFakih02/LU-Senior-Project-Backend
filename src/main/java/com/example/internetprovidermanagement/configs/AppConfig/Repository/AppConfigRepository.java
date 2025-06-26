// AppConfigRepository.java
package com.example.internetprovidermanagement.configs.AppConfig.Repository;


import com.example.internetprovidermanagement.configs.AppConfig.Model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
    Optional<AppConfig> findByConfigKey(String configKey);
    boolean existsByConfigKey(String configKey);
}