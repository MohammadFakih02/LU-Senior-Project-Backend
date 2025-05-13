package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.dtos.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentService paymentService;

    public List<UserResponseDTO> getAllUsers() {
        // 1. Fetch non-deleted users with their non-deleted bundles from repository
        List<User> users = userRepository.findAllActiveUsers();

        // Filter out deleted bundles for each user
        users.forEach(user ->
                user.setBundles(
                        user.getBundles().stream()
                                .filter(ub -> !ub.isDeleted()) // Remove deleted bundles
                                .collect(Collectors.toSet())
                )
        );

        return userMapper.toUserResponseDTOList(users);
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long id) {
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Filter out deleted bundles
        Set<UserBundle> activeBundles = user.getBundles().stream()
                .filter(ub -> !ub.isDeleted()) // Keep non-deleted bundles
                .collect(Collectors.toSet());
        user.setBundles(activeBundles); // Replace the collection

        return userMapper.toUserDetailsDTO(user);
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
            User user = userRepository.findByIdWithBundlesAndPayments(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

            updateUserBundles(user, userDTO.getBundleSubscriptions());

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
            userBundle.setSubscriptionDate(subscription.getSubscriptionDate() != null ?
                    subscription.getSubscriptionDate() : LocalDate.now());
            userBundle.setStatus(subscription.getStatus());
            userBundle.setLocation(savedLocation);

            UserBundle savedUserBundle = userBundleRepository.save(userBundle);
            createPaymentForUserBundle(savedUserBundle); // Create payment after saving

            return savedUserBundle;
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ConflictException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to add bundle to user", ex);
            }
        }
    }

    private void updateUserBundles(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> subscriptions) {
        Set<Long> processedIds = new HashSet<>();

        subscriptions.forEach(sub -> {
            // Validate required fields
            if (sub.getBundleId() == null) {
                throw new ValidationException("Bundle ID is required");
            }
            if (sub.getLocation() == null) {
                throw new ValidationException("Location is required for bundle subscription");
            }

            Bundle bundle = bundleRepository.findById(sub.getBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + sub.getBundleId()));

            // Handle location properly
            LocationDTO locationDTO = sub.getLocation();
            Location location = findExistingLocation(locationDTO)
                    .orElseGet(() -> locationRepository.save(locationMapper.toLocation(locationDTO)));

            Optional<UserBundle> existingUserBundle = userBundleRepository.findByUserAndBundleAndLocation(
                    user, bundle, location
            );

            existingUserBundle.ifPresentOrElse(
                    ub -> {
                        // Update existing bundle
                        ub.setSubscriptionDate(sub.getSubscriptionDate() != null ?
                                sub.getSubscriptionDate() : LocalDate.now());
                        ub.setStatus(sub.getStatus());
                        userBundleRepository.save(ub);
                        processedIds.add(ub.getId());
                    },
                    () -> {
                        // Create new bundle
                        UserBundle newUb = new UserBundle();
                        newUb.setUser(user);
                        newUb.setBundle(bundle);
                        newUb.setSubscriptionDate(sub.getSubscriptionDate() != null ?
                                sub.getSubscriptionDate() : LocalDate.now());
                        newUb.setStatus(sub.getStatus());
                        newUb.setLocation(location);

                        UserBundle savedUb = userBundleRepository.save(newUb);
                        processedIds.add(savedUb.getId());
                        createPaymentForUserBundle(savedUb);
                    }
            );
        });

        // Soft-delete removed bundles
        user.getBundles().stream()
                .filter(ub -> !processedIds.contains(ub.getId()))
                .forEach(ub -> {
                    // Soft-delete the bundle
                    ub.setDeleted(true);

                    // Soft-delete all associated payments
                    ub.getPayments().forEach(payment -> {
                        payment.setDeleted(true);
                    });
                });
    }

    private Optional<Location> findExistingLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {
            throw new ValidationException("Location data cannot be null");
        }

        try {
            Location exampleLocation = locationMapper.toLocation(locationDTO);
            exampleLocation.setLocationId(null);  // Ignore ID for matching

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnorePaths("locationId", "googleMapsUrl")
                    .withStringMatcher(StringMatcher.DEFAULT);

            return locationRepository.findOne(Example.of(exampleLocation, matcher));
        } catch (Exception ex) {
            throw new OperationFailedException("Failed to find existing location", ex);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Soft-delete cascade
        user.setDeleted(true);

        user.getBundles().forEach(userBundle -> {
            userBundle.setDeleted(true);
            userBundle.getPayments().forEach(payment -> {
                payment.setDeleted(true);
            });
        });

        userRepository.save(user);
    }

    private void createPaymentForUserBundle(UserBundle userBundle) {
        CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
        paymentDTO.setAmount(userBundle.getBundle().getPrice());
        paymentDTO.setDueDate(LocalDateTime.now().plusMonths(1));
        paymentDTO.setPaymentMethod("Invoice");
        paymentDTO.setUserBundleId(userBundle.getId());

        paymentService.createPayment(paymentDTO);
    }

}