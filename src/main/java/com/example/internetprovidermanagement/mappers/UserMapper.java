package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.models.Bundle;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "bundleName", source = "bundle.name")
    @Mapping(target = "locationAddress", expression = "java(getLocationAddress(user.getLocation()))")
    UserDTO toUserDTO(User user);

    @Mapping(target = "bundle", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUserWithRelations(UserDTO userDTO, Bundle bundle, Location location);

    @Mapping(target = "bundle", source = "bundle")
    @Mapping(target = "location", source = "location")
    UserDetailsDTO toUserDetailsDTO(User user);

    default String getLocationAddress(Location location) {
        if (location == null) {
            return null;
        }
        return String.format("%s, %s, %s", 
            location.getStreet(), 
            location.getBuilding(), 
            location.getCity());
    }
}