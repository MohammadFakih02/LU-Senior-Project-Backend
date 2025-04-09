package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.models.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationDTO toLocationDTO(Location location);

    Location toLocation(LocationDTO locationDTO);
}