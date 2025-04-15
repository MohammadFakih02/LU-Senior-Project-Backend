package com.example.internetprovidermanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.internetprovidermanagement.models.UserBundle;

@Repository
public interface UserBundleRepository extends JpaRepository<UserBundle, Long> {
}