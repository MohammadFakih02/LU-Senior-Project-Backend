package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;

@Repository
public interface UserBundleRepository extends JpaRepository<UserBundle, Long> {
    List<UserBundle> findByUser(User user);
    List<UserBundle> findByBundleBundleId(Long bundleId);
    
    @Query("SELECT ub FROM UserBundle ub JOIN FETCH ub.bundle WHERE ub.user.id = :userId")
    List<UserBundle> findByUserIdWithBundle(Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bundles WHERE u.id = :id")
    Optional<User> findByIdWithBundles(Long id);

    boolean existsByUserAndBundleAndLocation(User user, Bundle bundle, Location location); 

    Optional<UserBundle> findByUserAndBundleAndLocation(User user, Bundle bundle, Location location);
    
    boolean existsByUserIdAndBundleBundleId(Long userId, Long bundleId);
}