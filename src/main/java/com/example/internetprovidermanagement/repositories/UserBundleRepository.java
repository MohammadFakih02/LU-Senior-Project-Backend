package com.example.internetprovidermanagement.repositories;

import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBundleRepository extends JpaRepository<UserBundle, Long> {
    List<UserBundle> findByUser(User user);
    List<UserBundle> findByBundleId(Long bundleId);
    
    @Query("SELECT ub FROM UserBundle ub JOIN FETCH ub.bundle WHERE ub.user.id = :userId")
    List<UserBundle> findByUserIdWithBundle(Long userId);
    
    boolean existsByUserIdAndBundleId(Long userId, Long bundleId);
}