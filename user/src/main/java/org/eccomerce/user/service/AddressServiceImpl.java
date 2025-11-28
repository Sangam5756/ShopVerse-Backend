package org.eccomerce.user.service;

import lombok.RequiredArgsConstructor;
import org.eccomerce.user.model.Address;
import org.eccomerce.user.model.User;
import org.eccomerce.user.repository.AddressRepository;
import org.eccomerce.user.repository.UserRepository;
import org.eccomerce.user.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Address addAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
