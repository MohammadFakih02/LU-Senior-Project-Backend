package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping; // Import BeanMapping
import org.mapstruct.NullValuePropertyMappingStrategy; // Import for updates

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.dtos.BundleResponseDTO;
import com.example.internetprovidermanagement.models.Bundle;

@Mapper(componentModel = "spring")
public interface BundleMapper {

    // Bundle to BundleDTO (for create/update request bodies, might contain deleted flag)
    BundleDTO toBundleDTO(Bundle bundle);

    // Bundle to BundleResponseDTO (for responses, will show current deleted status)
    BundleResponseDTO toBundleResponseDTO(Bundle bundle);

    // BundleDTO to Bundle (for creation)
    // 'deleted' from DTO will be used if present, otherwise defaults to Bundle's default (false)
    // createdAt and updatedAt are managed by BaseEntity/JPA
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // No need to explicitly map 'deleted' if names match and no special logic
    Bundle toBundle(BundleDTO bundleDTO);

    // Update Bundle from BundleDTO
    // bundleId, createdAt, updatedAt are system-managed or immutable for updates.
    // 'deleted' should be updatable from the DTO IF the service logic allows it.
    // However, for soft delete, the service usually handles setting 'deleted = true'.
    // If DTO can undelete, then mapping it is fine.
    // Let's assume the service handles setting 'deleted = true' via a dedicated delete endpoint.
    // For general updates, we usually don't want the 'deleted' flag to be arbitrarily changed by a generic update DTO.
    // If the service *does* want to allow BundleDTO to control 'deleted' on update:
    // then just let MapStruct map it by name.
    // If the service *does not* want BundleDTO to control 'deleted' on general updates:
    @Mapping(target = "bundleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Explicitly ignore if updates shouldn't change deleted status
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // For partial updates
    void updateBundleFromDto(BundleDTO bundleDTO, @MappingTarget Bundle bundle);
}