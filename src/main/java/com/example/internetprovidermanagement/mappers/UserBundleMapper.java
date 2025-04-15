package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {BundleMapper.class, PaymentMapper.class, LocationMapper.class})
public interface UserBundleMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bundleId", source = "bundle.id")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "location", source = "user.location")
    UserBundleDTO toDto(UserBundle userBundle);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "subscriptionDate", source = "subscriptionDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "consumption", source = "consumption")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "location", source = "user.location")
    @Mapping(target = "payments", source = "payments")
    UserBundleDetailsDTO toDetailsDto(UserBundle userBundle);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bundle", ignore = true)
    @Mapping(target = "payments", ignore = true)
    UserBundle toEntity(UserBundleDTO userBundleDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bundle", ignore = true)
    @Mapping(target = "payments", ignore = true)
    void updateUserBundleFromDto(UserBundleDTO userBundleDTO, @MappingTarget UserBundle userBundle);
}