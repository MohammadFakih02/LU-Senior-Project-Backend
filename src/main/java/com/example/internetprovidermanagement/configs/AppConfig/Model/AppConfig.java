// AppConfig.java
package com.example.internetprovidermanagement.configs.AppConfig.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_config", uniqueConstraints = @UniqueConstraint(columnNames = "config_key"))
@Getter
@Setter
public class AppConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;
    
    @Column(name = "config_value")
    private String configValue;
    
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;
}