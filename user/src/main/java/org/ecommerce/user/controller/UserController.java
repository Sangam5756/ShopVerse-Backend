package org.ecommerce.user.controller;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.ecommerce.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<?> getUserProfile(@RequestHeader("X-User-Email") String email) {
		return ResponseEntity.ok(userService.getUserByEmail(email));
	}
}
