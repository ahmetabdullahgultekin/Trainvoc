package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public Object getLeaderboard(@RequestParam(required = false) String roomCode) {
        if (roomCode == null || roomCode.isEmpty()) {
            return Collections.singletonMap("error", "Missing or empty parameter: roomCode");
        }
        List<Player> leaderboard = leaderboardService.getLeaderboardByRoom(roomCode);
        if (leaderboard == null || leaderboard.isEmpty()) {
            return Collections.singletonMap("error", "No leaderboard found for the given room code.");
        }
        return leaderboard;
    }
}
