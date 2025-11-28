package org.eccomerce.user.service;

import org.eccomerce.user.model.Address;

import java.util.List;

public interface AddressService {

    Address addAddress(Long userId, Address address);

    List<Address> getAddresses(Long userId);

    void deleteAddress(Long addressId);
}
