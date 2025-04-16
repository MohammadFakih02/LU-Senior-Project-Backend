// UserResponseDTO.java (for getAll)
package com.example.internetprovidermanagement.dtos;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.internetprovidermanagement.models.User;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String landLine;
    private String phone;
    private LocalDate subscriptionDate;
    private User.UserStatus status;
    private LocationDTO location;
    private Set<String> bundleNames;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.landLine = user.getLandLine();
        this.phone = user.getPhone();
        this.subscriptionDate = user.getSubscriptionDate();
        this.status = user.getStatus();
        this.location = new LocationDTO();
        this.location.setLocationId(user.getLocation().getLocationId());
        this.location.setAddress(user.getLocation().getAddress());
        
        this.bundleNames = user.getBundles().stream()
                .map(ub -> ub.getBundle().getName())
                .collect(Collectors.toSet());
    }
}