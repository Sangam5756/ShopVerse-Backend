package org.ecommerce.user.service;

import lombok.RequiredArgsConstructor;
import org.ecommerce.user.dto.AddressDTO;
import org.ecommerce.user.model.Address;
import org.ecommerce.user.model.User;
import org.ecommerce.user.repository.AddressRepository;
import org.ecommerce.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public AddressDTO addAddress(Long userId, AddressDTO dto) {
        User user = getUser(userId);

        Address address = new Address();
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPincode(dto.getPincode());
        address.setUser(user);

        Address saved = addressRepository.save(address);

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByUser(Long userId) {
        User user = getUser(userId);

        return user.getAddresses()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO dto) {
        Address address = getUserAddress(userId, addressId);

        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPincode(dto.getPincode());

        return mapToDTO(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getUserAddress(userId, addressId);
        addressRepository.delete(address);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private Address getUserAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Address not found"
                ));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Unauthorized address access"
            );
        }
        return address;
    }

    private AddressDTO mapToDTO(Address address) {
        return new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPincode()
        );
    }
}
