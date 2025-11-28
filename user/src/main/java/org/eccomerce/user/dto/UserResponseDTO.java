package org.eccomerce.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.eccomerce.user.model.Role;

@Getter @Setter
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private Role role;
}
