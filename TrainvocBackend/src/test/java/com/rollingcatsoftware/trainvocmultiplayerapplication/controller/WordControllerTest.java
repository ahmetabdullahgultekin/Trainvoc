package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WordController Tests")
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordRepository wordRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Word testWord1;
    private Word testWord2;

    @BeforeEach
    void setUp() {
        testWord1 = new Word();
        testWord1.setWord("apple");
        testWord1.setMeaning("elma");
        testWord1.setLevel("A1");

        testWord2 = new Word();
        testWord2.setWord("book");
        testWord2.setMeaning("kitap");
        testWord2.setLevel("A1");
    }

    @Nested
    @DisplayName("GET /api/words")
    class GetAllWords {

        @Test
        @WithMockUser
        @DisplayName("returns all words")
        void returnsAllWords() throws Exception {
            when(wordRepository.findAll()).thenReturn(List.of(testWord1, testWord2));

            mockMvc.perform(get("/api/words"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].word").value("apple"))
                    .andExpect(jsonPath("$[0].meaning").value("elma"))
                    .andExpect(jsonPath("$[1].word").value("book"));

            verify(wordRepository).findAll();
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when no words found")
        void returnsError_whenNoWordsFound() throws Exception {
            when(wordRepository.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/words"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No words found."));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when words is null")
        void returnsError_whenWordsNull() throws Exception {
            when(wordRepository.findAll()).thenReturn(null);

            mockMvc.perform(get("/api/words"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No words found."));
        }
    }

    @Nested
    @DisplayName("GET /api/words/by-level")
    class GetWordsByLevel {

        @Test
        @WithMockUser
        @DisplayName("returns words by level")
        void returnsWordsByLevel() throws Exception {
            when(wordRepository.findByLevel("A1")).thenReturn(List.of(testWord1, testWord2));

            mockMvc.perform(get("/api/words/by-level")
                            .param("level", "A1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].level").value("A1"))
                    .andExpect(jsonPath("$[1].level").value("A1"));

            verify(wordRepository).findByLevel("A1");
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when level is missing")
        void returnsError_whenLevelMissing() throws Exception {
            mockMvc.perform(get("/api/words/by-level"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: level"));

            verify(wordRepository, never()).findByLevel(anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when level is empty")
        void returnsError_whenLevelEmpty() throws Exception {
            mockMvc.perform(get("/api/words/by-level")
                            .param("level", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: level"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when no words found for level")
        void returnsError_whenNoWordsFoundForLevel() throws Exception {
            when(wordRepository.findByLevel("C2")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/words/by-level")
                            .param("level", "C2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No words found for the given level."));
        }
    }

    @Nested
    @DisplayName("GET /api/words/by-exam")
    class GetWordsByExam {

        @Test
        @WithMockUser
        @DisplayName("returns words by exam")
        void returnsWordsByExam() throws Exception {
            when(wordRepository.findByExam("YDS")).thenReturn(List.of(testWord1));

            mockMvc.perform(get("/api/words/by-exam")
                            .param("exam", "YDS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].word").value("apple"));

            verify(wordRepository).findByExam("YDS");
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when exam is missing")
        void returnsError_whenExamMissing() throws Exception {
            mockMvc.perform(get("/api/words/by-exam"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: exam"));

            verify(wordRepository, never()).findByExam(anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when exam is empty")
        void returnsError_whenExamEmpty() throws Exception {
            mockMvc.perform(get("/api/words/by-exam")
                            .param("exam", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("Missing or empty parameter: exam"));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when no words found for exam")
        void returnsError_whenNoWordsFoundForExam() throws Exception {
            when(wordRepository.findByExam("UNKNOWN")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/words/by-exam")
                            .param("exam", "UNKNOWN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No words found for the given exam."));
        }

        @Test
        @WithMockUser
        @DisplayName("returns error when words is null for exam")
        void returnsError_whenWordsNullForExam() throws Exception {
            when(wordRepository.findByExam("NULL")).thenReturn(null);

            mockMvc.perform(get("/api/words/by-exam")
                            .param("exam", "NULL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.error").value("No words found for the given exam."));
        }
    }
}
