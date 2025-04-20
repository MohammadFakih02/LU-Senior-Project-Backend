package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Force loading of all relationships if needed
        if (user.getBundles() != null) {
            user.getBundles().size(); // This forces loading
            for (UserBundle bundle : user.getBundles()) {
                if (bundle.getBundle() != null) {
                    bundle.getBundle().getName();
                }
                if (bundle.getLocation() != null) {
                    bundle.getLocation().getAddress();
                }
            }
        }
        
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
        Location userLocation = locationMapper.toLocation(userDTO.getLocation());
        user.setLocation(locationRepository.save(userLocation));
    
        User savedUser = userRepository.save(user);
        
        if (userDTO.getBundleSubscriptions() != null && !userDTO.getBundleSubscriptions().isEmpty()) {
            addBundlesToUser(savedUser, userDTO.getBundleSubscriptions());
        }
    
        // Fetch the user with bundles eagerly loaded
        User userWithBundles = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + savedUser.getId()));
        
        return userMapper.toUserDetailsDTO(userWithBundles);
    }

    @Transactional
    public UserDetailsDTO updateUser(Long id, CreateUpdateUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userMapper.updateUserFromDto(userDTO, user);

        if (userDTO.getLocation() != null) {
            Location location = user.getLocation();
            locationMapper.updateLocationFromDto(userDTO.getLocation(), location);
            locationRepository.save(location);
        }

        if (userDTO.getBundleSubscriptions() != null) {
            updateUserBundles(user, userDTO.getBundleSubscriptions());
        }

        return userMapper.toUserDetailsDTO(userRepository.save(user));
    }

    private void addBundlesToUser(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> bundleSubscriptions) {
        bundleSubscriptions.forEach(subscription -> addBundleToUser(user, subscription));
    }

private UserBundle addBundleToUser(User user, CreateUpdateUserDTO.UserBundleSubscriptionDTO subscription) {
    Bundle bundle = bundleRepository.findById(subscription.getBundleId())
            .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + subscription.getBundleId()));
    
    Location subscriptionLocation = locationMapper.toLocation(subscription.getLocation());
    Location savedLocation = locationRepository.save(subscriptionLocation);

    if (userBundleRepository.existsByUserAndBundleAndLocation(user, bundle, savedLocation)) {
        throw new ConflictException("User already has this bundle at this location");
    }

    UserBundle userBundle = new UserBundle();
    userBundle.setUser(user);
    userBundle.setBundle(bundle);
    userBundle.setSubscriptionDate(LocalDate.now()); // Auto-set to current date
    userBundle.setLocation(savedLocation);
    return userBundleRepository.save(userBundle); // Return the saved UserBundle
}

private void updateUserBundles(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> bundleSubscriptions) {
    Set<Long> processedUserBundleIds = new HashSet<>();

    bundleSubscriptions.forEach(subscription -> {
        Bundle bundle = bundleRepository.findById(subscription.getBundleId())
                .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + subscription.getBundleId()));
        
        Location subscriptionLocation = locationMapper.toLocation(subscription.getLocation());
        Location savedLocation = locationRepository.save(subscriptionLocation);

        Optional<UserBundle> existingUserBundle = userBundleRepository.findByUserAndBundleAndLocation(user, bundle, savedLocation);

        existingUserBundle.ifPresentOrElse(
            ub -> {
                // Update existing UserBundle (keep original subscription date)
                locationMapper.updateLocationFromDto(subscription.getLocation(), ub.getLocation());
                locationRepository.save(ub.getLocation());
                processedUserBundleIds.add(ub.getId());
            },
            () -> {
                // Add new UserBundle and capture its ID
                UserBundle newUb = addBundleToUser(user, subscription);
                processedUserBundleIds.add(newUb.getId());
            }
        );
    });

    // Remove UserBundles not included in the update
    user.getBundles().removeIf(ub -> !processedUserBundleIds.contains(ub.getId()));
}
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}