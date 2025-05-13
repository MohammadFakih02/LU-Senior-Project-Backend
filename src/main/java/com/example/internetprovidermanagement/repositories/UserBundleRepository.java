package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;

// UserBundleRepository.java
@Repository
public interface UserBundleRepository extends JpaRepository<UserBundle, Long> {

    // Existing methods with soft-deletion check added
    @Query("SELECT ub FROM UserBundle ub WHERE ub.user = :user AND ub.deleted = false")
    List<UserBundle> findByUser(@Param("user") User user);

    @Query("SELECT ub FROM UserBundle ub WHERE ub.bundle.bundleId = :bundleId AND ub.deleted = false")
    List<UserBundle> findByBundleBundleId(@Param("bundleId") Long bundleId);

    @Query("SELECT ub FROM UserBundle ub JOIN FETCH ub.bundle WHERE ub.user.id = :userId AND ub.deleted = false")
    List<UserBundle> findByUserIdWithBundle(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END " +
            "FROM UserBundle ub WHERE ub.user = :user AND ub.bundle = :bundle " +
            "AND ub.location = :location AND ub.deleted = false")
    boolean existsByUserAndBundleAndLocation(@Param("user") User user,
                                             @Param("bundle") Bundle bundle,
                                             @Param("location") Location location);

    // New method from previous conversation
    @Query("SELECT ub FROM UserBundle ub WHERE " +
            "ub.user = :user AND " +
            "ub.bundle = :bundle AND " +
            "ub.location = :location AND " +
            "ub.deleted = false")
    Optional<UserBundle> findByUserAndBundleAndLocation(
            @Param("user") User user,
            @Param("bundle") Bundle bundle,
            @Param("location") Location location
    );

    @Query("SELECT ub FROM UserBundle ub LEFT JOIN FETCH ub.payments WHERE ub.id = :id")
    Optional<UserBundle> findByIdWithPayments(@Param("id") Long id);
}