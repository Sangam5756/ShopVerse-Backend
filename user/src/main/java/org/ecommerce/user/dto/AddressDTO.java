package org.ecommerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    Long addressId;
    String city;
    String state;
    String country;
    String pincode;



}
