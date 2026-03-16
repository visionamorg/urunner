package com.runhub.chat.repository;

import com.runhub.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByCommunityIdOrderBySentAtAsc(Long communityId);
    List<Message> findByEventIdOrderBySentAtAsc(Long eventId);
}
