package com.rollingcatsoftware.trainvocmultiplayerapplication.words.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A learning language (mirrors the client's Room {@code languages} table, v18).
 * Ids are application-assigned from the seed manifest (en=1, tr=2) and permanent —
 * there is no {@code @GeneratedValue}.
 */
@Entity
@Table(name = "languages",
        uniqueConstraints = @UniqueConstraint(name = "uk_languages_code", columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;
}
