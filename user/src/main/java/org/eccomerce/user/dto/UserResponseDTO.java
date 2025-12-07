package org.eccomerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor

public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private String role;
    private List<AddressDTO> addresses;
}
