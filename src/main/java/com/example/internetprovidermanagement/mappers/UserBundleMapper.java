package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {BundleMapper.class, LocationMapper.class})
public interface UserBundleMapper {

    @Mapping(target = "userBundleId", source = "id")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "bundleLocation", source = "location")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "consumption", source = "consumption")
    @Mapping(target = "subscriptionDate", source = "subscriptionDate")
    UserBundleDetailsDTO toUserBundleDetailsDTO(UserBundle userBundle);
}