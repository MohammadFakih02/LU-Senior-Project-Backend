package com.example.internetprovidermanagement.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.internetprovidermanagement.dtos.CreateUpdateUserDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserResponseDTO;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserBundleMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "bundleNames", expression = "java(mapBundleNames(user.getBundles()))")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "bundles", source = "bundles", qualifiedByName = "mapBundles")
    @Mapping(target = "location", source = "location")
    UserDetailsDTO toUserDetailsDTO(User user);

    // Add to creation/update methods:
    @Mapping(target = "deleted", ignore = true) // Protect deletion status

// Updated toUser method:
    @Mapping(target = "bundles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Add this
    User toUser(CreateUpdateUserDTO createUpdateUserDTO);

    // Updated update method:
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "bundles", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Add this
    void updateUserFromDto(CreateUpdateUserDTO dto, @MappingTarget User user);

    @Named("mapBundles")
    default Set<UserBundleDetailsDTO> mapBundles(Set<UserBundle> bundles) {
        if (bundles == null) {
            return null;
        }
        return bundles.stream()
                .map(this::toUserBundleDetailsDTO)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "userBundleId", source = "id")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "bundleLocation", source = "location")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "consumption", source = "consumption")
    @Mapping(target = "subscriptionDate", source = "subscriptionDate")
    UserBundleDetailsDTO toUserBundleDetailsDTO(UserBundle userBundle);

    default Set<String> mapBundleNames(Set<UserBundle> bundles) {
        if (bundles == null) return null;
        return bundles.stream()
                .map(ub -> ub.getBundle().getName())
                .collect(Collectors.toSet());
    }
}