package com.is4tech.invoicemanagement.service;

import com.is4tech.invoicemanagement.dto.LoginDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.ProfileRespository;
import com.is4tech.invoicemanagement.repository.AuthRepository;
import com.is4tech.invoicemanagement.utils.PasswordGenerator;
import com.is4tech.invoicemanagement.utils.ResetCodeGenerator;
import com.is4tech.invoicemanagement.utils.SendEmail;

import jakarta.mail.MessagingException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthRepository userRepository;
    private final ProfileRespository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SendEmail sendEmail;

    public AuthService(
            AuthRepository userRepository,
            ProfileRespository profileRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            SendEmail sendEmail
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.sendEmail = sendEmail;
    }

    public User signup(UsersDto usersDto) throws MessagingException {
        User user = new User();
        String password = PasswordGenerator.generatePassword();

        user.setFullName(usersDto.getFullName());
        user.setEmail(usersDto.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setDateOfBirth(usersDto.getDateOfBirth());

        Profile profile = profileRepository.findById(usersDto.getProfileId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        user.setProfile(profile);

        user.setStatus(usersDto.getStatus());

        sendEmail.sendEmailPassword(
                usersDto.getEmail(),
                "infoFactura@facturacio.fac.com",
                "Credentails",
                password);

        User userSave = userRepository.save(user);

        return userSave;
    }

    public User authenticate(LoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
    }

    public void updatePasswordCode(String newPassword, String email){
        userRepository.updatePassword(newPassword,email);
    }
}
