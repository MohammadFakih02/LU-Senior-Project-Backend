package com.example.internetprovidermanagement.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.BeanMapping; // For update method configuration
import org.mapstruct.NullValuePropertyMappingStrategy; // For update method configuration


import com.example.internetprovidermanagement.dtos.CreateUpdateUserDTO;
import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.dtos.UserResponseDTO;
import com.example.internetprovidermanagement.models.User;
import com.example.internetprovidermanagement.models.UserBundle;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserBundleMapper.class})
public interface UserMapper {

    // User to UserResponseDTO
    @Mapping(target = "id", source = "id") // Assuming UserResponseDTO has 'id' not 'userId'
    @Mapping(target = "status", source = "status") // Enum to String or same type
    @Mapping(target = "bundleNames", expression = "java(mapBundleNames(user.getBundles()))")
    // UserResponseDTO does not have a 'deleted' field in the provided DTO definition.
    UserResponseDTO toUserResponseDTO(User user);

    // User to UserDetailsDTO
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "bundles", source = "bundles", qualifiedByName = "mapActiveBundlesToDetailsDTO") // Use custom method
    @Mapping(target = "location", source = "location") // Uses LocationMapper
    @Mapping(target = "status", source = "status")
    // UserDetailsDTO has 'boolean deleted;' - this will be mapped by convention from User.deleted
    UserDetailsDTO toUserDetailsDTO(User user);

    // CreateUpdateUserDTO to User (for creation)
    // User.id, User.createdAt, User.updatedAt, User.deleted are managed by system/service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true) // User.deleted is false by default on creation, service handles it
    @Mapping(target = "bundles", ignore = true) // Bundles are managed separately by the service
    // location is mapped by convention (CreateUpdateUserDTO.location -> User.location via LocationMapper)
    User toUser(CreateUpdateUserDTO createUpdateUserDTO);

    // Update User from CreateUpdateUserDTO
    // id, createdAt, updatedAt, deleted status, and bundles are managed carefully by the service or are immutable.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Service's deleteUser method handles this
    @Mapping(target = "location", ignore = true) // Location update is handled explicitly in service if DTO provides it
    @Mapping(target = "bundles", ignore = true)  // Bundles are managed by updateUserBundles in service
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(CreateUpdateUserDTO dto, @MappingTarget User user);

    // Renamed from mapBundles to be more specific, and it uses UserBundleMapper
    @Named("mapActiveBundlesToDetailsDTO")
    default Set<UserBundleDetailsDTO> mapActiveBundlesToDetailsDTO(Set<UserBundle> bundles) {
        if (bundles == null) {
            return null;
        }
        // UserBundleMapper should be injected or accessible if this default method needs it.
        // However, MapStruct can directly use UserBundleMapper for the Set<UserBundle> -> Set<UserBundleDetailsDTO>
        // if UserBundleMapper is listed in 'uses'. Let's rely on that.
        // The 'uses = UserBundleMapper.class' should automatically find toUserBundleDetailsDTO.
        // This custom qualifiedByName method is useful if you need to filter or do complex logic.
        // Here, we ensure only non-deleted UserBundles are mapped.
        UserBundleMapper ubMapper = Mappers.getMapper(UserBundleMapper.class); // Or inject if possible
        return bundles.stream()
                .filter(ub -> !ub.isDeleted()) // Ensure only active bundles are detailed
                .map(ubMapper::toUserBundleDetailsDTO)
                .collect(Collectors.toSet());
    }

    // This was the direct mapping method previously inside UserMapper, now relying on UserBundleMapper.
    // If UserBundleMapper is in `uses`, MapStruct will try to find a suitable method there for UserBundle -> UserBundleDetailsDTO.
    // The method toUserBundleDetailsDTO(UserBundle userBundle) in UserBundleMapper will be used.

    default Set<String> mapBundleNames(Set<UserBundle> bundles) {
        if (bundles == null) return null;
        return bundles.stream()
                .filter(ub -> !ub.isDeleted() && ub.getBundle() != null) // Only active bundles with valid bundle info
                .map(ub -> ub.getBundle().getName())
                .collect(Collectors.toSet());
    }
}