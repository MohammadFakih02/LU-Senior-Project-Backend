package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
// No need for MappingTarget here as it's a direct DTO mapping

import com.example.internetprovidermanagement.dtos.UserBundleDetailsDTO;
import com.example.internetprovidermanagement.models.UserBundle;

@Mapper(componentModel = "spring", uses = {BundleMapper.class, LocationMapper.class})
public interface UserBundleMapper {

    // UserBundle to UserBundleDetailsDTO
    // 'deleted' field exists in both with the same name, so MapStruct will map it by convention.
    // Explicit mapping is only needed if names differ or for custom logic.
    // The previous explicit @Mapping(target = "deleted", source = "deleted") was redundant but not harmful
    // unless another mapping for "deleted" existed.
    // To be safe and clear, let convention handle it if names match.
    @Mapping(target = "userBundleId", source = "id")
    @Mapping(target = "bundle", source = "bundle") // Uses BundleMapper
    @Mapping(target = "bundleLocation", source = "location") // Uses LocationMapper
    @Mapping(target = "status", source = "status") // Assumes enum to String mapping or same type
    @Mapping(target = "consumption", source = "consumption")
    @Mapping(target = "subscriptionDate", source = "subscriptionDate")
    // 'deleted' will be mapped by convention (UserBundle.deleted -> UserBundleDetailsDTO.deleted)
    UserBundleDetailsDTO toUserBundleDetailsDTO(UserBundle userBundle);

    // Note: There isn't a DTO -> UserBundle mapping here. If UserBundleDTO is used to update
    // a UserBundle entity, that logic is typically in the service or a different mapper/method.
    // The UserBundleDTO provided earlier is for updating a UserBundle, but there was no
    // UserBundleMapper method for UserBundleDTO -> UserBundle. This is handled in UserBundleService.
}