package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internetprovidermanagement.dtos.CreateUpdateUserDTO;
import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserResponseDTO;
import com.example.internetprovidermanagement.exceptions.ConflictException;
import com.example.internetprovidermanagement.exceptions.InvalidOperationException;
import com.example.internetprovidermanagement.exceptions.OperationFailedException;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.exceptions.ValidationException;
import com.example.internetprovidermanagement.mappers.LocationMapper;
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

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(userMapper::toUserResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new OperationFailedException("Failed to retrieve users", ex);
        }
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long id) {
        if (id == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            User user = userRepository.findByIdWithBundlesAndLocation(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            
            if (user.getBundles() != null) {
                user.getBundles().size();
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
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to retrieve user with id: " + id, ex);
            }
        }
    }

    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public UserDetailsDTO createUser(CreateUpdateUserDTO userDTO) {
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }

        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new ConflictException("Email '" + userDTO.getEmail() + "' is already in use");
            }
            if (userRepository.existsByPhone(userDTO.getPhone())) {
                throw new ConflictException("Phone number '" + userDTO.getPhone() + "' is already in use");
            }
        
            User user = userMapper.toUser(userDTO);
            LocationDTO userLocationDTO = userDTO.getLocation();
            if (userLocationDTO == null) {
                throw new ValidationException("User location is required");
            }
            
            Optional<Location> existingUserLocation = findExistingLocation(userLocationDTO);
            Location userLocation = existingUserLocation.orElseGet(() -> 
                locationRepository.save(locationMapper.toLocation(userLocationDTO))
            );
            user.setLocation(userLocation);
        
            User savedUser = userRepository.save(user);
            
            if (userDTO.getBundleSubscriptions() != null && !userDTO.getBundleSubscriptions().isEmpty()) {
                addBundlesToUser(savedUser, userDTO.getBundleSubscriptions());
            }
        
            User userWithBundles = userRepository.findById(savedUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + savedUser.getId()));
            
            return userMapper.toUserDetailsDTO(userWithBundles);
        } catch (Exception ex) {
            if (ex instanceof ConflictException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to create user", ex);
            }
        }
    }

    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public UserDetailsDTO updateUser(Long id, CreateUpdateUserDTO userDTO) {
        if (id == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(userDTO.getEmail())) {
                    throw new ConflictException("Email '" + userDTO.getEmail() + "' is already in use");
                }
            }
            if (userDTO.getPhone() != null && !userDTO.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(userDTO.getPhone())) {
                    throw new ConflictException("Phone number '" + userDTO.getPhone() + "' is already in use");
                }
            }

            userMapper.updateUserFromDto(userDTO, user);

            if (userDTO.getLocation() != null) {
                LocationDTO newLocationDTO = userDTO.getLocation();
                Optional<Location> existingLocation = findExistingLocation(newLocationDTO);
                user.setLocation(existingLocation.orElseGet(() -> 
                    locationRepository.save(locationMapper.toLocation(newLocationDTO))
                ));
            }

            if (userDTO.getBundleSubscriptions() != null) {
                updateUserBundles(user, userDTO.getBundleSubscriptions());
            }

            return userMapper.toUserDetailsDTO(userRepository.save(user));
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ConflictException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to update user with id: " + id, ex);
            }
        }
    }

    private void addBundlesToUser(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> bundleSubscriptions) {
        if (bundleSubscriptions == null) {
            return;
        }

        try {
            bundleSubscriptions.forEach(subscription -> addBundleToUser(user, subscription));
        } catch (Exception ex) {
            throw new OperationFailedException("Failed to add bundles to user", ex);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private UserBundle addBundleToUser(User user, CreateUpdateUserDTO.UserBundleSubscriptionDTO subscription) {
        if (subscription == null) {
            throw new ValidationException("Bundle subscription data cannot be null");
        }
        if (subscription.getBundleId() == null) {
            throw new ValidationException("Bundle ID is required");
        }
        if (subscription.getLocation() == null) {
            throw new ValidationException("Location is required for bundle subscription");
        }

        try {
            Bundle bundle = bundleRepository.findById(subscription.getBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + subscription.getBundleId()));
            
            LocationDTO subscriptionLocationDTO = subscription.getLocation();
            Optional<Location> existingLocation = findExistingLocation(subscriptionLocationDTO);
            Location savedLocation = existingLocation.orElseGet(() -> 
                locationRepository.save(locationMapper.toLocation(subscriptionLocationDTO))
            );
        
            if (userBundleRepository.existsByUserAndBundleAndLocation(user, bundle, savedLocation)) {
                throw new ConflictException("User already has bundle '" + bundle.getName() + "' at this location");
            }
        
            UserBundle userBundle = new UserBundle();
            userBundle.setUser(user);
            userBundle.setBundle(bundle);
            userBundle.setSubscriptionDate(LocalDate.now());
            userBundle.setLocation(savedLocation);
            return userBundleRepository.save(userBundle);
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ConflictException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to add bundle to user", ex);
            }
        }
    }

    private void updateUserBundles(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> bundleSubscriptions) {
        if (bundleSubscriptions == null) {
            return;
        }
    
        try {
            Set<Long> processedUserBundleIds = new HashSet<>();
        
            bundleSubscriptions.forEach(subscription -> {
                if (subscription.getBundleId() == null) {
                    throw new ValidationException("Bundle ID is required");
                }
                if (subscription.getLocation() == null) {
                    throw new ValidationException("Location is required for bundle subscription");
                }
    
                Bundle bundle = bundleRepository.findById(subscription.getBundleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + subscription.getBundleId()));
                
                LocationDTO subscriptionLocationDTO = subscription.getLocation();
                Optional<Location> existingLocation = findExistingLocation(subscriptionLocationDTO);
                Location savedLocation = existingLocation.orElseGet(() -> 
                    locationRepository.save(locationMapper.toLocation(subscriptionLocationDTO))
                );
        
                Optional<UserBundle> existingUserBundle = userBundleRepository.findByUserAndBundleAndLocation(user, bundle, savedLocation);
        
                existingUserBundle.ifPresentOrElse(
                    ub -> {
                        // Update existing bundle properties including status
                        ub.setSubscriptionDate(subscription.getSubscriptionDate() != null 
                            ? subscription.getSubscriptionDate() 
                            : LocalDate.now());
                        ub.setStatus(subscription.getStatus());
                        userBundleRepository.save(ub);
                        processedUserBundleIds.add(ub.getId());
                    },
                    () -> {
                        // Create new bundle
                        UserBundle newUb = new UserBundle();
                        newUb.setUser(user);
                        newUb.setBundle(bundle);
                        newUb.setSubscriptionDate(subscription.getSubscriptionDate() != null 
                            ? subscription.getSubscriptionDate() 
                            : LocalDate.now());
                        newUb.setStatus(subscription.getStatus());
                        newUb.setLocation(savedLocation);
                        UserBundle savedUb = userBundleRepository.save(newUb);
                        processedUserBundleIds.add(savedUb.getId());
                    }
                );
            });
        
            user.getBundles().removeIf(ub -> !processedUserBundleIds.contains(ub.getId()));
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ValidationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to update user bundles", ex);
            }
        }
    }

    private Optional<Location> findExistingLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {
            throw new ValidationException("Location data cannot be null");
        }

        try {
            Location location = locationMapper.toLocation(locationDTO);
            location.setLocationId(null);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIncludeNullValues()
                    .withIgnorePaths("locationId")
                    .withStringMatcher(StringMatcher.EXACT);

            Example<Location> example = Example.of(location, matcher);
            return locationRepository.findOne(example);
        } catch (Exception ex) {
            throw new OperationFailedException("Failed to find existing location", ex);
        }
    }

    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public void deleteUser(Long id) {
        if (id == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            
            if (!user.getBundles().isEmpty()) {
                throw new InvalidOperationException("Cannot delete user with active bundles");
            }
            
            userRepository.delete(user);
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof InvalidOperationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to delete user with id: " + id, ex);
            }
        }
    }
}