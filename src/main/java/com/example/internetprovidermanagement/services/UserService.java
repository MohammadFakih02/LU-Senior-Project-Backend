package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.CreateUpdateUserDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserResponseDTO;
import com.example.internetprovidermanagement.exceptions.ConflictException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.LocationMapper;
import com.example.internetprovidermanagement.mappers.UserBundleMapper;
import com.example.internetprovidermanagement.mappers.UserMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;
import com.example.internetprovidermanagement.repositories.LocationRepository;
import com.example.internetprovidermanagement.repositories.UserBundleRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final UserBundleRepository userBundleRepository;
    private final LocationRepository locationRepository;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    @SuppressWarnings("unused")
    private final UserBundleMapper userBundleMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toUserDetailsDTO(user);
    }

    @Transactional
    public UserDetailsDTO createUser(CreateUpdateUserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new ConflictException("Phone number already in use");
        }

        User user = userMapper.toUser(userDTO);
        Location location = locationMapper.toLocation(userDTO.getLocation());
        user.setLocation(locationRepository.save(location));

        User savedUser = userRepository.save(user);
        
        // Add bundles if specified
        if (userDTO.getBundleIds() != null && !userDTO.getBundleIds().isEmpty()) {
            addBundlesToUser(savedUser.getId(), userDTO.getBundleIds());
        }

        return userMapper.toUserDetailsDTO(savedUser);
    }

    @Transactional
    public UserDetailsDTO updateUser(Long id, CreateUpdateUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update basic user info
        userMapper.updateUserFromDto(userDTO, user);

        // Update location if changed
        if (userDTO.getLocation() != null) {
            Location location = user.getLocation();
            locationMapper.updateLocationFromDto(userDTO.getLocation(), location);
            locationRepository.save(location);
        }

        // Update bundles if specified
        if (userDTO.getBundleIds() != null) {
            updateUserBundles(user, userDTO.getBundleIds());
        }

        return userMapper.toUserDetailsDTO(userRepository.save(user));
    }

    private void updateUserBundles(User user, Set<Long> bundleIds) {
        // Remove bundles not in the new set
        user.getBundles().removeIf(ub -> !bundleIds.contains(ub.getBundle().getBundleId()));

        // Add new bundles
        Set<Long> existingBundleIds = user.getBundles().stream()
                .map(ub -> ub.getBundle().getBundleId())
                .collect(Collectors.toSet());
        
        bundleIds.stream()
                .filter(bundleId -> !existingBundleIds.contains(bundleId))
                .forEach(bundleId -> addBundleToUser(user, bundleId));
    }

    private void addBundlesToUser(Long userId, Set<Long> bundleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        bundleIds.forEach(bundleId -> addBundleToUser(user, bundleId));
    }

    private void addBundleToUser(User user, Long bundleId) {
        Bundle bundle = bundleRepository.findById(bundleId)
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found"));
        
        if (userBundleRepository.existsByUserIdAndBundleId(user.getId(), bundleId)) {
            throw new ConflictException("User already has this bundle");
        }

        UserBundle userBundle = new UserBundle();
        userBundle.setUser(user);
        userBundle.setBundle(bundle);
        userBundle.setSubscriptionDate(user.getSubscriptionDate());
        userBundleRepository.save(userBundle);
    }
}