package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
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
    public List<UserBundleDetailsDTO> getUserBundles(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return userBundleRepository.findByUserIdWithBundle(userId).stream()
                .map(userBundleMapper::toUserBundleDetailsDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserBundleDetailsDTO updateUserBundle(Long id, UserBundleDTO userBundleDTO) {
        UserBundle userBundle = userBundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User bundle not found"));

        if (userBundleDTO.getBundleId() != null && 
            !userBundle.getBundle().getBundleId().equals(userBundleDTO.getBundleId())) {
            Bundle newBundle = bundleRepository.findById(userBundleDTO.getBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bundle not found"));
            userBundle.setBundle(newBundle);
        }

        if (userBundleDTO.getStatus() != null) {
            userBundle.setStatus(UserBundle.BundleStatus.valueOf(userBundleDTO.getStatus().name()));
        }

        if (userBundleDTO.getConsumption() != null) {
            userBundle.setConsumption(userBundleDTO.getConsumption());
        }

        return userBundleMapper.toUserBundleDetailsDTO(userBundleRepository.save(userBundle));
    }
}