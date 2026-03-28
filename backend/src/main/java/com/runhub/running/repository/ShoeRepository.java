package com.runhub.running.repository;

import com.runhub.running.model.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    List<Shoe> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Shoe> findByUserIdAndRetiredFalseOrderByCreatedAtDesc(Long userId);
    Optional<Shoe> findByUserIdAndIsDefaultTrue(Long userId);
}
