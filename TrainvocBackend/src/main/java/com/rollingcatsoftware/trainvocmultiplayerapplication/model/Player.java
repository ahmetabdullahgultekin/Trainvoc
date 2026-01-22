package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "player")
public class Player {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "room_code")
    @JsonBackReference
    private GameRoom room;

    @Column
    private String name;
    @Column
    private int score = 0;
    @Column
    private int correctCount = 0;
    @Column
    private int wrongCount = 0;
    @Column
    private long totalAnswerTime = 0;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerAnswer> answers = new ArrayList<>();

    @Column
    private Integer avatarId; // int olarak değiştirildi, index olarak tutulacak

    @Column
    private Integer currentAnsweredQuestionIndex = null; // Oyuncunun en son hangi soruya cevap verdiği

    public Player() {
    }
}
