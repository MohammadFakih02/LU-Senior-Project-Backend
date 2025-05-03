package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.dtos.BundleResponseDTO;
import com.example.internetprovidermanagement.models.Bundle;

@Mapper(componentModel = "spring")
public interface BundleMapper {

    BundleDTO toBundleDTO(Bundle bundle);

    BundleResponseDTO toBundleResponseDTO(Bundle bundle);

    // Add to both methods:
    @Mapping(target = "deleted", ignore = true) // Prevent DTOs from modifying deletion status

// Updated toBundle method:
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Add this
    Bundle toBundle(BundleDTO bundleDTO);

    // Updated update method:
    @Mapping(target = "bundleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true) // Add this
    void updateBundleFromDto(BundleDTO bundleDTO, @MappingTarget Bundle bundle);
}