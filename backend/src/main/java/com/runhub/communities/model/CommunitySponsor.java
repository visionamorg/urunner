package com.runhub.communities.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_sponsors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CommunitySponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(name = "logo_url", nullable = false, length = 500)
    private String logoUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(length = 100)
    private String name;
}
