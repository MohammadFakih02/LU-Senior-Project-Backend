package com.example.internetprovidermanagement.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.dtos.UserDetailsDTO;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.User;


@Mapper(componentModel = "spring", uses = {UserBundleMapper.class, LocationMapper.class})
public interface UserMapper {
    @Mapping(target = "locationId", source = "location.id")
    UserDTO toDto(User user);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "bundles", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "bundles", source = "bundles")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "landLine", source = "landLine")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "subscriptionDate", source = "subscriptionDate")
    UserDetailsDTO toDetailsDto(User user);

    @Named("idToLocation")
    default Location idToLocation(Long id) {
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bundles", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);
    
    
}