package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {

    Optional<UserWordProgress> findByUserAndWord(User user, String word);

    List<UserWordProgress> findByUser(User user);

    List<UserWordProgress> findByUserAndIsLearnedFalse(User user);

    List<UserWordProgress> findByUserAndIsFavoriteTrue(User user);

    @Query("SELECT uwp FROM UserWordProgress uwp WHERE uwp.user = :user AND uwp.nextReviewDate <= :now")
    List<UserWordProgress> findDueForReview(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT uwp FROM UserWordProgress uwp WHERE uwp.user.id = :userId AND uwp.updatedAt > :since")
    List<UserWordProgress> findChangedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(uwp) FROM UserWordProgress uwp WHERE uwp.user = :user AND uwp.isLearned = true")
    long countLearnedWords(@Param("user") User user);

    List<UserWordProgress> findByUserAndUpdatedAtAfter(User user, LocalDateTime since);

    void deleteByUserAndWord(User user, String word);

    long countByUser(User user);

    @Query("SELECT MAX(uwp.updatedAt) FROM UserWordProgress uwp WHERE uwp.user = :user")
    LocalDateTime findLatestUpdatedAtByUser(@Param("user") User user);
}
