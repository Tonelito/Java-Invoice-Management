package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.LoginDto;
import com.is4tech.invoicemanagement.dto.RegisterDto;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.response.LoginResponse;
import com.is4tech.invoicemanagement.service.AuthenticationService;
import com.is4tech.invoicemanagement.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

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
