package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuizQuestion {
    private String english;
    private String correctMeaning;
    private List<String> options;

    public QuizQuestion(String english, String correctMeaning, List<String> options) {
        this.english = english;
        this.correctMeaning = correctMeaning;
        this.options = options;
    }

}