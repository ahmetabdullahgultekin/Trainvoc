package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link Synonym}: an unordered same-language pair stored once
 * with {@code wordId < synonymWordId} (the manifest guarantees the ordering). Field names
 * and types mirror the entity's {@code @Id} fields, as {@code @IdClass} requires;
 * equals/hashCode are null-safe.
 */
public class SynonymId implements Serializable {

    private Long wordId;
    private Long synonymWordId;

    public SynonymId() {
    }

    public SynonymId(Long wordId, Long synonymWordId) {
        this.wordId = wordId;
        this.synonymWordId = synonymWordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynonymId that = (SynonymId) o;
        return Objects.equals(wordId, that.wordId)
                && Objects.equals(synonymWordId, that.synonymWordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, synonymWordId);
    }
}
