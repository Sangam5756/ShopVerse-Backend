package org.ecommerce.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String city;
    private String state;
    private String country;
    private String pincode;
}

