package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.BundleMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BundleService {
    private final BundleRepository bundleRepository;
    private final BundleMapper bundleMapper;

    public BundleDTO createBundle(BundleDTO bundleDTO) {
        Bundle bundle = bundleMapper.toEntity(bundleDTO);
        Bundle savedBundle = bundleRepository.save(bundle);
        return bundleMapper.toDto(savedBundle);
    }

    public List<BundleDTO> getAllBundles() {
        return bundleRepository.findAll().stream()
                .map(bundleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BundleDTO> getBundlesByType(Bundle.BundleType type) {
        return bundleRepository.findByType(type).stream()
                .map(bundleMapper::toDto)
                .collect(Collectors.toList());
    }

    public BundleDTO getBundleById(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        return bundleMapper.toDto(bundle);
    }

    public BundleDTO updateBundle(Long id, BundleDTO bundleDTO) {
        Bundle existingBundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        
        bundleMapper.updateBundleFromDto(bundleDTO, existingBundle);
        Bundle updatedBundle = bundleRepository.save(existingBundle);
        return bundleMapper.toDto(updatedBundle);
    }

    public void deleteBundle(Long id) {
        if (!bundleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bundle not found with ID: " + id);
        }
        bundleRepository.deleteById(id);
    }
}