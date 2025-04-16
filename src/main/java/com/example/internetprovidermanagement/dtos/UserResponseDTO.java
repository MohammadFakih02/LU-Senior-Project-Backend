package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String landLine;
    private String phone;
    private LocalDate subscriptionDate;
    private String status; 
    private LocationDTO location;
    private Set<String> bundleNames;
}