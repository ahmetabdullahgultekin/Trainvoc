package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {
    private final WordRepository wordRepository;

    public WordController(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @GetMapping
    public Object getAllWords() {
        List<Word> words = wordRepository.findAll();
        if (words == null || words.isEmpty()) {
            return java.util.Collections.singletonMap("error", "No words found.");
        }
        return words;
    }

    @GetMapping("/by-level")
    public Object getWordsByLevel(@RequestParam(required = false) String level) {
        if (level == null || level.isEmpty()) {
            return java.util.Collections.singletonMap("error", "Missing or empty parameter: level");
        }
        List<Word> words = wordRepository.findByLevel(level);
        if (words == null || words.isEmpty()) {
            return java.util.Collections.singletonMap("error", "No words found for the given level.");
        }
        return words;
    }

    @GetMapping("/by-exam")
    public Object getWordsByExam(@RequestParam(required = false) String exam) {
        if (exam == null || exam.isEmpty()) {
            return java.util.Collections.singletonMap("error", "Missing or empty parameter: exam");
        }
        List<Word> words = wordRepository.findByExam(exam);
        if (words == null || words.isEmpty()) {
            return java.util.Collections.singletonMap("error", "No words found for the given exam.");
        }
        return words;
    }
}
