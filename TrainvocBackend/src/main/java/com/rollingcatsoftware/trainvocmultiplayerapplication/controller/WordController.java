package com.rollingcatsoftware.trainvocmultiplayerapplication.controller;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Word;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
public class WordController {

    /** Page size used when {@code size} is omitted on a paginated request. */
    static final int DEFAULT_PAGE_SIZE = 50;
    /** Upper bound on {@code size} so a client can't ask for the whole ~10.5k-row table in one page. */
    static final int MAX_PAGE_SIZE = 500;

    private final WordRepository wordRepository;

    public WordController(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * List words. Backward compatible by design: with NO {@code page}/{@code size} query
     * params this returns the historical full dump (a flat JSON array of every word, or an
     * {@code {"error": ...}} object when empty) so existing clients are unaffected. When
     * either param is present it returns a bounded page — {@code size} defaults to
     * {@value #DEFAULT_PAGE_SIZE} and is clamped to {@value #MAX_PAGE_SIZE}, {@code page}
     * is zero-based and clamped to {@code >= 0}, and rows are stably ordered by {@code id}
     * so page boundaries are deterministic.
     */
    @GetMapping
    public Object getAllWords(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null && size == null) {
            List<Word> words = wordRepository.findAll();
            if (words == null || words.isEmpty()) {
                return java.util.Collections.singletonMap("error", "No words found.");
            }
            return words;
        }

        int pageNumber = page == null ? 0 : Math.max(0, page);
        int pageSize = size == null ? DEFAULT_PAGE_SIZE : Math.min(Math.max(1, size), MAX_PAGE_SIZE);

        Page<Word> result = wordRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id")));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", result.getContent());
        body.put("page", pageNumber);
        body.put("size", pageSize);
        body.put("totalElements", result.getTotalElements());
        body.put("totalPages", result.getTotalPages());
        return body;
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
