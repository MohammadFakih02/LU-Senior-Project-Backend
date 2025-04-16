package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.internetprovidermanagement.dtos.UserBundleDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {BundleMapper.class, LocationMapper.class})
public interface UserBundleMapper {

    UserBundleMapper INSTANCE = Mappers.getMapper(UserBundleMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bundleId", source = "bundle.bundleId")
    UserBundleDTO toUserBundleDTO(UserBundle userBundle);

    @Mapping(target = "userBundleId", source = "id")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "bundleLocation", source = "user.location")
    UserBundleDetailsDTO toUserBundleDetailsDTO(UserBundle userBundle);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bundle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserBundle toUserBundle(UserBundleDTO userBundleDTO);
}