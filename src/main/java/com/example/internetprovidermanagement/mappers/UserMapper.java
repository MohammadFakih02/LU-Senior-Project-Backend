package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "bundleId", source = "bundle.id")
    @Mapping(target = "locationId", source = "location.id")
    UserDTO toUserDTO(User user);

    @Mapping(target = "bundle", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserDTO userDTO);

    @Mapping(target = "id", source = "userDTO.id")
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUserWithRelations(UserDTO userDTO, Bundle bundle, Location location);
}