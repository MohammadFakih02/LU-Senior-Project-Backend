package com.example.internetprovidermanagement.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.exceptions.InvalidOperationException;
import com.example.internetprovidermanagement.exceptions.OperationFailedException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.exceptions.ValidationException;
import com.example.internetprovidermanagement.mappers.UserBundleMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBundleService {

    private final UserBundleRepository userBundleRepository;
    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final UserBundleMapper userBundleMapper;

    @Transactional(readOnly = true)
    @SuppressWarnings("UseSpecificCatch")
    public List<UserBundleDetailsDTO> getUserBundles(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
            
            return userBundleRepository.findByUserIdWithBundle(userId).stream()
                    .map(userBundleMapper::toUserBundleDetailsDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to retrieve bundles for user with id: " + userId, ex);
            }
        }
    }

    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public UserBundleDetailsDTO updateUserBundle(Long id, UserBundleDTO userBundleDTO) {
        if (id == null) {
            throw new ValidationException("User bundle ID cannot be null");
        }
        if (userBundleDTO == null) {
            throw new ValidationException("User bundle data cannot be null");
        }

        try {
            UserBundle userBundle = userBundleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User bundle not found with id: " + id));

            // Validate bundle update
            if (userBundleDTO.getBundleId() != null && 
                !userBundle.getBundle().getBundleId().equals(userBundleDTO.getBundleId())) {
                
                Bundle newBundle = bundleRepository.findById(userBundleDTO.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + userBundleDTO.getBundleId()));
                
                if (userBundle.getStatus() == UserBundle.BundleStatus.ACTIVE) {
                    throw new InvalidOperationException("Cannot change bundle for an active subscription");
                }
                
                userBundle.setBundle(newBundle);
            }

            // Validate status update
            if (userBundleDTO.getStatus() != null) {
                try {
                    UserBundle.BundleStatus newStatus = UserBundle.BundleStatus.valueOf(userBundleDTO.getStatus().name());
                    
                    if (userBundle.getStatus() == UserBundle.BundleStatus.INACTIVE && newStatus == UserBundle.BundleStatus.ACTIVE) {
                        throw new InvalidOperationException("Cannot reactivate an inactive bundle. Please create a new subscription.");
                    }
                    
                    userBundle.setStatus(newStatus);
                } catch (IllegalArgumentException ex) {
                    throw new ValidationException("Invalid bundle status: " + userBundleDTO.getStatus());
                }
            }

            // Validate consumption update
            if (userBundleDTO.getConsumption() != null) {
                if (userBundleDTO.getConsumption().compareTo(BigDecimal.ZERO) < 0) {
                    throw new ValidationException("Consumption cannot be negative");
                }
                userBundle.setConsumption(userBundleDTO.getConsumption());
            }

            return userBundleMapper.toUserBundleDetailsDTO(userBundleRepository.save(userBundle));
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException || ex instanceof InvalidOperationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to update user bundle with id: " + id, ex);
            }
        }
    }
}