package com.example.internetprovidermanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.User;

// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.deleted = false")
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deleted = false")
    List<User> findByStatus(User.UserStatus status);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deleted = false")
    boolean existsByEmail(String email); //1

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone AND u.deleted = false")
    boolean existsByPhone(String phone); //1

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "LEFT JOIN FETCH u.location l " +
            "WHERE u.id = :id " +
            "AND u.deleted = false")
    Optional<User> findByIdWithBundlesAndLocation(@Param("id") Long id); //1

    @Query("SELECT u FROM User u JOIN FETCH u.location WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithLocation(Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "LEFT JOIN FETCH u.location l " +
            "WHERE u.id = :id " +
            "AND u.deleted = false " +
            "AND (ub.deleted = false OR ub IS NULL)")
    Optional<User> findByIdWithActiveBundlesAndLocation(@Param("id") Long id);
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "WHERE u.deleted = false")
    List<User> findAllActiveUsers(); //1

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.bundles ub " +
            "LEFT JOIN FETCH ub.payments p " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithBundlesAndPayments(@Param("id") Long id);//1

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.bundles ub " +
            "WHERE u.deleted = false " +
            "AND ub.deleted = false")
    List<User> findAllActiveUsersWithActiveBundles();

}