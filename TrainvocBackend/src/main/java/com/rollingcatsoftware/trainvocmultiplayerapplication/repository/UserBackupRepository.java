package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.User;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.UserBackup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBackupRepository extends JpaRepository<UserBackup, Long> {

    Optional<UserBackup> findByBackupId(String backupId);

    Optional<UserBackup> findByUserAndBackupId(User user, String backupId);

    List<UserBackup> findByUser(User user);

    List<UserBackup> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<UserBackup> findByUserAndStatus(User user, String status);

    @Query("SELECT ub FROM UserBackup ub WHERE ub.user = :user AND ub.status = 'COMPLETED' ORDER BY ub.createdAt DESC")
    List<UserBackup> findLatestCompletedBackups(@Param("user") User user, Pageable pageable);

    @Query("SELECT SUM(ub.sizeBytes) FROM UserBackup ub WHERE ub.user = :user AND ub.status = 'COMPLETED'")
    Long totalBackupSize(@Param("user") User user);

    void deleteByBackupId(String backupId);
}
