package org.ecommerce.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequestDTO {

    private String fullName;
    private String email;
    private String phoneNo;
    private String password;  // plaintext, will be hashed in service
}
