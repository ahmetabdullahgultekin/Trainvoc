package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.GameRoom;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.QuizQuestion;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.GameService;
import com.rollingcatsoftware.trainvocmultiplayerapplication.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("QuizController Tests")
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @MockBean
    private GameService gameService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private QuizQuestion testQuestion;
    private GameRoom testRoom;

    @BeforeEach
    void setUp() {
        testQuestion = new QuizQuestion("apple", "elma", List.of("elma", "armut", "kiraz", "muz"));

        testRoom = new GameRoom();
        testRoom.setRoomCode("ABC12");
        testRoom.setLevel("A1");
        testRoom.setOptionCount(4);
        testRoom.setTotalQuestionCount(10);
    }

    @Nested
    @DisplayName("GET /api/quiz/question")
    class GetQuestion {

        @Test
        @WithMockUser
        @DisplayName("returns question when parameters are valid")
        void returnsQuestion_whenParametersValid() throws Exception {
            when(quizService.generateQuestion("A1", 4)).thenReturn(testQuestion);

            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "A1")
                            .param("optionCount", "4"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.english").value("apple"))
                    .andExpect(jsonPath("$.correctMeaning").value("elma"));

            verify(quizService).generateQuestion("A1", 4);
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when level is missing")
        void returns400_whenLevelMissing() throws Exception {
            mockMvc.perform(get("/api/quiz/question")
                            .param("optionCount", "4"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: level"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when level is empty")
        void returns400_whenLevelEmpty() throws Exception {
            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "")
                            .param("optionCount", "4"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: level"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when optionCount is missing")
        void returns400_whenOptionCountMissing() throws Exception {
            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "A1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Missing or invalid parameter: optionCount"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when optionCount is invalid")
        void returns400_whenOptionCountInvalid() throws Exception {
            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "A1")
                            .param("optionCount", "0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Missing or invalid parameter: optionCount"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when no question found")
        void returns404_whenNoQuestionFound() throws Exception {
            when(quizService.generateQuestion("C2", 4)).thenReturn(null);

            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "C2")
                            .param("optionCount", "4"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("No question found for the given parameters."));
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when service throws exception")
        void returns400_whenServiceThrowsException() throws Exception {
            when(quizService.generateQuestion(anyString(), anyInt()))
                    .thenThrow(new RuntimeException("Not enough words"));

            mockMvc.perform(get("/api/quiz/question")
                            .param("level", "A1")
                            .param("optionCount", "4"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Not enough words"));
        }
    }

    @Nested
    @DisplayName("GET /api/quiz/all-questions")
    class GetAllQuestions {

        @Test
        @WithMockUser
        @DisplayName("returns questions when room is valid")
        void returnsQuestions_whenRoomValid() throws Exception {
            when(gameService.getRoom("ABC12")).thenReturn(testRoom);
            when(quizService.generateQuestions("A1", 4, 10))
                    .thenReturn(List.of(testQuestion));

            mockMvc.perform(get("/api/quiz/all-questions")
                            .param("roomCode", "ABC12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].english").value("apple"));

            verify(gameService).getRoom("ABC12");
            verify(quizService).generateQuestions("A1", 4, 10);
        }

        @Test
        @WithMockUser
        @DisplayName("returns 404 when room not found")
        void returns404_whenRoomNotFound() throws Exception {
            when(gameService.getRoom("INVALID")).thenReturn(null);

            mockMvc.perform(get("/api/quiz/all-questions")
                            .param("roomCode", "INVALID"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when room settings are incomplete")
        void returns400_whenRoomSettingsIncomplete() throws Exception {
            GameRoom incompleteRoom = new GameRoom();
            incompleteRoom.setRoomCode("ABC12");
            incompleteRoom.setLevel(null);

            when(gameService.getRoom("ABC12")).thenReturn(incompleteRoom);

            mockMvc.perform(get("/api/quiz/all-questions")
                            .param("roomCode", "ABC12"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("returns 400 when service throws exception")
        void returns400_whenServiceThrowsException() throws Exception {
            when(gameService.getRoom("ABC12")).thenReturn(testRoom);
            when(quizService.generateQuestions(anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("Question generation failed"));

            mockMvc.perform(get("/api/quiz/all-questions")
                            .param("roomCode", "ABC12"))
                    .andExpect(status().isBadRequest());
        }
    }
}
