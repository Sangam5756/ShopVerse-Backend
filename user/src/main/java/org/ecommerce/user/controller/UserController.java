package org.ecommerce.user.controller;


import lombok.RequiredArgsConstructor;
import org.ecommerce.user.dto.UserCreateRequest;
import org.ecommerce.user.dto.UserResponse;
import org.ecommerce.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/email/{email}")
	public UserResponse getUserByEmail(@PathVariable String email) {
		return userService.getUserByEmail(email);
	}

	@GetMapping("/me")
	public UserResponse me(@RequestHeader("X-User-Email") String email) {
		return userService.getUserByEmail(email);
	}

	@PutMapping("/me")
	public UserResponse update(@RequestHeader("X-User-Email") String email,
							   @RequestBody UserCreateRequest request) {
		return userService.updateProfile(email, request);
	}
}
