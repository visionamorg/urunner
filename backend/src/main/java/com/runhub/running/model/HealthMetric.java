package com.runhub.running.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "health_metrics",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    /** Resting heart rate in bpm */
    @Column(name = "resting_heart_rate")
    private Integer restingHeartRate;

    /** Garmin sleep score 0–100 */
    @Column(name = "sleep_score")
    private Integer sleepScore;

    /** VO2 Max estimate (ml/kg/min) */
    @Column(name = "vo2_max")
    private Double vo2Max;

    /** Fitness age (years) */
    @Column(name = "fitness_age")
    private Integer fitnessAge;

    /** HRV status: BALANCED, UNBALANCED, POOR, etc. */
    @Column(name = "hrv_status", length = 50)
    private String hrvStatus;

    /** Max body battery level for the day (0–100) */
    @Column(name = "body_battery_max")
    private Integer bodyBatteryMax;

    /** Average stress level for the day (0–100) */
    @Column(name = "stress_level")
    private Integer stressLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
