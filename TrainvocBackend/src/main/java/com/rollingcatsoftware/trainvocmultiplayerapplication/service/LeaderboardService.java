package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.GameRoomRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    private final PlayerRepository playerRepository;
    private final GameRoomRepository gameRoomRepository;

    @Autowired
    public LeaderboardService(PlayerRepository playerRepository, GameRoomRepository gameRoomRepository) {
        this.playerRepository = playerRepository;
        this.gameRoomRepository = gameRoomRepository;
    }

    public List<Player> getSortedPlayers(GameRoom room) {
        if (room.getPlayers() == null) {
            return java.util.Collections.emptyList();
        }
        return room.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    public List<Player> getTop3Players(GameRoom room) {
        return getSortedPlayers(room).stream().limit(3).collect(Collectors.toList());
    }

    public List<Player> getLeaderboard() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    public List<Player> getLeaderboardByRoom(String roomCode) {
        return gameRoomRepository.findById(roomCode)
                .map(this::getSortedPlayers)
                .orElseGet(List::of);
    }
}
