package org.eccomerce.user.mapper;

import org.eccomerce.user.dto.AddressDTO;
import org.eccomerce.user.dto.UserResponseDTO;
import org.eccomerce.user.model.User;

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
