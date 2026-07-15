package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link WordTranslation}: a directed sense-level edge
 * {@code (wordId, translatedWordId, senseIndex)}. Field names and types mirror the
 * {@code @Id} fields on the entity, as {@code @IdClass} requires. equals/hashCode are
 * null-safe via {@link Objects}.
 */
public class WordTranslationId implements Serializable {

    private Long wordId;
    private Long translatedWordId;
    private Integer senseIndex;

    public WordTranslationId() {
    }

    public WordTranslationId(Long wordId, Long translatedWordId, Integer senseIndex) {
        this.wordId = wordId;
        this.translatedWordId = translatedWordId;
        this.senseIndex = senseIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordTranslationId that = (WordTranslationId) o;
        return Objects.equals(wordId, that.wordId)
                && Objects.equals(translatedWordId, that.translatedWordId)
                && Objects.equals(senseIndex, that.senseIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, translatedWordId, senseIndex);
    }
}
