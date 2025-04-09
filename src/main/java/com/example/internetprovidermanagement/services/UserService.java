package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.exceptions.ConflictException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.repositories.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BundleService bundleService;
    private final LocationService locationService;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, 
                     BundleService bundleService, 
                     LocationService locationService,
                     ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.bundleService = bundleService;
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already in use: " + userDTO.getEmail());
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new ConflictException("Phone number already in use: " + userDTO.getPhone());
        }

        Bundle bundle = getBundleById(userDTO.getBundleId());
        Location location = getLocationById(userDTO.getLocationId());
        
        User user = modelMapper.toUser(userDTO, bundle, location);
        User savedUser = userRepository.save(user);
        
        return modelMapper.toUserDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(modelMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return modelMapper.toUserDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return modelMapper.toUserDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already in use: " + userDTO.getEmail());
        }
        
        if (!existingUser.getPhone().equals(userDTO.getPhone()) && 
            userRepository.existsByPhone(userDTO.getPhone())) {
            throw new ConflictException("Phone number already in use: " + userDTO.getPhone());
        }

        Bundle bundle = existingUser.getBundle();
        if (!bundle.getId().equals(userDTO.getBundleId())) {
            bundle = getBundleById(userDTO.getBundleId());
        }
        
        Location location = existingUser.getLocation();
        if (!location.getId().equals(userDTO.getLocationId())) {
            location = getLocationById(userDTO.getLocationId());
        }
        
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setLandLine(userDTO.getLandLine());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setConsumption(userDTO.getConsumption());
        existingUser.setBill(userDTO.getBill());
        existingUser.setSubscriptionDate(userDTO.getSubscriptionDate());
        existingUser.setStatus(userDTO.getStatus());
        existingUser.setBundle(bundle);
        existingUser.setLocation(location);
        
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.toUserDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private Bundle getBundleById(Long bundleId) {
        return modelMapper.toBundle(bundleService.getBundleById(bundleId));
    }

    private Location getLocationById(Long locationId) {
        return modelMapper.toLocation(locationService.getLocationById(locationId));
    }
}