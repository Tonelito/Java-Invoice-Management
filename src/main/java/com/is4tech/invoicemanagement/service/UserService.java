package com.is4tech.invoicemanagement.service;

import com.is4tech.invoicemanagement.dto.UserSearchDto;
import com.is4tech.invoicemanagement.dto.UserUpdateDto;
import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.ProfileRespository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import com.is4tech.invoicemanagement.utils.MessagePage;
import com.is4tech.invoicemanagement.utils.PasswordGenerator;
import com.is4tech.invoicemanagement.utils.SendEmail;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRespository profileRepository;
    private final AuditService auditService;
    private final SendEmail sendEmail;

    private static final String NAME_ENTITY = "Users";
    private static final String ID_ENTITY = "user_id";
    int statusCode;

    @Transactional
    public MessagePage listAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        if (users.isEmpty()) {
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, pageable.toString());
        }

        statusCode = HttpStatus.OK.value();

        List<UsersDto> userDtos = users.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return MessagePage.builder()
                .note("Users Retrieved Successfully")
                .object(userDtos)
                .totalElements((int) users.getTotalElements())
                .totalPages(users.getTotalPages())
                .currentPage(users.getNumber())
                .pageSize(users.getSize())
                .build();
    }

    @Transactional
    public User saveUser(UsersDto usersDto, HttpServletRequest request) throws MessagingException {
        try {
            Profile profile = profileRepository.findById(usersDto.getProfileId())
                    .orElseThrow(() -> new ResourceNorFoundException("Profile not found"));

            String rawPassword = PasswordGenerator.generatePassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            usersDto.setStatus(true);

            User user = User.builder()
                    .fullName(usersDto.getFullName())
                    .email(usersDto.getEmail())
                    .password(encodedPassword)
                    .dateOfBirth(usersDto.getDateOfBirth())
                    .status(usersDto.getStatus())
                    .profile(profile)
                    .build();

            sendEmail.sendEmailPassword(
                    usersDto.getEmail(),
                    "infoFactura@facturacio.fac.com",
                    "Credentials",
                    rawPassword
            );

            User savedUser = userRepository.save(user);

            statusCode = HttpStatus.CREATED.value();
            auditService.logAudit(usersDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

            return savedUser;
        } catch (ResourceNorFoundException e) {
            statusCode = HttpStatus.NOT_FOUND.value();
            auditService.logAudit(usersDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw e;
        } catch (DataAccessException e) {
            statusCode = HttpStatus.BAD_REQUEST.value();
            auditService.logAudit(usersDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new BadRequestException("Error saving record: " + e.getMessage());
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(usersDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw e;
        }
    }



    @Transactional
    public User updateUser(Integer id, UserUpdateDto userUpdateDto, HttpServletRequest request) {
        if (userUpdateDto.getUserId() == null) {
            throw new BadRequestException("User ID cannot be null");
        }

        try {
            Profile profile = null;
            if (userUpdateDto.getProfileId() != null) {
                profile = profileRepository.findById(userUpdateDto.getProfileId())
                        .orElseThrow(() -> new ResourceNorFoundException("Profile not found"));
            }

            User existingUser = userRepository.findById(userUpdateDto.getUserId())
                    .orElseThrow(() -> new ResourceNorFoundException("User not found"));

            if (userUpdateDto.getFullName() != null) {
                existingUser.setFullName(userUpdateDto.getFullName());
            }
            if (userUpdateDto.getDateOfBirth() != null) {
                existingUser.setDateOfBirth(userUpdateDto.getDateOfBirth());
            }
            if (profile != null) {
                existingUser.setProfile(profile);
            }

            User savedUser = userRepository.save(existingUser);

            statusCode = HttpStatus.OK.value();
            auditService.logAudit(userUpdateDto, this.getClass().getMethods()[0], null, statusCode, NAME_ENTITY, request);

            return savedUser;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            auditService.logAudit(userUpdateDto, this.getClass().getMethods()[0], e, statusCode, NAME_ENTITY, request);
            throw new BadRequestException("Error updating user: " + e.getMessage());
        }
    }

    @Transactional
    public MessagePage findByName(UserSearchDto userSearchDto, Pageable pageable) {
        if (userSearchDto == null || userSearchDto.getFullName() == null) {
            throw new BadRequestException("UserSearchDto or fullName cannot be null");
        }

        String name = userSearchDto.getFullName();
        Page<User> users = userRepository.findByFullNameContainingIgnoreCase(name, pageable);

        if (users.isEmpty()) {
            throw new ResourceNorFoundException(NAME_ENTITY, "FullName", name);
        }

        List<UsersDto> userDtos = users.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return MessagePage.builder()
                .note("Users Found")
                .object(userDtos)
                .totalElements((int) users.getTotalElements())
                .totalPages(users.getTotalPages())
                .currentPage(users.getNumber())
                .pageSize(users.getSize())
                .build();
    }

    @Transactional
    public UsersDto findByIdUser(Integer userId, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, userId.toString());
        }

        return toDto(user);
    }

    public UsersDto toDto(User user){
        return UsersDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profileId(user.getProfile() != null ? user.getProfile().getProfileId() : null)
                .dateOfBirth(user.getDateOfBirth())
                .status(Optional.ofNullable(user.getStatus()).orElse(true))
                .build();
    }

    public User toggleUserStatus(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNorFoundException("User not found"));

        user.setStatus(!user.getStatus());

        userRepository.save(user);

        return user;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existById(Integer userId) {
        return userRepository.existsById(userId);
    }
}
