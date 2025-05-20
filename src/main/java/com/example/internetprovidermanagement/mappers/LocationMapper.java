package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy; // Import this

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.models.Location;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL) // Add this strategy
public interface LocationMapper {

    LocationDTO toLocationDTO(Location location);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Location toLocation(LocationDTO locationDTO);

    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLocationFromDto(LocationDTO locationDTO, @MappingTarget Location location);
}