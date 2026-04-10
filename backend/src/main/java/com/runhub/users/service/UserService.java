package com.runhub.users.service;

import com.runhub.config.ResourceNotFoundException;
import com.runhub.users.dto.NotificationPreferenceDto;
import com.runhub.users.dto.PublicProfileDto;
import com.runhub.users.dto.UpdateUserRequest;
import com.runhub.users.dto.UserDto;
import com.runhub.users.mapper.UserMapper;
import com.runhub.users.model.User;
import com.runhub.users.model.UserFollow;
import com.runhub.users.repository.UserFollowRepository;
import com.runhub.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserFollowRepository userFollowRepository;

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
    public NotificationPreferenceDto getNotificationPreferences(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setEmailInvites(user.getEmailInvites());
        return dto;
    }

    @Transactional
    public NotificationPreferenceDto updateNotificationPreferences(String email, NotificationPreferenceDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (request.getEmailInvites() != null) {
            user.setEmailInvites(request.getEmailInvites());
        }
        userRepository.save(user);
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setEmailInvites(user.getEmailInvites());
        return dto;
    }

    @Transactional
    public void awardRunPoints(User user, int points) {
        user.setRunPoints((user.getRunPoints() == null ? 0 : user.getRunPoints()) + points);
        userRepository.save(user);
    }

    @Transactional
    public void follow(String currentUserEmail, String targetUsername) {
        User currentUser = getUserEntityByEmail(currentUserEmail);
        User target = userRepository.findByUsername(targetUsername)
            .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));
        if (currentUser.getId().equals(target.getId())) return;
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), target.getId())) {
            UserFollow follow = UserFollow.builder()
                .followerId(currentUser.getId())
                .followingId(target.getId())
                .build();
            userFollowRepository.save(follow);
        }
    }

    @Transactional
    public void unfollow(String currentUserEmail, String targetUsername) {
        User currentUser = getUserEntityByEmail(currentUserEmail);
        User target = userRepository.findByUsername(targetUsername)
            .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));
        userFollowRepository.findByFollowerIdAndFollowingId(currentUser.getId(), target.getId())
            .ifPresent(userFollowRepository::delete);
    }

    public PublicProfileDto getPublicProfile(String username, String currentUserEmail) {
        User target = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        User currentUser = currentUserEmail != null ? getUserEntityByEmail(currentUserEmail) : null;
        boolean isFollowing = currentUser != null &&
            userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), target.getId());
        return PublicProfileDto.builder()
            .id(target.getId())
            .username(target.getUsername())
            .firstName(target.getFirstName())
            .lastName(target.getLastName())
            .profileImageUrl(target.getProfileImageUrl())
            .bio(target.getBio())
            .location(target.getLocation())
            .runningCategory(target.getRunningCategory())
            .pb5k(target.getPb5k())
            .pb10k(target.getPb10k())
            .pbHalfMarathon(target.getPbHalfMarathon())
            .pbMarathon(target.getPbMarathon())
            .followerCount(userFollowRepository.countByFollowingId(target.getId()))
            .followingCount(userFollowRepository.countByFollowerId(target.getId()))
            .following(isFollowing)
            .build();
    }

    public List<UserDto> searchUsers(String q) {
        return userRepository.findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(q, q)
            .stream().limit(10)
            .map(u -> {
                UserDto dto = new UserDto();
                dto.setId(u.getId());
                dto.setUsername(u.getUsername());
                dto.setFirstName(u.getFirstName());
                dto.setLastName(u.getLastName());
                dto.setProfileImageUrl(u.getProfileImageUrl());
                return dto;
            })
            .toList();
    }
}
