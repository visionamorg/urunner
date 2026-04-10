package com.runhub.users.model;

import lombok.*;
import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UserFollowId implements Serializable {
    private Long followerId;
    private Long followingId;
}
