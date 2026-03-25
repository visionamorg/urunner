package com.runhub.running.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_splits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ActivitySplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private RunningActivity activity;

    @Column(name = "split_km", nullable = false)
    private Integer splitKm;

    @Column(name = "split_pace")
    private Double splitPace;

    @Column(name = "split_elevation")
    private Double splitElevation;

    @Column(name = "split_heart_rate")
    private Integer splitHeartRate;
}
