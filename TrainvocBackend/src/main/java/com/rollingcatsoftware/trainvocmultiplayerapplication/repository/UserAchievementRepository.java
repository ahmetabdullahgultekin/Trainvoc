package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    Optional<UserAchievement> findByUserAndAchievementId(User user, String achievementId);

    List<UserAchievement> findByUser(User user);

    List<UserAchievement> findByUserAndIsUnlockedTrue(User user);

    List<UserAchievement> findByUserAndIsUnlockedFalse(User user);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.updatedAt > :since")
    List<UserAchievement> findChangedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user = :user AND ua.isUnlocked = true")
    long countUnlockedAchievements(@Param("user") User user);

    List<UserAchievement> findByUserAndUpdatedAtAfter(User user, LocalDateTime since);

    void deleteByUserAndAchievementId(User user, String achievementId);
}
