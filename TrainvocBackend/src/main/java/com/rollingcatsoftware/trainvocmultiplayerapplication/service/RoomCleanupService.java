package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoomCleanupService {
    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Scheduled(fixedRate = 60000) // Her 60 saniyede bir çalışır
    public void removeUnusedRooms() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15); // 15 dakikadan eski odalar
        // lastUsed null olan odaları da sil
        gameRoomRepository.findAll().stream()
                .filter(room -> room.getLastUsed() == null || room.getLastUsed().isBefore(threshold))
                .forEach(gameRoomRepository::delete);
    }
}
