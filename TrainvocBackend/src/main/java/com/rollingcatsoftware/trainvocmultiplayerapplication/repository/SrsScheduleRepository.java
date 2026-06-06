package com.rollingcatsoftware.trainvocmultiplayerapplication.repository;

import com.rollingcatsoftware.trainvocmultiplayerapplication.model.SrsSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Data access for {@link SrsSchedule} rows (primary {@code trainvoc} database).
 *
 * <p>Backs the SRS engine's S4 cross-device sync slice. Queries are user-scoped;
 * callers must always pass the authenticated user's id.</p>
 */
@Repository
public interface SrsScheduleRepository extends JpaRepository<SrsSchedule, SrsSchedule.SrsScheduleId> {

    Optional<SrsSchedule> findByUserIdAndWordId(String userId, String wordId);

    List<SrsSchedule> findByUserId(String userId);

    /** Full schedule for a user, ordered by soonest-due first (for the pull endpoint). */
    List<SrsSchedule> findByUserIdOrderByDueAtAsc(String userId);

    /** Count of cards due at or before {@code now} for a user. */
    @Query("SELECT COUNT(s) FROM SrsSchedule s WHERE s.userId = :userId AND s.dueAt <= :now")
    long countDue(@Param("userId") String userId, @Param("now") Instant now);

    /** Earliest upcoming due instant for a user, or {@code null} if none. */
    @Query("SELECT MIN(s.dueAt) FROM SrsSchedule s WHERE s.userId = :userId")
    Instant findNextDueAt(@Param("userId") String userId);
}
