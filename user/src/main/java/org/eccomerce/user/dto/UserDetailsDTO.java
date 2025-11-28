package org.eccomerce.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UserDetailsDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private List<AddressResponseDTO> addresses;
}
