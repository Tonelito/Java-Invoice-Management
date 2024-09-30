package com.is4tech.invoicemanagement.service;

import com.is4tech.invoicemanagement.dto.UsersDto;
import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.ProfileRespository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRespository profileRespository;

    public Page<User> listAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User saveUser(UsersDto usersDto) {
        Profile profile = profileRespository.findById(usersDto.getProfileId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        User user = User.builder()
                .userId(usersDto.getUserId())
                .fullName(usersDto.getFullName())
                .email(usersDto.getEmail())
                .password(usersDto.getPassword())
                .dateOfBirth(usersDto.getDateOfBirth())
                .status(usersDto.getStatus())
                .profile(profile)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User findByIdUser(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public boolean existById(Integer userId) {
        return userRepository.existsById(userId);
    }
}
