package com.runhub.chat.repository;

import com.runhub.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByCommunityIdOrderBySentAtAsc(Long communityId);
    List<Message> findByEventIdOrderBySentAtAsc(Long eventId);
    List<Message> findByRoomIdOrderBySentAtAsc(Long roomId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender.id = :userId AND m.community.id = :communityId AND m.sentAt >= :since")
    long countBySenderIdAndCommunityIdAndSentAtAfter(@Param("userId") Long userId, @Param("communityId") Long communityId, @Param("since") LocalDateTime since);
}
