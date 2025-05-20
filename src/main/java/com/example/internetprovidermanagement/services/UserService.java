package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.dtos.*;
import com.example.internetprovidermanagement.repositories.PaymentRepository;
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
    private final PaymentRepository paymentRepository;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final PaymentService paymentService;

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAllActiveUsers();

        users.forEach(user ->
                user.setBundles(
                        user.getBundles().stream()
                                .filter(ub -> !ub.isDeleted())
                                .collect(Collectors.toSet())
                )
        );

        return userMapper.toUserResponseDTOList(users);
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long id) {
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<UserBundle> activeBundles = user.getBundles().stream()
                .filter(ub -> !ub.isDeleted())
                .collect(Collectors.toSet());
        user.setBundles(activeBundles);

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
            if (userDTO.getLocation() == null) {
                throw new ValidationException("User location is required");
            }
            Location userLocation = getOrCreateUpdateLocation(userDTO.getLocation(), null);
            user.setLocation(userLocation);

            User savedUser = userRepository.save(user);

            if (userDTO.getBundleSubscriptions() != null && !userDTO.getBundleSubscriptions().isEmpty()) {
                addBundlesToUser(savedUser, userDTO.getBundleSubscriptions());
            }

            User userWithBundles = userRepository.findById(savedUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + savedUser.getId()));

            return userMapper.toUserDetailsDTO(userWithBundles);
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException || ex instanceof ConflictException || ex instanceof ValidationException) {
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
                Location updatedUserLocation = getOrCreateUpdateLocation(userDTO.getLocation(), user.getLocation());
                user.setLocation(updatedUserLocation);
            }

            if (userDTO.getBundleSubscriptions() != null) {
                updateUserBundles(user, userDTO.getBundleSubscriptions());
            }

            return userMapper.toUserDetailsDTO(userRepository.save(user));
        } catch (Exception ex) {
            if (ex instanceof ResourceNotFoundException ||
                    ex instanceof ConflictException ||
                    ex instanceof ValidationException ||
                    ex instanceof InvalidOperationException) {
                throw ex;
            } else {
                throw new OperationFailedException("Failed to update user with id: " + id, ex);
            }
        }
    }


    private Location getOrCreateUpdateLocation(LocationDTO desiredLocationData, Location currentEntityLocationIfAny) {
        if (desiredLocationData == null) {
            throw new ValidationException("Desired location data cannot be null.");
        }

        Location targetLocation;

        if (desiredLocationData.getLocationId() != null) {
            targetLocation = locationRepository.findById(desiredLocationData.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location specified in DTO not found with ID: " + desiredLocationData.getLocationId()));
            locationMapper.updateLocationFromDto(desiredLocationData, targetLocation);
        } else if (currentEntityLocationIfAny != null) {
            targetLocation = currentEntityLocationIfAny;
            locationMapper.updateLocationFromDto(desiredLocationData, targetLocation);
        } else {
            targetLocation = findExistingLocationByAttributes(desiredLocationData)
                    .map(existingLoc -> {
                        locationMapper.updateLocationFromDto(desiredLocationData, existingLoc);
                        return existingLoc; // Return the updated existing location
                    })
                    .orElseGet(() -> locationMapper.toLocation(desiredLocationData));
        }
        return locationRepository.save(targetLocation);
    }

    private Optional<Location> findExistingLocationByAttributes(LocationDTO locationDTO) {
        if (locationDTO == null) {
            return Optional.empty();
        }
        Location exampleLocation = locationMapper.toLocation(locationDTO);
        exampleLocation.setLocationId(null);
        exampleLocation.setCreatedAt(null);
        exampleLocation.setUpdatedAt(null);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("locationId", "createdAt", "updatedAt", "googleMapsUrl")
                .withStringMatcher(StringMatcher.DEFAULT)
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE);
        return locationRepository.findOne(Example.of(exampleLocation, matcher));
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

            Location bundleLocation = getOrCreateUpdateLocation(subscription.getLocation(), null);

            if (userBundleRepository.existsByUserAndBundleAndLocation(user, bundle, bundleLocation)) {
                throw new ConflictException("User already has bundle '" + bundle.getName() + "' at this location");
            }

            UserBundle userBundle = new UserBundle();
            userBundle.setUser(user);
            userBundle.setBundle(bundle);
            userBundle.setSubscriptionDate(subscription.getSubscriptionDate() != null ?
                    subscription.getSubscriptionDate() : LocalDate.now());
            userBundle.setStatus(subscription.getStatus());
            userBundle.setLocation(bundleLocation);

            UserBundle savedUserBundle = userBundleRepository.save(userBundle);
            createPaymentForUserBundle(savedUserBundle);

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
        final Set<Long> processedUserBundleIds = new HashSet<>(); // Make this effectively final from the start

        for (final CreateUpdateUserDTO.UserBundleSubscriptionDTO subDTO : subscriptions) { // subDTO is effectively final in each iteration
            if (subDTO.getBundleId() == null) {
                throw new ValidationException("Bundle ID is required for subscription.");
            }
            if (subDTO.getLocation() == null) {
                throw new ValidationException("Location is required for bundle subscription.");
            }

            final Bundle bundle = bundleRepository.findById(subDTO.getBundleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bundle not found with id: " + subDTO.getBundleId()));

            UserBundle userBundleToProcess = null;
            Location resolvedLocationForThisSub = null;

            List<UserBundle> existingUserBundlesForThisBundle = userBundleRepository.findByUserAndBundleAndDeletedIsFalse(user, bundle);

            if (subDTO.getLocation().getLocationId() != null) {
                // DTO specifies a Location ID: attempt to use/update that specific Location record
                final Location tempResolvedLoc = getOrCreateUpdateLocation(subDTO.getLocation(), null); // Existing loc by ID, or new if ID not found (error). Attributes updated.
                resolvedLocationForThisSub = tempResolvedLoc;

                Optional<UserBundle> ubAtSpecificLocation = existingUserBundlesForThisBundle.stream()
                        .filter(ub -> ub.getLocation().getLocationId().equals(tempResolvedLoc.getLocationId()))
                        .findFirst();
                if (ubAtSpecificLocation.isPresent()) {
                    userBundleToProcess = ubAtSpecificLocation.get();
                }
            } else {
                // DTO does not specify a Location ID
                if (existingUserBundlesForThisBundle.size() == 1) {
                    // Only one existing bundle for this User+Bundle: this is a candidate for "move" or location attribute update
                    UserBundle candidate = existingUserBundlesForThisBundle.get(0);
                    final Location tempResolvedLoc = getOrCreateUpdateLocation(subDTO.getLocation(), candidate.getLocation()); // Update attributes of candidate's location
                    resolvedLocationForThisSub = tempResolvedLoc;
                    userBundleToProcess = candidate; // This candidate will be processed (location potentially changed)
                } else {
                    // No existing bundle for this User+Bundle, or multiple (ambiguous to "move")
                    // A new UserBundle will likely be created. Resolve its location based on DTO attributes.
                    final Location tempResolvedLoc = getOrCreateUpdateLocation(subDTO.getLocation(), null); // Find by attributes or create new
                    resolvedLocationForThisSub = tempResolvedLoc;
                    // Check if a UB already exists at this newly resolved location (to avoid duplicates if attributes matched an existing one)
                    Optional<UserBundle> ubAtAttributeLocation = existingUserBundlesForThisBundle.stream()
                            .filter(ub -> ub.getLocation().getLocationId().equals(tempResolvedLoc.getLocationId()))
                            .findFirst();
                    if (ubAtAttributeLocation.isPresent()) {
                        userBundleToProcess = ubAtAttributeLocation.get();
                    }
                }
            }

            // At this point, userBundleToProcess is either an existing UB or null.
            // resolvedLocationForThisSub is the location this subDTO should be associated with.

            if (userBundleToProcess != null) {
                // An existing UserBundle is being updated (potentially moved)
                userBundleToProcess.setLocation(resolvedLocationForThisSub); // Critical: assign the resolved location

                UserBundle.BundleStatus currentStatus = userBundleToProcess.getStatus();
                UserBundle.BundleStatus newStatusFromDTO = subDTO.getStatus();

                if (currentStatus == UserBundle.BundleStatus.INACTIVE && newStatusFromDTO == UserBundle.BundleStatus.ACTIVE) {
                    if (paymentRepository.existsUnpaidPaymentForUserBundle(userBundleToProcess.getId())) {
                        throw new InvalidOperationException(
                                "Cannot reactivate UserBundle (ID: " + userBundleToProcess.getId() + ") for bundle '" +
                                        bundle.getName() + "' at location '" + resolvedLocationForThisSub.getAddress() +
                                        "' because it has unpaid payments."
                        );
                    }
                }
                userBundleToProcess.setStatus(newStatusFromDTO);
                userBundleToProcess.setSubscriptionDate(subDTO.getSubscriptionDate() != null ? subDTO.getSubscriptionDate() : LocalDate.now());

                userBundleRepository.save(userBundleToProcess);
                processedUserBundleIds.add(userBundleToProcess.getId());
            } else {
                // No existing UserBundle to update, so create a new one
                // resolvedLocationForThisSub should be non-null here. If it's null, there's a logic flaw.
                if (resolvedLocationForThisSub == null) { // Defensive check
                    throw new OperationFailedException("Logical error: resolvedLocationForThisSub is null when creating new UserBundle.");
                }

                // Final check for exact duplicate before creating
                Optional<UserBundle> duplicateCheck = userBundleRepository.findByUserAndBundleAndLocation(user, bundle, resolvedLocationForThisSub);
                if (duplicateCheck.isPresent() && !processedUserBundleIds.contains(duplicateCheck.get().getId())) {
                    UserBundle ub = duplicateCheck.get();
                    // Location is already resolvedLocationForThisSub. Ensure attributes are up-to-date if DTO was different.
                    getOrCreateUpdateLocation(subDTO.getLocation(), ub.getLocation());
                    ub.setStatus(subDTO.getStatus());
                    ub.setSubscriptionDate(subDTO.getSubscriptionDate() != null ? subDTO.getSubscriptionDate() : LocalDate.now());
                    userBundleRepository.save(ub);
                    processedUserBundleIds.add(ub.getId());

                } else if (!duplicateCheck.isPresent()) {
                    UserBundle newUb = new UserBundle();
                    newUb.setUser(user);
                    newUb.setBundle(bundle);
                    newUb.setLocation(resolvedLocationForThisSub);
                    newUb.setSubscriptionDate(subDTO.getSubscriptionDate() != null ?
                            subDTO.getSubscriptionDate() : LocalDate.now());
                    newUb.setStatus(subDTO.getStatus());

                    UserBundle savedUb = userBundleRepository.save(newUb);
                    processedUserBundleIds.add(savedUb.getId());
                    createPaymentForUserBundle(savedUb);
                }
            }
        }

        final Set<Long> finalProcessedIds = processedUserBundleIds; // Effectively final for the lambda
        user.getBundles().stream()
                .filter(ub -> !finalProcessedIds.contains(ub.getId()) && !ub.isDeleted())
                .forEach(ub -> {
                    ub.setDeleted(true);
                    ub.setStatus(UserBundle.BundleStatus.INACTIVE);
                    ub.getPayments().forEach(payment -> {
                        payment.setDeleted(true);
                    });
                    userBundleRepository.save(ub);
                });
    }


    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setDeleted(true);
        user.setStatus(User.UserStatus.INACTIVE);

        user.getBundles().forEach(userBundle -> {
            userBundle.setDeleted(true);
            userBundle.setStatus(UserBundle.BundleStatus.INACTIVE);
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
        paymentDTO.setPaymentMethod("Auto-Generated on Subscription");
        paymentDTO.setUserBundleId(userBundle.getId());

        paymentService.createPayment(paymentDTO);
    }
}