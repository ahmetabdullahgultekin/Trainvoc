package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * A directed, sense-grouped translation edge (mirrors the client's Room
 * {@code word_translations} table, v18). In the seed manifest these are English→Turkish
 * only, keyed by {@code (wordId, translatedWordId, senseIndex)} so that a single lemma
 * with several senses carries several rows to the same or different target words.
 * Both endpoints are plain columns (logical FKs to {@link Word}); the physical
 * PostgreSQL FKs live in the DDL script.
 */
@Entity
@Table(name = "word_translations",
        indexes = {
                @Index(name = "idx_word_translations_word_id", columnList = "word_id"),
                @Index(name = "idx_word_translations_translated_word_id", columnList = "translated_word_id")
        })
@IdClass(WordTranslationId.class)
@Getter
@Setter
public class WordTranslation {

    @Id
    @Column(name = "word_id")
    private Long wordId;

    @Id
    @Column(name = "translated_word_id")
    private Long translatedWordId;

    @Id
    @Column(name = "sense_index")
    private Integer senseIndex;

    @Column(name = "note")
    private String note;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    public WordTranslation() {
    }

    public WordTranslation(Long wordId, Long translatedWordId, Integer senseIndex, String note, boolean isPrimary) {
        this.wordId = wordId;
        this.translatedWordId = translatedWordId;
        this.senseIndex = senseIndex;
        this.note = note;
        this.isPrimary = isPrimary;
    }
}
