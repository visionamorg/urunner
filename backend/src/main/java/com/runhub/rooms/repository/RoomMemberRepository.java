package com.runhub.rooms.repository;

import com.runhub.rooms.model.RoomMember;
import com.runhub.rooms.model.RoomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, RoomMemberId> {
    List<RoomMember> findByRoomId(Long roomId);
    boolean existsByIdRoomIdAndIdUserId(Long roomId, Long userId);
    long countByRoomId(Long roomId);
    void deleteByIdRoomIdAndIdUserId(Long roomId, Long userId);
}
