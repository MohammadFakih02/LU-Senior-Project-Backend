package com.example.internetprovidermanagement.dtos; // Or your appropriate DTOs package

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}