package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.EmailDto;
import com.is4tech.invoicemanagement.dto.LoginDto;
import com.is4tech.invoicemanagement.dto.UserChangePasswordDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.dto.VerificCodeRequest;
import com.is4tech.invoicemanagement.exception.EmailAlreadyExistsException;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.response.LoginResponse;
import com.is4tech.invoicemanagement.service.JwtService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.PasswordValidator;
import com.is4tech.invoicemanagement.utils.ResetCodeGenerator;
import com.is4tech.invoicemanagement.utils.SendEmail;

import com.is4tech.invoicemanagement.service.AuthService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RequestMapping("/invoice-management/v0.1/auth/")
@RestController
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final SendEmail sendEmail;

    public AuthController(JwtService jwtService, AuthService authenticationService, PasswordEncoder passwordEncoder, SendEmail sendEmail) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
        this.sendEmail = sendEmail;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody @Valid UsersDto usersDto) throws MessagingException {
        if (authenticationService.emailExists(usersDto.getEmail())) {
            throw new EmailAlreadyExistsException("El correo ya está registrado");
        }

        usersDto.setStatus(true);
        User registeredUser = authenticationService.signup(usersDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginDto) {
        User authenticatedUser = authenticationService.authenticate(loginDto);

        Integer userId = authenticatedUser.getUserId();

        String jwtToken = jwtService.generateToken(authenticatedUser, userId);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setAuthorities(authenticatedUser.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setUserId(userId);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/recover-password")
    public ResponseEntity<Message> recoverPassword(@RequestBody EmailDto emailDto) throws MessagingException {
        authenticationService.findByEmail(emailDto.getEmail());
        String passwordCode = ResetCodeGenerator.getPassword(
                ResetCodeGenerator.MINUSCULAS +
                        ResetCodeGenerator.MAYUSCULAS +
                        ResetCodeGenerator.NUMEROS, 10);

        sendEmail.sendEmailRestorationCode(
                emailDto.getEmail(),
                "infoFactura@facturacio.fac.com",
                "Recovery Password",
                passwordCode
        );

        return new ResponseEntity<>(Message.builder()
                .note("Email Send")
                .object(null)
                .build(),
                HttpStatus.OK);
    }

    @PostMapping("/verific-code")
    public ResponseEntity<Message> verificRecoverPassword(@RequestBody VerificCodeRequest verificCodeRequest) {
        String passwordValidationResult = PasswordValidator.validatePassword(verificCodeRequest.getCodePassword().getNewPassword());
        if (passwordValidationResult.isEmpty()) {
            return new ResponseEntity<>(Message.builder()
                    .note("Password validation failed")
                    .object(passwordValidationResult)
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }
        String response = sendEmail.verificCode(verificCodeRequest.getCodePassword());
        if (response.equals("Code valid")) {
            String newPassword = passwordEncoder.encode(verificCodeRequest.getCodePassword().getNewPassword());
            authenticationService.updatePasswordCode(newPassword, verificCodeRequest.getEmail().getEmail());
            response = "The password modified successfully";


            return new ResponseEntity<>(Message.builder()
                    .note("Code verification result")
                    .object(response)
                    .build(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(Message.builder()
                .note("Code verification error internal")
                .object(response)
                .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Message> changePassword(@RequestBody UserChangePasswordDto userChangePasswordDto) {
        String passwordValidationResult = PasswordValidator.validatePassword(userChangePasswordDto.getNewPassword());

        if (!passwordValidationResult.equals("La contraseña es válida.")) {
            return new ResponseEntity<>(Message.builder()
                    .note("Password validation failed")
                    .object(passwordValidationResult)
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }

        String message = "The password not modified";
        User user = authenticationService.findByEmail(userChangePasswordDto.getEmail());

        if (passwordEncoder.matches(userChangePasswordDto.getPassword(), user.getPassword())) {
            authenticationService.updatePasswordCode(passwordEncoder.encode(userChangePasswordDto.getNewPassword()), userChangePasswordDto.getEmail());
            message = "The password modified";
        }

        return new ResponseEntity<>(Message.builder()
                .note("Code verification result")
                .object(message)
                .build(),
                HttpStatus.OK);
    }
}