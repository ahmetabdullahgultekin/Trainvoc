package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "words")
public class Word {
    // getter ve setter'lar
    @Id
    @Column(name = "word")
    private String word;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "level")
    private String level;

    @Column(name = "last_reviewed")
    private Long lastReviewed;

    @Column(name = "stat_id")
    private Integer statId;

    @Column(name = "seconds_spent")
    private Integer secondsSpent;

    // Getters, setters, constructors
    public Word() {
    }

}