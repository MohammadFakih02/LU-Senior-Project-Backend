package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.CreateUserDTO;
import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.exceptions.ConflictException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.UserMapper;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.User.UserStatus;
import com.example.internetprovidermanagement.models.UserBundle;
import com.example.internetprovidermanagement.repositories.BundleRepository;
import com.example.internetprovidermanagement.repositories.LocationRepository;
import com.example.internetprovidermanagement.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final LocationRepository locationRepository;
    private final UserMapper userMapper;

public UserDTO createUser(CreateUserDTO createUserDTO) {
    UserDTO userDTO = createUserDTO.getUser();
    
    if (userRepository.existsByEmail(userDTO.getEmail())) {
        throw new ConflictException("Email already in use: " + userDTO.getEmail());
    }
    if (userRepository.existsByPhone(userDTO.getPhone())) {
        throw new ConflictException("Phone number already in use: " + userDTO.getPhone());
    }

    Location location = locationRepository.findById(createUserDTO.getLocationId())
            .orElseThrow(() -> new ResourceNotFoundException("Location not found with ID: " + createUserDTO.getLocationId()));

    User user = userMapper.toEntity(userDTO);
    user.setLocation(location);
    user.setSubscriptionDate(LocalDate.now());
    user.setStatus(UserStatus.ACTIVE);

    // Save user first to get the ID
    User savedUser = userRepository.save(user);
    
    // Create and save UserBundle associations
    createUserDTO.getBundleIds().forEach(bundleId -> {
        Bundle bundle = bundleRepository.findById(bundleId)
            .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with ID: " + bundleId));
        
        UserBundle userBundle = new UserBundle();
        userBundle.setUser(savedUser);
        userBundle.setBundle(bundle);
        userBundle.setSubscriptionDate(savedUser.getSubscriptionDate());
        userBundle.setStatus(UserStatus.ACTIVE);
        
        savedUser.getBundles().add(userBundle);
    });

    // Save again to persist the associations
    User finalUser = userRepository.save(savedUser);
    return userMapper.toDto(finalUser);
}

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toDetailsDto(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already in use: " + userDTO.getEmail());
        }
        
        userMapper.updateUserFromDto(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}