package com.runhub.users.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "provider_access_token", length = 1000)
    private String providerAccessToken;

    @Column(name = "provider_refresh_token", length = 1000)
    private String providerRefreshToken;

    @Column(name = "provider_token_secret", length = 1000)
    private String providerTokenSecret;

    @Column(name = "provider_token_expires_at")
    private Long providerTokenExpiresAt;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(length = 200)
    private String location;

    @Column(name = "running_category", length = 50)
    private String runningCategory;

    @Column(columnDefinition = "TEXT")
    private String passion;

    @Column(length = 20)
    private String gender;

    @Column(name = "years_running")
    private Integer yearsRunning;

    @Column(name = "weekly_goal_km")
    private Double weeklyGoalKm;

    @Column(name = "pb_5k", length = 20)
    private String pb5k;

    @Column(name = "pb_10k", length = 20)
    private String pb10k;

    @Column(name = "pb_half_marathon", length = 20)
    private String pbHalfMarathon;

    @Column(name = "pb_marathon", length = 20)
    private String pbMarathon;

    @Column(name = "instagram_handle", length = 100)
    private String instagramHandle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getDisplayUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
