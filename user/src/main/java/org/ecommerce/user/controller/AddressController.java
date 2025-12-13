package org.ecommerce.user.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.user.dto.AddressDTO;
import org.ecommerce.user.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDTO addAddress(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody AddressDTO dto) {
        return addressService.addAddress(userId, dto);
    }

    @GetMapping
    public List<AddressDTO> getAddresses(@RequestHeader("X-User-Id") Long userId) {
        return addressService.getAddressesByUser(userId);
    }

    @PutMapping("/{addressId}")
    public AddressDTO updateAddress(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long addressId,
                                    @RequestBody AddressDTO dto) {
        return addressService.updateAddress(userId, addressId, dto);
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
    }
}
