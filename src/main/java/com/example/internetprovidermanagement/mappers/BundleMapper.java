package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.models.Bundle;

@Mapper(componentModel = "spring")
public interface BundleMapper {

    BundleDTO toDto(Bundle bundle);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Bundle toEntity(BundleDTO bundleDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateBundleFromDto(BundleDTO bundleDTO, @MappingTarget Bundle bundle);
}