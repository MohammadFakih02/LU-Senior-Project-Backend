package com.example.internetprovidermanagement.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.services.BundleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bundles")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BundleDTO createBundle(@Valid @RequestBody BundleDTO bundleDTO) {
        return bundleService.createBundle(bundleDTO);
    }

    @GetMapping
    public ResponseEntity<List<BundleDTO>> getAllBundles() {
        return ResponseEntity.ok(bundleService.getAllBundles());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<BundleDTO>> getBundlesByType(@PathVariable Bundle.BundleType type) {
        return ResponseEntity.ok(bundleService.getBundlesByType(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BundleDTO> getBundleById(@PathVariable Long id) {
        return ResponseEntity.ok(bundleService.getBundleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BundleDTO> updateBundle(
            @PathVariable Long id, 
            @Valid @RequestBody BundleDTO bundleDTO) {
        return ResponseEntity.ok(bundleService.updateBundle(id, bundleDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBundle(@PathVariable Long id) {
        bundleService.deleteBundle(id);
    }
}