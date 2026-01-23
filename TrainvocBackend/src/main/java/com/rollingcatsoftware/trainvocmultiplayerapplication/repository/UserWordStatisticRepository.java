package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserWordStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordStatisticRepository extends JpaRepository<UserWordStatistic, Long> {

    Optional<UserWordStatistic> findByUserAndWordId(User user, String wordId);

    List<UserWordStatistic> findByUser(User user);

    @Query("SELECT uws FROM UserWordStatistic uws WHERE uws.user.id = :userId AND uws.updatedAt > :since")
    List<UserWordStatistic> findChangedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT SUM(uws.correctCount) FROM UserWordStatistic uws WHERE uws.user = :user")
    Long sumCorrectCount(@Param("user") User user);

    @Query("SELECT SUM(uws.wrongCount) FROM UserWordStatistic uws WHERE uws.user = :user")
    Long sumWrongCount(@Param("user") User user);

    List<UserWordStatistic> findByUserAndUpdatedAtAfter(User user, LocalDateTime since);

    void deleteByUserAndWordId(User user, String wordId);
}
