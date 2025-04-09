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

@Service
@Transactional
public class BundleService {

    private final BundleRepository bundleRepository;
    private final BundleMapper bundleMapper;

    public BundleService(BundleRepository bundleRepository, BundleMapper bundleMapper) {
        this.bundleRepository = bundleRepository;
        this.bundleMapper = bundleMapper;
    }

    public BundleDTO createBundle(BundleDTO bundleDTO) {
        Bundle bundle = bundleMapper.toBundle(bundleDTO);
        Bundle savedBundle = bundleRepository.save(bundle);
        return bundleMapper.toBundleDTO(savedBundle);
    }

    public List<BundleDTO> getAllBundles() {
        return bundleRepository.findAll().stream()
                .map(bundleMapper::toBundleDTO)
                .collect(Collectors.toList());
    }

    public List<BundleDTO> getBundlesByType(Bundle.BundleType type) {
        return bundleRepository.findByType(type).stream()
                .map(bundleMapper::toBundleDTO)
                .collect(Collectors.toList());
    }

    public BundleDTO getBundleById(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        return bundleMapper.toBundleDTO(bundle);
    }

    public BundleDTO updateBundle(Long id, BundleDTO bundleDTO) {
        Bundle existingBundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));

        existingBundle.setName(bundleDTO.getName());
        existingBundle.setDescription(bundleDTO.getDescription());
        existingBundle.setType(bundleDTO.getType());
        existingBundle.setPrice(bundleDTO.getPrice());
        existingBundle.setDataCap(bundleDTO.getDataCap());
        existingBundle.setSpeed(bundleDTO.getSpeed());

        Bundle updatedBundle = bundleRepository.save(existingBundle);
        return bundleMapper.toBundleDTO(updatedBundle);
    }

    public void deleteBundle(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + id));
        bundleRepository.delete(bundle);
    }
}