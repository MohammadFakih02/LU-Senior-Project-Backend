package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.internetprovidermanagement.dtos.BundleDTO;
import com.example.internetprovidermanagement.models.Bundle;

@Mapper(componentModel = "spring")
public interface BundleMapper {

    BundleMapper INSTANCE = Mappers.getMapper(BundleMapper.class);

    BundleDTO toBundleDTO(Bundle bundle);

    Bundle toBundle(BundleDTO bundleDTO);
}