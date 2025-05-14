package com.example.internetprovidermanagement.services;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.dtos.BundleResponseDTO;
import com.example.internetprovidermanagement.exceptions.ConflictException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.BundleMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundleService {

    private final BundleRepository bundleRepository;
    private final BundleMapper bundleMapper;
    private final UserBundleRepository userBundleRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public BundleResponseDTO createBundle(BundleDTO bundleDTO) {
        if (bundleRepository.existsByName(bundleDTO.getName())) {
            throw new ConflictException("Bundle with name " + bundleDTO.getName() + " already exists");
        }
        
        Bundle bundle = bundleMapper.toBundle(bundleDTO);
        return bundleMapper.toBundleResponseDTO(bundleRepository.save(bundle));
    }

    @Transactional(readOnly = true)
    public List<BundleResponseDTO> getAllBundles() {
        return bundleRepository.findAllActiveBundles().stream()
                .map(bundleMapper::toBundleResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BundleResponseDTO getBundleById(Long id) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + id));

        if (bundle.isDeleted()) {
            throw new ResourceNotFoundException("Bundle not found with id: " + id);
        }

        return bundleMapper.toBundleResponseDTO(bundle);
    }

    @Transactional
    public BundleResponseDTO updateBundle(Long id, BundleDTO bundleDTO) {
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + id));
        
        bundleMapper.updateBundleFromDto(bundleDTO, bundle);
        return bundleMapper.toBundleResponseDTO(bundleRepository.save(bundle));
    }

    @Transactional
    public void deleteBundle(Long id) {
        // 1. Soft-delete the bundle
        Bundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found"));
        bundle.setDeleted(true);
        bundleRepository.save(bundle);

        // 2. Soft-delete all related user bundles
        userBundleRepository.softDeleteByBundleId(id);

        // 3. Soft-delete all related payments
        paymentRepository.softDeleteByBundleId(id);
    }

}