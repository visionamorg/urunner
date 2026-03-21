package com.runhub.users.service;

import com.runhub.config.ResourceNotFoundException;
import com.runhub.users.dto.UpdateUserRequest;
import com.runhub.users.dto.UserDto;
import com.runhub.users.mapper.UserMapper;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setLocation(request.getLocation());
        user.setRunningCategory(request.getRunningCategory());
        user.setPassion(request.getPassion());
        user.setGender(request.getGender());
        user.setYearsRunning(request.getYearsRunning());
        user.setWeeklyGoalKm(request.getWeeklyGoalKm());
        user.setPb5k(request.getPb5k());
        user.setPb10k(request.getPb10k());
        user.setPbHalfMarathon(request.getPbHalfMarathon());
        user.setPbMarathon(request.getPbMarathon());
        user.setInstagramHandle(request.getInstagramHandle());
        return userMapper.toDto(userRepository.save(user));
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Transactional
    public void awardRunPoints(User user, int points) {
        user.setRunPoints((user.getRunPoints() == null ? 0 : user.getRunPoints()) + points);
        userRepository.save(user);
    }
}
