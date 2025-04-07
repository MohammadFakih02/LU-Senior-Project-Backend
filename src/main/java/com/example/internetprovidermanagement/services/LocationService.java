package com.example.internetprovidermanagement.services;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.repositories.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service // This marks it as a Spring service bean
public class LocationService {

    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    // Constructor injection (recommended over field injection)
    public LocationService(LocationRepository locationRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.modelMapper = modelMapper;
    }

    // Create a new location
    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = modelMapper.toLocation(locationDTO);
        Location savedLocation = locationRepository.save(location);
        return modelMapper.toLocationDTO(savedLocation);
    }

    // Get location by ID
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return modelMapper.toLocationDTO(location);
    }

    // Get all locations
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(modelMapper::toLocationDTO)
                .collect(Collectors.toList());
    }

    // Get locations by city
    public List<LocationDTO> getLocationsByCity(String city) {
        return locationRepository.findByCity(city)
                .stream()
                .map(modelMapper::toLocationDTO)
                .collect(Collectors.toList());
    }

    // Update location
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        // Update fields
        existingLocation.setAddress(locationDTO.getAddress());
        existingLocation.setCity(locationDTO.getCity());
        existingLocation.setStreet(locationDTO.getStreet());
        existingLocation.setBuilding(locationDTO.getBuilding());
        existingLocation.setFloor(locationDTO.getFloor());
        existingLocation.setGoogleMapsUrl(locationDTO.getGoogleMapsUrl());
        
        Location updatedLocation = locationRepository.save(existingLocation);
        return modelMapper.toLocationDTO(updatedLocation);
    }

    // Delete location
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}