package com.example.internetprovidermanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.services.UserBundleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-bundles")
@RequiredArgsConstructor
public class UserBundleController {

    private final UserBundleService userBundleService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBundleDetailsDTO>> getUserBundles(@PathVariable Long userId) {
        return ResponseEntity.ok(userBundleService.getUserBundles(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBundleDetailsDTO> updateUserBundle(
            @PathVariable Long id,
            @Valid @RequestBody UserBundleDTO userBundleDTO) {
        return ResponseEntity.ok(userBundleService.updateUserBundle(id, userBundleDTO));
    }
}