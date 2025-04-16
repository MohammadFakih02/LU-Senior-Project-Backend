package com.example.internetprovidermanagement.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.example.internetprovidermanagement.dtos.CreateUpdateUserDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserResponseDTO;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserBundleMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "bundleNames", expression = "java(mapBundleNames(user.getBundles()))")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "bundles", source = "bundles", qualifiedByName = "mapBundles")
    UserDetailsDTO toUserDetailsDTO(User user);

    @Mapping(target = "bundles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(CreateUpdateUserDTO createUpdateUserDTO);

    @Named("mapBundles")
    default Set<UserBundleDetailsDTO> mapBundles(Set<UserBundle> bundles) {
        if (bundles == null) {
            return null;
        }
        return bundles.stream()
                .map(UserBundleMapper.INSTANCE::toUserBundleDetailsDTO)
                .collect(Collectors.toSet());
    }

    default Set<String> mapBundleNames(Set<UserBundle> bundles) {
        if (bundles == null) return null;
        return bundles.stream()
                .map(ub -> ub.getBundle().getName())
                .collect(Collectors.toSet());
    }
}