package com.example.internetprovidermanagement.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.exceptions.ResourceNotFoundException;
import com.example.internetprovidermanagement.mappers.LocationMapper;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.repositories.LocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toDto(savedLocation);
    }

    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return locationMapper.toDto(location);
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LocationDTO> getLocationsByCity(String city) {
        return locationRepository.findByCity(city).stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());
    }

    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        
        locationMapper.updateLocationFromDto(locationDTO, existingLocation);
        Location updatedLocation = locationRepository.save(existingLocation);
        return locationMapper.toDto(updatedLocation);
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}