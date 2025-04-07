package com.example.internetprovidermanagement.services;

import org.springframework.stereotype.Component;

import com.example.internetprovidermanagement.dtos.LocationDTO;
import com.example.internetprovidermanagement.dtos.PaymentDTO;
import com.example.internetprovidermanagement.dtos.ServiceDTO;
import com.example.internetprovidermanagement.dtos.UserDTO;
import com.example.internetprovidermanagement.models.Location;
import com.example.internetprovidermanagement.models.Payment;
import com.example.internetprovidermanagement.models.Service;
import com.example.internetprovidermanagement.models.User;

@Component
public class ModelMapper {

    // Location conversions
    public LocationDTO toLocationDTO(Location location) {
        if (location == null) {
            return null;
        }
        
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setAddress(location.getAddress());
        dto.setCity(location.getCity());
        dto.setStreet(location.getStreet());
        dto.setBuilding(location.getBuilding());
        dto.setFloor(location.getFloor());
        dto.setGoogleMapsUrl(location.getGoogleMapsUrl());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
        return dto;
    }

    public Location toLocation(LocationDTO locationDTO) {
        if (locationDTO == null) {
            return null;
        }
        
        Location location = new Location();
        location.setId(locationDTO.getId());
        location.setAddress(locationDTO.getAddress());
        location.setCity(locationDTO.getCity());
        location.setStreet(locationDTO.getStreet());
        location.setBuilding(locationDTO.getBuilding());
        location.setFloor(locationDTO.getFloor());
        location.setGoogleMapsUrl(locationDTO.getGoogleMapsUrl());
        // createdAt and updatedAt are automatically managed by BaseEntity
        return location;
    }

    // Payment conversions
    public PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setMethod(payment.getMethod());
        dto.setUserId(payment.getUser() != null ? payment.getUser().getId() : null);
        dto.setDueDate(payment.getDueDate());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    public Payment toPayment(PaymentDTO paymentDTO, User user) {
        if (paymentDTO == null) {
            return null;
        }
        
        Payment payment = new Payment();
        payment.setId(paymentDTO.getId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setStatus(paymentDTO.getStatus());
        payment.setMethod(paymentDTO.getMethod());
        payment.setUser(user);
        payment.setDueDate(paymentDTO.getDueDate());
        // createdAt and updatedAt are automatically managed by BaseEntity
        return payment;
    }

    // Service conversions
    public ServiceDTO toServiceDTO(Service service) {
        if (service == null) {
            return null;
        }
        
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setType(service.getType());
        dto.setPrice(service.getPrice());
        dto.setDataCap(service.getDataCap());
        dto.setSpeed(service.getSpeed());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        return dto;
    }

    public Service toService(ServiceDTO serviceDTO) {
        if (serviceDTO == null) {
            return null;
        }
        
        Service service = new Service();
        service.setId(serviceDTO.getId());
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setType(serviceDTO.getType());
        service.setPrice(serviceDTO.getPrice());
        service.setDataCap(serviceDTO.getDataCap());
        service.setSpeed(serviceDTO.getSpeed());
        // createdAt and updatedAt are automatically managed by BaseEntity
        return service;
    }

    // User conversions
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setLandLine(user.getLandLine());
        dto.setPhone(user.getPhone());
        dto.setConsumption(user.getConsumption());
        dto.setBill(user.getBill());
        dto.setSubscriptionDate(user.getSubscriptionDate());
        dto.setStatus(user.getStatus());
        dto.setServiceId(user.getService() != null ? user.getService().getId() : null);
        dto.setLocationId(user.getLocation() != null ? user.getLocation().getId() : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public User toUser(UserDTO userDTO, Service service, Location location) {
        if (userDTO == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setLandLine(userDTO.getLandLine());
        user.setPhone(userDTO.getPhone());
        user.setConsumption(userDTO.getConsumption());
        user.setBill(userDTO.getBill());
        user.setSubscriptionDate(userDTO.getSubscriptionDate());
        user.setStatus(userDTO.getStatus());
        user.setService(service);
        user.setLocation(location);
        // createdAt and updatedAt are automatically managed by BaseEntity
        return user;
    }
}