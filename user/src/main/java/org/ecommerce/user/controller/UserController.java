package org.ecommerce.user.controller;


import lombok.AllArgsConstructor;
import org.ecommerce.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;



   @GetMapping("/profile")
    public ResponseEntity<?> getUserById() {
       return ResponseEntity.ok(userService.getUser());
   }







}
