package com.example.internetprovidermanagement.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUserDTO {
    @Valid
    @NotNull(message = "User details are required")
    private UserDTO user;
    
    @NotEmpty(message = "At least one bundle must be selected")
    private List<Long> bundleIds;
    
    @NotNull(message = "Location is required")
    private Long locationId;
}