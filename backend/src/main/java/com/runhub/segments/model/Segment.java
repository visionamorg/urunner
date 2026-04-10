package com.runhub.segments.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "segments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Segment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "distance_km", precision = 6, scale = 3)
    private BigDecimal distanceKm;
    @Column(name = "start_lat", precision = 10, scale = 7)
    private BigDecimal startLat;
    @Column(name = "start_lng", precision = 10, scale = 7)
    private BigDecimal startLng;
    @Column(name = "end_lat", precision = 10, scale = 7)
    private BigDecimal endLat;
    @Column(name = "end_lng", precision = 10, scale = 7)
    private BigDecimal endLng;
    @Column(length = 20)
    private String difficulty;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
