package com.rollingcatsoftware.trainvocmultiplayerapplication.dto.mapper;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.GameRoomResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.PlayerResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.response.RoomListItemResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting game entities to DTOs.
 * Centralizes all entity-to-DTO conversion logic.
 */
@Component
public class GameMapper {

    /**
     * Convert Player entity to PlayerResponse DTO.
     */
    public PlayerResponse toPlayerResponse(Player player) {
        if (player == null) {
            return null;
        }
        return PlayerResponse.builder()
                .id(player.getId())
                .name(player.getName())
                .score(player.getScore())
                .correctCount(player.getCorrectCount())
                .wrongCount(player.getWrongCount())
                .totalAnswerTime(player.getTotalAnswerTime())
                .avatarId(player.getAvatarId())
                .currentAnsweredQuestionIndex(player.getCurrentAnsweredQuestionIndex())
                .build();
    }

    /**
     * Convert list of Player entities to PlayerResponse DTOs.
     */
    public List<PlayerResponse> toPlayerResponseList(List<Player> players) {
        if (players == null) {
            return null;
        }
        return players.stream()
                .map(this::toPlayerResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert GameRoom entity to full GameRoomResponse DTO.
     */
    public GameRoomResponse toGameRoomResponse(GameRoom room) {
        if (room == null) {
            return null;
        }
        return GameRoomResponse.builder()
                .roomCode(room.getRoomCode())
                .players(toPlayerResponseList(room.getPlayers()))
                .currentQuestionIndex(room.getCurrentQuestionIndex())
                .started(room.getStarted())
                .hostId(room.getHostId())
                .questionDuration(room.getQuestionDuration())
                .optionCount(room.getOptionCount())
                .level(room.getLevel())
                .totalQuestionCount(room.getTotalQuestionCount())
                .lastUsed(room.getLastUsed())
                .hasPassword(room.getHashedPassword() != null && !room.getHashedPassword().isEmpty())
                .currentState(room.getCurrentState())
                .stateStartTime(room.getStateStartTime())
                .build();
    }

    /**
     * Convert GameRoom entity to simplified RoomListItemResponse DTO.
     */
    public RoomListItemResponse toRoomListItemResponse(GameRoom room) {
        if (room == null) {
            return null;
        }
        return RoomListItemResponse.builder()
                .roomCode(room.getRoomCode())
                .playerCount(room.getPlayers() != null ? room.getPlayers().size() : 0)
                .started(room.getStarted())
                .hostId(room.getHostId())
                .questionDuration(room.getQuestionDuration())
                .level(room.getLevel())
                .totalQuestionCount(room.getTotalQuestionCount())
                .hasPassword(room.getHashedPassword() != null && !room.getHashedPassword().isEmpty())
                .currentState(room.getCurrentState())
                .build();
    }

    /**
     * Convert list of GameRoom entities to RoomListItemResponse DTOs.
     */
    public List<RoomListItemResponse> toRoomListItemResponseList(List<GameRoom> rooms) {
        if (rooms == null) {
            return null;
        }
        return rooms.stream()
                .map(this::toRoomListItemResponse)
                .collect(Collectors.toList());
    }
}
