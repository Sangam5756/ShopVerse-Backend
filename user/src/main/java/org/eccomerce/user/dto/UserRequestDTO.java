package org.eccomerce.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequestDTO {

    private String fullName;
    private String email;
    private String phoneNo;
    private String password;  // plaintext, will be hashed in service
}
