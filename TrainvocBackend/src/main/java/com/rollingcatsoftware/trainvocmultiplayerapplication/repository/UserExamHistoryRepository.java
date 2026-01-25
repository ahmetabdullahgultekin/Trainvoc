package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserExamHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamHistoryRepository extends JpaRepository<UserExamHistory, Long> {

    Optional<UserExamHistory> findByUserAndExamId(User user, String examId);

    List<UserExamHistory> findByUser(User user);

    List<UserExamHistory> findByUserOrderByCompletedAtDesc(User user, Pageable pageable);

    @Query("SELECT ueh FROM UserExamHistory ueh WHERE ueh.user.id = :userId AND ueh.createdAt > :since")
    List<UserExamHistory> findChangedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(ueh) FROM UserExamHistory ueh WHERE ueh.user = :user")
    long countExamsByUser(@Param("user") User user);

    @Query("SELECT AVG(ueh.score) FROM UserExamHistory ueh WHERE ueh.user = :user")
    Double averageScore(@Param("user") User user);

    @Query("SELECT MAX(ueh.score) FROM UserExamHistory ueh WHERE ueh.user = :user")
    Integer maxScore(@Param("user") User user);

    List<UserExamHistory> findByUserAndUpdatedAtAfter(User user, LocalDateTime since);

    long countByUser(User user);

    @Query("SELECT MAX(ueh.updatedAt) FROM UserExamHistory ueh WHERE ueh.user = :user")
    LocalDateTime findLatestUpdatedAtByUser(@Param("user") User user);
}
