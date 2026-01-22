package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "game_room")
public class GameRoom {
    @Id
    @Column(length = 32)
    private String roomCode;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Player> players = new ArrayList<>();

    @Column
    private int currentQuestionIndex;

    @Column
    private Boolean started = false;

    @Column
    private String hostId;

    @Column
    private int questionDuration = 60;
    @Column
    private int optionCount = 4;
    @Column
    private String level = "A1";
    @Column
    private int totalQuestionCount = 5;
    @Column
    private LocalDateTime lastUsed;

    @Column(length = 128)
    private String hashedPassword;

    @Enumerated(EnumType.ORDINAL)
    @Column
    private GameState currentState = GameState.LOBBY; // Oyun state'i: lobby, countdown, question, ranking, vb.

    @Column
    private LocalDateTime stateStartTime; // O anki state'in başlangıç zamanı
}

