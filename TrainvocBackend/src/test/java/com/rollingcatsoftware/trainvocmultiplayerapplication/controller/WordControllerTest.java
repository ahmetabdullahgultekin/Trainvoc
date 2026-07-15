package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtAuthenticationFilter;
import com.rollingcatsoftware.trainvocmultiplayerapplication.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WordController Tests")
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WordRepository wordRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Word testWord1;
    private Word testWord2;

    @BeforeEach
    void setUp() {
        testWord1 = new Word();
        testWord1.setLemma("apple");
        testWord1.setMeaning("elma");
        testWord1.setLevel("A1");

        testWord2 = new Word();
        testWord2.setLemma("book");
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
                    .andExpect(jsonPath("$[0].lemma").value("apple"))
                    .andExpect(jsonPath("$[0].meaning").value("elma"))
                    .andExpect(jsonPath("$[1].lemma").value("book"));

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
                    .andExpect(jsonPath("$[0].lemma").value("apple"));

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

    @Nested
    @DisplayName("GET /api/words (pagination)")
    class GetAllWordsPaginated {

        private ArgumentCaptor<Pageable> captureFindAll() {
            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            when(wordRepository.findAll(captor.capture()))
                    .thenReturn(new PageImpl<>(List.of(testWord1, testWord2), Pageable.unpaged(), 2));
            return captor;
        }

        @Test
        @WithMockUser
        @DisplayName("no page/size params returns the backward-compatible full dump (array), never paginates")
        void noParams_returnsFullDump() throws Exception {
            when(wordRepository.findAll()).thenReturn(List.of(testWord1, testWord2));

            mockMvc.perform(get("/api/words"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].lemma").value("apple"))
                    .andExpect(jsonPath("$[1].lemma").value("book"));

            verify(wordRepository).findAll();
            verify(wordRepository, never()).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser
        @DisplayName("returns the paged envelope (content/page/size/totalElements/totalPages) when size is given")
        void withSize_returnsPagedEnvelope() throws Exception {
            when(wordRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(testWord1, testWord2), PageRequest.of(0, 2), 5));

            mockMvc.perform(get("/api/words").param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].lemma").value("apple"))
                    .andExpect(jsonPath("$.content[1].lemma").value("book"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(3));

            verify(wordRepository, never()).findAll();
        }

        @Test
        @WithMockUser
        @DisplayName("defaults to size 50, page 0, sorted by id asc when only page is given")
        void onlyPage_appliesDefaults() throws Exception {
            ArgumentCaptor<Pageable> captor = captureFindAll();

            mockMvc.perform(get("/api/words").param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(50));

            Pageable used = captor.getValue();
            assertThat(used.getPageNumber()).isZero();
            assertThat(used.getPageSize()).isEqualTo(50);
            assertThat(used.getSort()).isEqualTo(Sort.by(Sort.Direction.ASC, "id"));
        }

        @Test
        @WithMockUser
        @DisplayName("clamps size above the max down to 500")
        void size_clampedToMax() throws Exception {
            ArgumentCaptor<Pageable> captor = captureFindAll();

            mockMvc.perform(get("/api/words").param("size", "501"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(500));

            assertThat(captor.getValue().getPageSize()).isEqualTo(500);
        }

        @Test
        @WithMockUser
        @DisplayName("floors size below 1 up to 1")
        void size_flooredToOne() throws Exception {
            ArgumentCaptor<Pageable> captor = captureFindAll();

            mockMvc.perform(get("/api/words").param("size", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(1));

            assertThat(captor.getValue().getPageSize()).isEqualTo(1);
        }

        @Test
        @WithMockUser
        @DisplayName("floors a negative page up to 0")
        void page_flooredToZero() throws Exception {
            ArgumentCaptor<Pageable> captor = captureFindAll();

            mockMvc.perform(get("/api/words").param("page", "-3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0));

            assertThat(captor.getValue().getPageNumber()).isZero();
        }
    }
}
