package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.LocationMapper;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.repositories.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = locationMapper.toLocation(locationDTO);
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toLocationDTO(savedLocation);
    }

    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return locationMapper.toLocationDTO(location);
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toLocationDTO)
                .collect(Collectors.toList());
    }

    public List<LocationDTO> getLocationsByCity(String city) {
        return locationRepository.findByCity(city)
                .stream()
                .map(locationMapper::toLocationDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        existingLocation.setAddress(locationDTO.getAddress());
        existingLocation.setCity(locationDTO.getCity());
        existingLocation.setStreet(locationDTO.getStreet());
        existingLocation.setBuilding(locationDTO.getBuilding());
        existingLocation.setFloor(locationDTO.getFloor());
        existingLocation.setGoogleMapsUrl(locationDTO.getGoogleMapsUrl());
        
        Location updatedLocation = locationRepository.save(existingLocation);
        return locationMapper.toLocationDTO(updatedLocation);
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}