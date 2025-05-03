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

// UserBundleRepository.java
@Repository
public interface UserBundleRepository extends JpaRepository<UserBundle, Long> {
    @Query("SELECT ub FROM UserBundle ub WHERE ub.user = :user AND ub.deleted = false")
    List<UserBundle> findByUser(User user);

    @Query("SELECT ub FROM UserBundle ub WHERE ub.bundle.bundleId = :bundleId AND ub.deleted = false")
    List<UserBundle> findByBundleBundleId(Long bundleId);

    @Query("SELECT ub FROM UserBundle ub JOIN FETCH ub.bundle WHERE ub.user.id = :userId AND ub.deleted = false")
    List<UserBundle> findByUserIdWithBundle(Long userId);

    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBundle ub WHERE ub.user = :user AND ub.bundle = :bundle AND ub.location = :location AND ub.deleted = false")
    boolean existsByUserAndBundleAndLocation(User user, Bundle bundle, Location location);

    @Query("SELECT ub FROM UserBundle ub WHERE ub.user = :user AND ub.bundle = :bundle AND ub.location = :location AND ub.deleted = false")
    Optional<UserBundle> findByUserAndBundleAndLocation(User user, Bundle bundle, Location location);

    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBundle ub WHERE ub.user.id = :userId AND ub.bundle.bundleId = :bundleId AND ub.deleted = false")
    boolean existsByUserIdAndBundleBundleId(Long userId, Long bundleId);
}