package com.example.internetprovidermanagement.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {
    @NotNull(message = "User details are required")
    private UserDTO user;
    
    @NotNull(message = "Bundle ID is required")
    private Long bundleId;
    
    @NotNull(message = "Location details are required")
    private LocationDTO location;
}