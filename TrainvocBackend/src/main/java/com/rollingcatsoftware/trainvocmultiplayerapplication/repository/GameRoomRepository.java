package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface GameRoomRepository extends JpaRepository<GameRoom, String> {
    void deleteByLastUsedBefore(LocalDateTime time);

    GameRoom findByRoomCode(String roomCode);
}
