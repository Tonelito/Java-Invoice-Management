package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.LoginDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.response.LoginResponse;
import com.is4tech.invoicemanagement.service.JwtService;
import com.is4tech.invoicemanagement.service.UsersService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.ResetCodeGenerator;
import com.is4tech.invoicemanagement.utils.SendEmail;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequestMapping("/invoice-management/v0.1/auth/")
@RestController
public class UsersController {
    private final JwtService jwtService;
    private final UsersService authenticationService;
    private final SendEmail sendEmail;

    public UsersController(JwtService jwtService, UsersService authenticationService, SendEmail sendEmail) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.sendEmail = sendEmail;
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

    @PostMapping("/recover-password")
    public ResponseEntity<Message> recoverPassword(@RequestBody String email) {
        String passwordCode = ResetCodeGenerator.getPassword(
                                ResetCodeGenerator.MINUSCULAS+
                                ResetCodeGenerator.MAYUSCULAS+
                                ResetCodeGenerator.NUMEROS,10); 
                                
        sendEmail.sendEmailRestorationCode(
                email,
                "infoFactura@facturacio.fac.com", 
                "Recovery Password",
                "Your recovery code is: \n" + passwordCode, passwordCode);

        return new ResponseEntity<>(Message.builder()
                    .note("Email Send")
                    .object(null)
                    .build(),
                    HttpStatus.OK);
    }

    @PostMapping("/verific-code")
    public ResponseEntity<Message> verificRecoverPassword(@RequestBody String code) {
        String response = sendEmail.verificCode(code);

        return new ResponseEntity<>(Message.builder()
                    .note("Code is valued succeful")
                    .object(response)
                    .build(),
                    HttpStatus.OK);
    }
}
