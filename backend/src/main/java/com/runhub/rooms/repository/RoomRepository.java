package com.runhub.rooms.repository;

import com.runhub.rooms.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCommunityId(Long communityId);
    List<Room> findByCommunityIdAndIsPrivateFalse(Long communityId);
}
