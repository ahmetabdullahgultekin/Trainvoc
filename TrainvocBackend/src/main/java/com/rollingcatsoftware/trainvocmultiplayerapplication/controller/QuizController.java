package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;
    private final GameService gameService;

    public QuizController(QuizService quizService, GameService gameService) {
        this.quizService = quizService;
        this.gameService = gameService;
    }

    @GetMapping("/question")
    public ResponseEntity<?> getQuestion(@RequestParam(required = false) String level, @RequestParam(required = false) Integer optionCount) {
        if (level == null || level.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Missing or empty parameter: level"));
        }
        if (optionCount == null || optionCount <= 0) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Missing or invalid parameter: optionCount"));
        }
        try {
            QuizQuestion question = quizService.generateQuestion(level, optionCount);
            if (question == null) {
                return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "No question found for the given parameters."));
            }
            return ResponseEntity.ok(question);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred while generating the question."));
        }
    }

    @GetMapping("/all-questions")
    public ResponseEntity<?> getGameQuestions(@RequestParam String roomCode) {
        var room = gameService.getRoom(roomCode);
        if (room == null) {
            return ResponseEntity.status(404).body(java.util.Collections.singletonMap("error", "Oda bulunamadı."));
        }
        String level = room.getLevel();
        int optionCount = room.getOptionCount();
        int totalQuestionCount = room.getTotalQuestionCount();
        if (level == null || level.isEmpty() || optionCount <= 0 || totalQuestionCount <= 0) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", "Oda ayarları eksik."));
        }
        try {
            List<QuizQuestion> questions = quizService.generateQuestions(level, optionCount, totalQuestionCount);
            return ResponseEntity.ok(questions);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", ex.getMessage() != null ? ex.getMessage() : "Soru oluşturulurken beklenmeyen bir hata oluştu."));
        }
    }
}