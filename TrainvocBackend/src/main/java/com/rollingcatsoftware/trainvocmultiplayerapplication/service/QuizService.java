package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class QuizService {
    private final WordRepository wordRepository;

    public QuizService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public QuizQuestion generateQuestion(String level, int optionCount) {
        List<Word> words = wordRepository.findRandomWordsByLevel(level, optionCount);
        if (words.size() < optionCount) throw new RuntimeException("Yeterli kelime yok!");

        Word questionWord = words.get(new Random().nextInt(words.size()));
        List<String> options = new ArrayList<>();
        for (Word w : words) options.add(w.getMeaning());
        Collections.shuffle(options);

        return new QuizQuestion(questionWord.getWord(), questionWord.getMeaning(), options);
    }

    public List<QuizQuestion> generateQuestions(String level, int optionCount, int totalQuestionCount) {
        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < totalQuestionCount; i++) {
            questions.add(generateQuestion(level, optionCount));
        }
        return questions;
    }
}