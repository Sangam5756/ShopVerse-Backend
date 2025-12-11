package org.ecommerce.user.mapper;

import org.ecommerce.user.dto.AddressDTO;
import org.ecommerce.user.dto.UserResponseDTO;
import org.ecommerce.user.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDTO mapToDto(User u) {
        return new UserResponseDTO(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getPhoneNo(),
                u.getRole().name(),
                u.getAddresses().stream()
                        .map(a -> new AddressDTO(a.getAddressId(), a.getCity(), a.getState(), a.getCountry(), a.getPincode()))
                        .collect(Collectors.toList())
        );
    }
}
