package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizService Tests")
class QuizServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private QuizService quizService;

    private List<Word> testWords;
    private static final String TEST_LEVEL = "A1";
    private static final int OPTION_COUNT = 4;

    @BeforeEach
    void setUp() {
        testWords = new ArrayList<>();

        Word word1 = new Word();
        word1.setId(1L);
        word1.setWord("hello");
        word1.setMeaning("merhaba");
        word1.setLevel(TEST_LEVEL);

        Word word2 = new Word();
        word2.setId(2L);
        word2.setWord("goodbye");
        word2.setMeaning("hoşça kal");
        word2.setLevel(TEST_LEVEL);

        Word word3 = new Word();
        word3.setId(3L);
        word3.setWord("thank you");
        word3.setMeaning("teşekkür ederim");
        word3.setLevel(TEST_LEVEL);

        Word word4 = new Word();
        word4.setId(4L);
        word4.setWord("please");
        word4.setMeaning("lütfen");
        word4.setLevel(TEST_LEVEL);

        testWords.add(word1);
        testWords.add(word2);
        testWords.add(word3);
        testWords.add(word4);
    }

    @Nested
    @DisplayName("generateQuestion")
    class GenerateQuestion {

        @Test
        @DisplayName("generates question with correct number of options")
        void generatesQuestionWithCorrectOptionCount() {
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            QuizQuestion question = quizService.generateQuestion(TEST_LEVEL, OPTION_COUNT);

            assertNotNull(question);
            assertEquals(OPTION_COUNT, question.getOptions().size());
            verify(wordRepository).findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT);
        }

        @Test
        @DisplayName("question contains word from word list")
        void questionContainsWordFromList() {
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            QuizQuestion question = quizService.generateQuestion(TEST_LEVEL, OPTION_COUNT);

            List<String> words = testWords.stream().map(Word::getWord).toList();
            assertTrue(words.contains(question.getQuestion()));
        }

        @Test
        @DisplayName("correct answer is in options")
        void correctAnswerIsInOptions() {
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            QuizQuestion question = quizService.generateQuestion(TEST_LEVEL, OPTION_COUNT);

            assertTrue(question.getOptions().contains(question.getCorrectAnswer()));
        }

        @Test
        @DisplayName("options contain all meanings from words")
        void optionsContainAllMeanings() {
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            QuizQuestion question = quizService.generateQuestion(TEST_LEVEL, OPTION_COUNT);

            List<String> meanings = testWords.stream().map(Word::getMeaning).toList();
            for (String option : question.getOptions()) {
                assertTrue(meanings.contains(option));
            }
        }

        @Test
        @DisplayName("throws exception when not enough words available")
        void throwsException_whenNotEnoughWords() {
            List<Word> insufficientWords = List.of(testWords.get(0), testWords.get(1));
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(insufficientWords);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> quizService.generateQuestion(TEST_LEVEL, OPTION_COUNT)
            );

            assertTrue(exception.getMessage().contains("Yeterli kelime yok"));
        }

        @Test
        @DisplayName("works with different levels")
        void worksWithDifferentLevels() {
            String level = "B2";
            when(wordRepository.findRandomWordsByLevel(level, OPTION_COUNT))
                .thenReturn(testWords);

            QuizQuestion question = quizService.generateQuestion(level, OPTION_COUNT);

            assertNotNull(question);
            verify(wordRepository).findRandomWordsByLevel(level, OPTION_COUNT);
        }

        @Test
        @DisplayName("works with different option counts")
        void worksWithDifferentOptionCounts() {
            int optionCount = 3;
            List<Word> threeWords = testWords.subList(0, 3);
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, optionCount))
                .thenReturn(threeWords);

            QuizQuestion question = quizService.generateQuestion(TEST_LEVEL, optionCount);

            assertNotNull(question);
            assertEquals(optionCount, question.getOptions().size());
        }
    }

    @Nested
    @DisplayName("generateQuestions")
    class GenerateQuestions {

        @Test
        @DisplayName("generates correct number of questions")
        void generatesCorrectNumberOfQuestions() {
            int totalQuestions = 5;
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            List<QuizQuestion> questions = quizService.generateQuestions(TEST_LEVEL, OPTION_COUNT, totalQuestions);

            assertEquals(totalQuestions, questions.size());
            verify(wordRepository, times(totalQuestions)).findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT);
        }

        @Test
        @DisplayName("all generated questions are valid")
        void allGeneratedQuestionsAreValid() {
            int totalQuestions = 3;
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords);

            List<QuizQuestion> questions = quizService.generateQuestions(TEST_LEVEL, OPTION_COUNT, totalQuestions);

            for (QuizQuestion question : questions) {
                assertNotNull(question);
                assertNotNull(question.getQuestion());
                assertNotNull(question.getCorrectAnswer());
                assertEquals(OPTION_COUNT, question.getOptions().size());
            }
        }

        @Test
        @DisplayName("generates empty list when total is zero")
        void generatesEmptyList_whenTotalIsZero() {
            List<QuizQuestion> questions = quizService.generateQuestions(TEST_LEVEL, OPTION_COUNT, 0);

            assertTrue(questions.isEmpty());
            verify(wordRepository, never()).findRandomWordsByLevel(any(), anyInt());
        }

        @Test
        @DisplayName("throws exception if any question generation fails")
        void throwsException_ifAnyGenerationFails() {
            when(wordRepository.findRandomWordsByLevel(TEST_LEVEL, OPTION_COUNT))
                .thenReturn(testWords)
                .thenReturn(List.of(testWords.get(0))); // Second call returns insufficient words

            assertThrows(RuntimeException.class, () ->
                quizService.generateQuestions(TEST_LEVEL, OPTION_COUNT, 2)
            );
        }
    }
}
