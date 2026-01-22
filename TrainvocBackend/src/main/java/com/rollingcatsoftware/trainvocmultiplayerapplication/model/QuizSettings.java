package com.rollingcatsoftware.trainvocmultiplayerapplication.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QuizSettings {
    @Min(value = 10, message = "Question duration must be at least 10 seconds")
    @Max(value = 300, message = "Question duration cannot exceed 300 seconds")
    private int questionDuration = 60;

    @Min(value = 2, message = "Option count must be at least 2")
    @Max(value = 4, message = "Option count cannot exceed 4")
    private int optionCount = 4;

    @Pattern(regexp = "^(A1|A2|B1|B2|C1|C2|Mixed)$", message = "Level must be A1, A2, B1, B2, C1, C2, or Mixed")
    private String level = "A1";

    @Min(value = 1, message = "Total question count must be at least 1")
    @Max(value = 50, message = "Total question count cannot exceed 50")
    private int totalQuestionCount = 5;
}
