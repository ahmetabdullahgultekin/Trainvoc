package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserFeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFeatureFlagRepository extends JpaRepository<UserFeatureFlag, Long> {

    Optional<UserFeatureFlag> findByUserAndFlagName(User user, String flagName);

    List<UserFeatureFlag> findByUser(User user);

    List<UserFeatureFlag> findByUserAndIsEnabledTrue(User user);

    @Query("SELECT uff FROM UserFeatureFlag uff WHERE uff.user.id = :userId AND uff.updatedAt > :since")
    List<UserFeatureFlag> findChangedSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    void deleteByUserAndFlagName(User user, String flagName);
}
