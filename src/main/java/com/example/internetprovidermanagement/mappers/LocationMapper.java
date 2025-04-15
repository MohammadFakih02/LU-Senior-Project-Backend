package com.example.internetprovidermanagement.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.models.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    LocationDTO toDto(Location location);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Location toEntity(LocationDTO locationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLocationFromDto(LocationDTO locationDTO, @MappingTarget Location location);

    @AfterMapping
    default void setDefaultValues(@MappingTarget Location location) {
        if (location.getGoogleMapsUrl() == null) {
            location.setGoogleMapsUrl("");
        }
    }
}