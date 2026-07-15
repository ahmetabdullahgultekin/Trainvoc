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
 * A same-language synonym pair (mirrors the client's Room {@code synonyms} table, v18).
 * Stored once per unordered pair with {@code wordId < synonymWordId}. Both columns are
 * logical FKs to {@link Word}; physical PostgreSQL FKs live in the DDL script.
 */
@Entity
@Table(name = "synonyms",
        indexes = {
                @Index(name = "idx_synonyms_word_id", columnList = "word_id"),
                @Index(name = "idx_synonyms_synonym_word_id", columnList = "synonym_word_id")
        })
@IdClass(SynonymId.class)
@Getter
@Setter
public class Synonym {

    @Id
    @Column(name = "word_id")
    private Long wordId;

    @Id
    @Column(name = "synonym_word_id")
    private Long synonymWordId;

    public Synonym() {
    }

    public Synonym(Long wordId, Long synonymWordId) {
        this.wordId = wordId;
        this.synonymWordId = synonymWordId;
    }
}
