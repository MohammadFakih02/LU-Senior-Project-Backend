package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;

@Service
@Transactional
public class BundleService {

    private final BundleRepository bundleRepository;
    private final ModelMapper modelMapper;

    // Constructor injection
    public BundleService(BundleRepository bundleRepository, ModelMapper modelMapper) {
        this.bundleRepository = bundleRepository;
        this.modelMapper = modelMapper;
    }

    // Create a new Bundle
    public BundleDTO createBundle(BundleDTO bundleDTO) {
        Bundle bundle = modelMapper.toBundle(bundleDTO);
        Bundle savedBundle = bundleRepository.save(bundle);
        return modelMapper.toBundleDTO(savedBundle);
    }

    // Get all Bundles
    public List<BundleDTO> getAllBundles() {
        return bundleRepository.findAll().stream()
                .map(modelMapper::toBundleDTO)
                .collect(Collectors.toList());
    }

    // Get Bundles by Type (using custom repository method)
    public List<BundleDTO> getBundlesByType(String type) {
        return bundleRepository.findByType(type).stream()
                .map(modelMapper::toBundleDTO)
                .collect(Collectors.toList());
    }

    // Get a single Bundle by ID
    public BundleDTO getBundleById(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        return modelMapper.toBundleDTO(bundle);
    }

    // Update a Bundle
    public BundleDTO updateBundle(Long id, BundleDTO bundleDTO) {
        Bundle existingBundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));

        // Update fields from DTO
        existingBundle.setName(bundleDTO.getName());
        existingBundle.setDescription(bundleDTO.getDescription());
        existingBundle.setType(bundleDTO.getType());
        existingBundle.setPrice(bundleDTO.getPrice());
        existingBundle.setDataCap(bundleDTO.getDataCap());
        existingBundle.setSpeed(bundleDTO.getSpeed());

        Bundle updatedBundle = bundleRepository.save(existingBundle);
        return modelMapper.toBundleDTO(updatedBundle);
    }

    // Delete a Bundle
    public void deleteBundle(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        bundleRepository.delete(bundle);
    }
}