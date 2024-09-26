package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.service.UserService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.PasswordGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice-management/vo.1/")
public class UserController {

    @Autowired
    private UserService userService;

    private static final String NAME_ENTITY = "Users";
    private static final String ID_ENTITY = "user_id";

    @PostMapping("/user")
    public ResponseEntity<Message> saveUser(@RequestBody @Valid UsersDto userDto) {
        String generatePassword = PasswordGenerator.generatePassword();
        userDto.setPassword(generatePassword);
        User userSave = null;

        try {
            userSave = userService.saveUser(userDto);
            return new ResponseEntity<>(Message.builder()
                    .note("Saved Successfully")
                    .object(UsersDto.builder()
                            .userId(userSave.getUserId())
                            .fullName(userSave.getFullName())
                            .email(userSave.getEmail())
                            .password(userSave.getPassword())
                            .profileId(userSave.getProfile().getProfileId())
                            .dateOfBirth(userSave.getDateOfBirth())
                            .status(userSave.getStatus())
                            .build())
                    .build(),
                    HttpStatus.CREATED);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error save record: " + e.getMessage());
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Message> updateUser(@PathVariable Integer id, @RequestBody @Valid UsersDto userDto) {
        User userUpdate = null;
        try {
            if (userService.existById(id)) {
                userDto.setUserId(id);
                userUpdate = userService.saveUser(userDto);
                return new ResponseEntity<>(Message.builder()
                        .note("Updated Successfully")
                        .object(UsersDto.builder()
                        .userId(userUpdate.getUserId())
                        .fullName(userUpdate.getFullName())
                        .email(userUpdate.getEmail())
                        .password(userUpdate.getPassword())
                                .profileId(userUpdate.getProfile().getProfileId())
                        .dateOfBirth(userUpdate.getDateOfBirth())
                        .status(userUpdate.getStatus())
                        .build())
                        .build(),
                        HttpStatus.OK);
            } else {
                throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());
            }
        } catch (DataAccessException e) {
            throw new BadRequestException("Error update record: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> getUser(@PathVariable Integer id) {
        User user = userService.findByIdUser(id);
        if (user == null)
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, id.toString());

            return new ResponseEntity<>(Message.builder()
            .note("User Found")
            .object(UsersDto.builder()
            .userId(user.getUserId())
            .fullName(user.getFullName())
            .email(user.getEmail())
                    .profileId(user.getProfile().getProfileId())
            .password(user.getPassword())
            .dateOfBirth(user.getDateOfBirth())
            .status(user.getStatus())
                    .build())
                    .build(),
                    HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Message> showAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<User> users = userService.listAllUsers(pageable);
        if (users.isEmpty())
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, pageable.toString());

        return new ResponseEntity<>(Message.builder()
                .note("Users Retrieved Successfully")
                .object(users.getContent())
                .build(),
                HttpStatus.OK);
    }
}