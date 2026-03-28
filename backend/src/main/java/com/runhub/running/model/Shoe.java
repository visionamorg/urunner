package com.runhub.running.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Shoe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(length = 100)
    private String nickname;

    @Column(name = "max_distance_km", nullable = false)
    @Builder.Default
    private Double maxDistanceKm = 800.0;

    @Column(name = "total_distance_km", nullable = false)
    @Builder.Default
    private Double totalDistanceKm = 0.0;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean retired = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public double getWearPercent() {
        if (maxDistanceKm == null || maxDistanceKm == 0) return 0;
        return Math.min(100, (totalDistanceKm / maxDistanceKm) * 100);
    }
}
