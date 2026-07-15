package com.rollingcatsoftware.trainvocmultiplayerapplication.repository.word;

import com.rollingcatsoftware.trainvocmultiplayerapplication.words.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, String> {
}
