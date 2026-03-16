package com.runhub.communities.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommunityMemberId implements Serializable {

    @Column(name = "community_id")
    private Long communityId;

    @Column(name = "user_id")
    private Long userId;
}
