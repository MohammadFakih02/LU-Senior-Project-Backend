package com.example.internetprovidermanagement.repositories;

import com.example.internetprovidermanagement.models.UserBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.Bundle;

import java.util.List;

// BundleRepository.java
@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bundle b WHERE b.name = :name AND b.deleted = false")
    boolean existsByName(String name);

    @Query("SELECT b FROM Bundle b WHERE b.deleted = false")
    List<Bundle> findAllActiveBundles();

    @Query("SELECT ub FROM UserBundle ub LEFT JOIN FETCH ub.payments WHERE ub.bundle.bundleId = :bundleId")
    List<UserBundle> findByBundleIdWithPayments(@Param("bundleId") Long bundleId);
}