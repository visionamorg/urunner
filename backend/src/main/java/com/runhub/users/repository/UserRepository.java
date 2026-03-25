package com.runhub.users.repository;

import com.runhub.users.model.AuthProvider;
import com.runhub.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);

    java.util.List<User> findAllByAuthProviderAndProviderAccessTokenIsNotNull(AuthProvider authProvider);
}
