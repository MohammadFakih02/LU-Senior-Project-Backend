package com.example.internetprovidermanagement.dtos;

import java.util.Set;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String landLine;
    private String phone;
    private String status;
    private LocationDTO location;
    private Set<UserBundleDetailsDTO> bundles;
}