package com.example.internetprovidermanagement.controllers;

import com.example.internetprovidermanagement.models.Bundle;
import org.springframework.web.bind.annotation.*;
import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.services.BundleService;
import java.util.List;

@RestController
@RequestMapping("/api/bundles")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    @PostMapping
    public BundleDTO createBundle(@RequestBody BundleDTO bundleDTO) {
        return bundleService.createBundle(bundleDTO);
    }

    @GetMapping
    public List<BundleDTO> getAllBundles() {
        return bundleService.getAllBundles();
    }

    @GetMapping("/type/{type}")
    public List<BundleDTO> getBundlesByType(@PathVariable String type) {
        return bundleService.getBundlesByType(Bundle.BundleType.DSL);
    }

    @GetMapping("/{id}")
    public BundleDTO getBundleById(@PathVariable Long id) {
        return bundleService.getBundleById(id);
    }

    @PutMapping("/{id}")
    public BundleDTO updateBundle(@PathVariable Long id, @RequestBody BundleDTO bundleDTO) {
        return bundleService.updateBundle(id, bundleDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBundle(@PathVariable Long id) {
        bundleService.deleteBundle(id);
    }
}