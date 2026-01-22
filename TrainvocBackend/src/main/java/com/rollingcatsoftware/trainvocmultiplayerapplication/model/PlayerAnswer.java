package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "player_answer")
public class PlayerAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column
    private String questionId;
    @Column
    private int selectedOptionIndex;
    @Column
    private boolean correct;
    @Column
    private long answerTimeMs;
}
