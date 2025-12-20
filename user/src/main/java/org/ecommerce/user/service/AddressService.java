package org.ecommerce.user.service;

import org.ecommerce.user.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO addAddress(Long userId, AddressDTO addressDTO);

    List<AddressDTO> getAddressesByUser(Long userId);

    AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    void deleteAddress(Long userId, Long addressId);
}
