package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.UserSearchDto;
import com.is4tech.invoicemanagement.dto.UserUpdateDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.service.UserService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.MessagePage;
import com.is4tech.invoicemanagement.utils.PasswordGenerator;
import com.is4tech.invoicemanagement.utils.SendEmail;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice-management/v0.1/user")
public class UserController {

    private final UserService userService;
    private final AuditService auditService;
    private final SendEmail sendEmail;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, SendEmail sendEmail, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userService = userService;
        this.sendEmail = sendEmail;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    private static final String NAME_ENTITY = "Users";
    private static final String ID_ENTITY = "user_id";
    int statusCode;

    @PostMapping("/create")
    public ResponseEntity<Message> saveUser(@RequestBody @Valid UsersDto userDto, HttpServletRequest request) {
        try {
            User userSave = userService.saveUser(userDto, request);

            if (userSave != null) {
                String generatePassword = PasswordGenerator.generatePassword();
                sendEmail.sendEmailPassword(
                        userSave.getEmail(),
                        "infoFactura@facturacio.fac.com",
                        "Credentials",
                        "Your login credentials are: \nEmail = " + userSave.getEmail() +
                                "\nPassword = " + generatePassword);
            }

            statusCode = HttpStatus.CREATED.value();
            return new ResponseEntity<>(Message.builder()
                    .note("Saved Successfully")
                    .object(UsersDto.builder()
                            .userId(userSave.getUserId())
                            .fullName(userSave.getFullName())
                            .email(userSave.getEmail())
                            .profileId(userSave.getProfile().getProfileId())
                            .dateOfBirth(userSave.getDateOfBirth())
                            .status(userSave.getStatus())
                            .build())
                    .build(),
                    HttpStatus.CREATED);

        } catch (DataAccessException e) {
            statusCode = HttpStatus.BAD_REQUEST.value();
            throw new BadRequestException("Error saving record: " + e.getMessage());
        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Message> updateUser(@PathVariable Integer id, @RequestBody @Valid UserUpdateDto userUpdateDto, HttpServletRequest request) {
        try {
            if (!userService.existById(id)) {
                throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
            }

            userUpdateDto.setUserId(id);
            User userUpdate = userService.updateUser(id, userUpdateDto, request);

            UsersDto updatedUserDto = UsersDto.builder()
                    .userId(userUpdate.getUserId())
                    .fullName(userUpdate.getFullName())
                    .email(userUpdate.getEmail())
                    .profileId(userUpdate.getProfile() != null ? userUpdate.getProfile().getProfileId() : null)
                    .dateOfBirth(userUpdate.getDateOfBirth())
                    .status(userUpdate.getStatus())
                    .build();

            return new ResponseEntity<>(Message.builder()
                    .note("Updated Successfully")
                    .object(updatedUserDto)
                    .build(), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error updating record: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/show-all")
    public ResponseEntity<MessagePage> showAllUsers(Pageable pageable, HttpServletRequest request) {
        try {
            MessagePage message = userService.listAllUsers(pageable, request);
            statusCode = HttpStatus.OK.value();

            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(null, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/search")
    public ResponseEntity<MessagePage> searchUsers(@RequestBody UserSearchDto userSearchDto, Pageable pageable, HttpServletRequest request) {
        try {
            MessagePage message = userService.findByName(userSearchDto, pageable, request);
            statusCode = HttpStatus.OK.value();

            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(userSearchDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw e;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(userSearchDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new BadRequestException("Unexpected error occurred: " + e.getMessage());
        }
    }
}