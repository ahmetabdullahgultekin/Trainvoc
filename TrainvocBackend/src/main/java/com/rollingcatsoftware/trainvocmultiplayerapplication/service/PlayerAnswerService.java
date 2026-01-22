package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Player;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.PlayerAnswer;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Question;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.PlayerAnswerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerAnswerService {
    private final PlayerAnswerRepository answerRepo;

    public PlayerAnswerService(PlayerAnswerRepository answerRepo) {
        this.answerRepo = answerRepo;
    }

    public void saveAnswer(Player player, Question question, int selectedOptionIndex, long answerTimeMs) {
        boolean correct = (question != null) && selectedOptionIndex == question.getCorrectIndex();
        PlayerAnswer answer = new PlayerAnswer();
        answer.setPlayer(player);
        answer.setQuestionId(question != null ? question.getId() : null);
        answer.setSelectedOptionIndex(selectedOptionIndex);
        answer.setCorrect(correct);
        answer.setAnswerTimeMs(answerTimeMs);
        answerRepo.save(answer);
    }
}
