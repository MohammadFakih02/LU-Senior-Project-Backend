package com.example.internetprovidermanagement.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BundleRepository bundleRepository;
    private final UserBundleRepository userBundleRepository;
    private final LocationRepository locationRepository;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.debug("Fetching all non-deleted users");
        try {
            return userRepository.findAll().stream()
                    .filter(user -> !user.isDeleted())
                    .map(userMapper::toUserResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Failed to retrieve users", ex);
            throw new OperationFailedException("Failed to retrieve users", ex);
        }
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO getUserById(Long id) {
        if (id == null) {
            logger.warn("Attempted to get user with null ID");
            throw new ValidationException("User ID cannot be null");
        }
        logger.debug("Fetching user by ID: {}", id);

        try {
            User user = userRepository.findByIdWithBundlesAndLocation(id)
                    .orElseThrow(() -> {
                        logger.warn("User not found with ID: {}", id);
                        return new ResourceNotFoundException("User not found with id: " + id);
                    });

            if (user.isDeleted()){
                logger.warn("Attempted to fetch soft-deleted user with ID: {}", id);
                throw new ResourceNotFoundException("User not found with id: " + id + " (deleted)");
            }

            if (user.getBundles() == null) {
                logger.warn("User ID {} has a null 'bundles' collection after query.", id);
                user.setBundles(new HashSet<>());
            } else {
                user.getBundles().forEach(ub -> {
                    if (ub != null) {
                        if (ub.getBundle() != null) ub.getBundle().getName();
                        if (ub.getLocation() != null) ub.getLocation().getAddress();
                    }
                });
            }

            Set<UserBundle> activeBundles = user.getBundles().stream()
                    .filter(ub -> ub != null && !ub.isDeleted())
                    .collect(Collectors.toSet());
            user.setBundles(activeBundles);

            return userMapper.toUserDetailsDTO(user);
        } catch (ResourceNotFoundException | ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to retrieve user with id: {}", id, ex);
            throw new OperationFailedException("Failed to retrieve user with id: " + id, ex);
        }
    }

    @Transactional
    public UserDetailsDTO createUser(CreateUpdateUserDTO userDTO) {
        if (userDTO == null) {
            logger.warn("Attempted to create user with null DTO");
            throw new ValidationException("User data cannot be null");
        }
        logger.info("Creating new user with email: {}", userDTO.getEmail());

        try {
            if (userDTO.getEmail() != null && userRepository.existsByEmail(userDTO.getEmail())) {
                logger.warn("Conflict: Email '{}' already in use", userDTO.getEmail());
                throw new ConflictException("Email '" + userDTO.getEmail() + "' is already in use");
            }
            if (userRepository.existsByPhone(userDTO.getPhone())) {
                logger.warn("Conflict: Phone '{}' already in use", userDTO.getPhone());
                throw new ConflictException("Phone number '" + userDTO.getPhone() + "' is already in use");
            }

            User user = userMapper.toUser(userDTO);
            if (userDTO.getLocation() == null) {
                logger.warn("User location DTO is null during user creation");
                throw new ValidationException("User location is required");
            }
            user.setLocation(findOrCreateLocation(userDTO.getLocation()));
            user.setDeleted(false);
            if (user.getBundles() == null) {
                user.setBundles(new HashSet<>());
            }

            User savedUser = userRepository.save(user);
            logger.info("User saved with ID: {}", savedUser.getId());

            if (userDTO.getBundleSubscriptions() != null && !userDTO.getBundleSubscriptions().isEmpty()) {
                logger.debug("Adding bundle subscriptions for new user ID: {}", savedUser.getId());
                updateUserBundles(savedUser, userDTO.getBundleSubscriptions());
            }

            User userWithBundles = userRepository.findByIdWithBundlesAndLocation(savedUser.getId())
                    .orElseThrow(() -> {
                        logger.error("Failed to reload user with ID {} after creation and bundle subscription update.", savedUser.getId());
                        return new ResourceNotFoundException("User not found with id: " + savedUser.getId());
                    });

            if (userWithBundles.getBundles() == null) userWithBundles.setBundles(new HashSet<>());
            Set<UserBundle> activeBundles = userWithBundles.getBundles().stream()
                    .filter(ub -> ub != null && !ub.isDeleted())
                    .collect(Collectors.toSet());
            userWithBundles.setBundles(activeBundles);

            return userMapper.toUserDetailsDTO(userWithBundles);
        } catch (ConflictException | ValidationException | ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to create user with email: {}", userDTO.getEmail(), ex);
            throw new OperationFailedException("Failed to create user", ex);
        }
    }


    @Transactional
    public UserDetailsDTO updateUser(Long id, CreateUpdateUserDTO userDTO) {
        if (id == null) {
            logger.warn("Attempted to update user with null ID");
            throw new ValidationException("User ID cannot be null");
        }
        if (userDTO == null) {
            logger.warn("Attempted to update user ID {} with null DTO", id);
            throw new ValidationException("User data cannot be null");
        }
        logger.info("Updating user with ID: {}", id);

        try {
            User user = userRepository.findByIdWithBundlesAndLocation(id)
                    .orElseThrow(() -> {
                        logger.warn("User not found for update with ID: {}", id);
                        return new ResourceNotFoundException("User not found with id: " + id);
                    });

            if(user.isDeleted()){
                logger.warn("Attempted to update soft-deleted user with ID: {}", id);
                throw new ResourceNotFoundException("User not found with id: " + id + " (deleted)");
            }

            if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(userDTO.getEmail())) {
                    logger.warn("Conflict during update: Email '{}' already in use for user ID {}", userDTO.getEmail(), id);
                    throw new ConflictException("Email '" + userDTO.getEmail() + "' is already in use");
                }
            }
            if (userDTO.getPhone() != null && !userDTO.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(userDTO.getPhone())) {
                    logger.warn("Conflict during update: Phone '{}' already in use for user ID {}", userDTO.getPhone(), id);
                    throw new ConflictException("Phone number '" + userDTO.getPhone() + "' is already in use");
                }
            }

            userMapper.updateUserFromDto(userDTO, user);

            if (userDTO.getLocation() != null) {
                logger.debug("Updating location for user ID: {}", id);
                user.setLocation(findOrCreateLocation(userDTO.getLocation()));
            }

            if (user.getBundles() == null) {
                logger.warn("User ID {} had null 'bundles' collection before bundle update. Initializing.", id);
                user.setBundles(new HashSet<>());
            }

            if (userDTO.getBundleSubscriptions() != null) {
                logger.debug("Updating bundle subscriptions for user ID: {}", id);
                updateUserBundles(user, userDTO.getBundleSubscriptions());
            }

            User savedUser = userRepository.save(user);
            logger.info("User ID {} successfully updated and saved.", id);

            User finalUser = userRepository.findByIdWithBundlesAndLocation(savedUser.getId())
                    .orElseThrow(() -> {
                        logger.error("Failed to reload user with ID {} after update.", savedUser.getId());
                        return new ResourceNotFoundException("User not found post-update with id: " + savedUser.getId());
                    });

            if (finalUser.getBundles() == null) finalUser.setBundles(new HashSet<>());
            Set<UserBundle> activeBundles = finalUser.getBundles().stream()
                    .filter(ub -> ub != null && !ub.isDeleted())
                    .collect(Collectors.toSet());
            finalUser.setBundles(activeBundles);

            return userMapper.toUserDetailsDTO(finalUser);
        } catch (ResourceNotFoundException | ConflictException | ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to update user with id: {}", id, ex);
            throw new OperationFailedException("Failed to update user with id: " + id, ex);
        }
    }

    private void updateUserBundles(User user, Set<CreateUpdateUserDTO.UserBundleSubscriptionDTO> bundleSubscriptionsDTO) {
        logger.debug("Inside updateUserBundles for user ID: {}", user.getId());
        if (user.getBundles() == null) {
            logger.error("Critical: user.getBundles() is null in updateUserBundles for user ID: {}. This should not happen.", user.getId());
            user.setBundles(new HashSet<>());
        }

        Map<String, UserBundle> existingUserBundlesMap = new HashMap<>();
        user.getBundles().forEach(ub -> {
            if (ub != null && ub.getBundle() != null && ub.getBundle().getBundleId() != null &&
                    ub.getLocation() != null && ub.getLocation().getLocationId() != null) {
                String key = ub.getBundle().getBundleId() + "-" + ub.getLocation().getLocationId();
                existingUserBundlesMap.put(key, ub);
            } else {
                logger.warn("User ID {} has an existing UserBundle (ID: {}) with null bundle/location or their IDs. Skipping for map.",
                        user.getId(), ub != null ? ub.getId() : "N/A");
            }
        });
        logger.debug("User ID {}: Found {} existing valid UserBundles to process.", user.getId(), existingUserBundlesMap.size());


        Set<UserBundle> finalUserBundlesForUserObject = new HashSet<>();

        for (CreateUpdateUserDTO.UserBundleSubscriptionDTO subDTO : bundleSubscriptionsDTO) {
            logger.debug("User ID {}: Processing DTO subscription for bundleId: {}", user.getId(), subDTO.getBundleId());
            if (subDTO.getBundleId() == null) {
                logger.warn("User ID {}: DTO subscription has null bundleId.", user.getId());
                throw new ValidationException("Bundle ID is required.");
            }
            if (subDTO.getLocation() == null) {
                logger.warn("User ID {}: DTO subscription for bundleId {} has null location.", user.getId(), subDTO.getBundleId());
                throw new ValidationException("Location is required for bundle subscription.");
            }

            Bundle bundle = bundleRepository.findById(subDTO.getBundleId())
                    .orElseThrow(() -> {
                        logger.warn("User ID {}: Bundle not found with ID: {} during subscription processing.", user.getId(), subDTO.getBundleId());
                        return new ResourceNotFoundException("Bundle not found with id: " + subDTO.getBundleId());
                    });

            if (bundle.isDeleted()){
                logger.warn("User ID {}: Attempt to subscribe to deleted bundle ID: {}", user.getId(), subDTO.getBundleId());
                throw new InvalidOperationException("Bundle with id: " + subDTO.getBundleId() + " is deleted and cannot be subscribed to.");
            }

            Location resolvedLocation = findOrCreateLocation(subDTO.getLocation());
            if (resolvedLocation == null || resolvedLocation.getLocationId() == null) {
                logger.error("User ID {}: ResolvedLocation is null or has null ID for DTO bundleId {}. DTO Location: {}", user.getId(), subDTO.getBundleId(), subDTO.getLocation());
                throw new OperationFailedException("Failed to resolve location for bundle subscription.");
            }
            logger.debug("User ID {}: Resolved location ID {} for bundleId {}.", user.getId(), resolvedLocation.getLocationId(), subDTO.getBundleId());


            String currentSubKey = bundle.getBundleId() + "-" + resolvedLocation.getLocationId();
            UserBundle userBundleToProcess = existingUserBundlesMap.get(currentSubKey);

            if (userBundleToProcess != null) {
                logger.debug("User ID {}: Found existing UserBundle (ID: {}) for key: {}. Updating.", user.getId(), userBundleToProcess.getId(), currentSubKey);
                userBundleToProcess.setSubscriptionDate(subDTO.getSubscriptionDate() != null ? subDTO.getSubscriptionDate() : userBundleToProcess.getSubscriptionDate());
                userBundleToProcess.setStatus(subDTO.getStatus());
                userBundleToProcess.setDeleted(subDTO.isDeleted());

                UserBundle savedUb = userBundleRepository.save(userBundleToProcess);
                finalUserBundlesForUserObject.add(savedUb);
                existingUserBundlesMap.remove(currentSubKey);
            } else {
                if (!subDTO.isDeleted()) {
                    logger.debug("User ID {}: No existing UserBundle for key: {}. Creating new one.", user.getId(), currentSubKey);
                    UserBundle newUserBundle = new UserBundle();
                    newUserBundle.setUser(user);
                    newUserBundle.setBundle(bundle);
                    newUserBundle.setLocation(resolvedLocation);
                    newUserBundle.setSubscriptionDate(subDTO.getSubscriptionDate() != null ? subDTO.getSubscriptionDate() : LocalDate.now());
                    newUserBundle.setStatus(subDTO.getStatus());
                    newUserBundle.setDeleted(false);

                    UserBundle savedNewUb = userBundleRepository.save(newUserBundle);
                    finalUserBundlesForUserObject.add(savedNewUb);
                } else {
                    logger.debug("User ID {}: DTO for key {} marked as deleted and no existing UserBundle found. Skipping creation.", user.getId(), currentSubKey);
                }
            }
        }

        for (UserBundle staleUserBundle : existingUserBundlesMap.values()) {
            if (!staleUserBundle.isDeleted()) {
                logger.debug("User ID {}: Soft-deleting stale UserBundle (ID: {}) as it's no longer in DTO.", user.getId(), staleUserBundle.getId());
                staleUserBundle.setDeleted(true);
                staleUserBundle.setStatus(UserBundle.BundleStatus.INACTIVE);
                UserBundle savedStaleUb = userBundleRepository.save(staleUserBundle);
                finalUserBundlesForUserObject.add(savedStaleUb);
            } else {
                logger.debug("User ID {}: Stale UserBundle (ID: {}) was already soft-deleted. Retaining in final set.", user.getId(), staleUserBundle.getId());
                finalUserBundlesForUserObject.add(staleUserBundle);
            }
        }

        logger.debug("User ID {}: Finalizing user's bundles collection. Size: {}", user.getId(), finalUserBundlesForUserObject.size());
        user.getBundles().clear();
        user.getBundles().addAll(finalUserBundlesForUserObject);
    }


    private Location findOrCreateLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {
            logger.warn("findOrCreateLocation called with null locationDTO.");
            throw new ValidationException("Location data cannot be null for findOrCreate operation.");
        }
        logger.debug("Finding or creating location for DTO: {}", locationDTO);

        if (locationDTO.getLocationId() != null) {
            Optional<Location> byId = locationRepository.findById(locationDTO.getLocationId());
            if (byId.isPresent()) {
                logger.debug("Found location by ID: {}", locationDTO.getLocationId());
                return byId.get();
            }
            logger.debug("Location ID {} provided in DTO, but not found. Proceeding to find by properties or create.", locationDTO.getLocationId());
        }

        Optional<Location> existingByProps = findExistingLocation(locationDTO);
        if (existingByProps.isPresent()) {
            logger.debug("Found existing location by properties: ID {}", existingByProps.get().getLocationId());
            return existingByProps.get();
        }

        logger.debug("No existing location found by ID or properties. Creating new location from DTO: {}", locationDTO);
        Location newLocation = locationMapper.toLocation(locationDTO);
        newLocation.setLocationId(null);
        Location savedLocation = locationRepository.save(newLocation);
        logger.info("Created and saved new location with ID: {}", savedLocation.getLocationId());
        return savedLocation;
    }


    private Optional<Location> findExistingLocation(LocationDTO locationDTO) {
        logger.debug("Attempting to find existing location by properties for DTO: {}", locationDTO);
        if (locationDTO == null) {
            throw new ValidationException("Location data cannot be null");
        }
        try {
            Location location = locationMapper.toLocation(locationDTO);
            location.setLocationId(null);

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIncludeNullValues()
                    .withIgnorePaths("locationId", "createdAt", "updatedAt")
                    .withStringMatcher(StringMatcher.EXACT);

            Example<Location> example = Example.of(location, matcher);
            Optional<Location> found = locationRepository.findOne(example);
            if(found.isPresent()){
                logger.debug("Found existing location by properties: ID {}", found.get().getLocationId());
            } else {
                logger.debug("No existing location found by properties for DTO: {}", locationDTO);
            }
            return found;
        } catch (Exception ex) {
            logger.error("Failed to find existing location for DTO: {}", locationDTO, ex);
            throw new OperationFailedException("Failed to find existing location", ex);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id == null) {
            logger.warn("Attempted to delete user with null ID.");
            throw new ValidationException("User ID cannot be null");
        }
        logger.info("Attempting to delete user with ID: {}", id);
        User user = userRepository.findByIdWithBundlesAndLocation(id)
                .orElseThrow(() -> {
                    logger.warn("User not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        if (user.isDeleted()){
            logger.info("User ID {} is already soft-deleted. No action taken.", id);
            return;
        }

        if (user.getBundles() == null) user.setBundles(new HashSet<>());
        if (user.getBundles().stream().anyMatch(ub -> ub != null && !ub.isDeleted())) {
            logger.warn("Cannot delete user ID {} due to active bundles.", id);
            throw new InvalidOperationException("Cannot delete user with active bundles. Please deactivate or remove bundles first.");
        }

        user.setDeleted(true);
        user.setStatus(User.UserStatus.INACTIVE);

        user.getBundles().forEach(ub -> {
            if(ub != null && !ub.isDeleted()){
                ub.setDeleted(true);
                ub.setStatus(UserBundle.BundleStatus.INACTIVE);
                userBundleRepository.save(ub);
            }
        });
        userRepository.save(user);
        logger.info("User ID {} successfully soft-deleted.", id);
    }
}