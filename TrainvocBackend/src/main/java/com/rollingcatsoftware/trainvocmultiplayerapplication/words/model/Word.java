package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * A vocabulary word in a single language (mirrors the client's Room {@code words}
 * table, v18). Every language is a first-class set of rows — an English lemma and a
 * Turkish lemma are two separate {@code Word}s linked through {@link WordTranslation}.
 * <p>
 * Ids are application-assigned from the seed manifest and are permanent: the importer
 * inserts them verbatim (never renumbered, id holes are legal). {@code languageId} is a
 * plain column (logical FK to {@link Language}) — the entity is deliberately
 * {@code @ManyToOne}-free so JSON serialization of {@code /api/words} stays flat.
 * {@code meaning} is a denormalized display cache carried over from the client
 * (kept NOT NULL to mirror Room; the relational source of truth is {@link WordTranslation}).
 */
@Entity
@Table(name = "words",
        uniqueConstraints = @UniqueConstraint(name = "uk_words_lemma_language", columnNames = {"lemma", "language_id"}),
        indexes = {
                @Index(name = "idx_words_language_id", columnList = "language_id"),
                @Index(name = "idx_words_level", columnList = "level")
        })
@Getter
@Setter
public class Word {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "level")
    private String level;

    @Column(name = "note")
    private String note;

    public Word() {
    }
}
