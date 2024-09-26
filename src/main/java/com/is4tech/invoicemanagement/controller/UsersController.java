package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.LoginDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.response.LoginResponse;
import com.is4tech.invoicemanagement.service.JwtService;
import com.is4tech.invoicemanagement.service.UsersService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequestMapping("/invoice-management/v0.1/auth/")
@RestController
public class UsersController {
    private final JwtService jwtService;
    private final UsersService authenticationService;

    public UsersController(JwtService jwtService, UsersService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody @Valid UsersDto usersDto) {
        User registeredUser = authenticationService.signup(usersDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginDto) {
        User authenticatedUser = authenticationService.authenticate(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setAuthorities(authenticatedUser.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(loginResponse);
    }
}
