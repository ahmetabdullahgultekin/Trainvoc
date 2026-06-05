package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoomCleanupService {
    private final GameRoomRepository gameRoomRepository;

    public RoomCleanupService(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
    }

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void removeUnusedRooms() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15); // Rooms older than 15 minutes
        // Also delete rooms whose lastUsed is null
        gameRoomRepository.findAll().stream()
                .filter(room -> room.getLastUsed() == null || room.getLastUsed().isBefore(threshold))
                .forEach(gameRoomRepository::delete);
    }
}
