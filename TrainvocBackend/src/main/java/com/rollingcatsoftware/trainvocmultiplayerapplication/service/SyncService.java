package com.rollingcatsoftware.trainvocmultiplayerapplication.service;

import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.BatchSyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncRequest;
import com.rollingcatsoftware.trainvocmultiplayerapplication.dto.sync.SyncResponse;
import com.rollingcatsoftware.trainvocmultiplayerapplication.model.*;
import com.rollingcatsoftware.trainvocmultiplayerapplication.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling sync operations between client and server.
 * Supports sync for: words, statistics, exams, achievements, user profile, backup, feature flags.
 */
@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final UserWordProgressRepository wordProgressRepository;
    private final UserWordStatisticRepository wordStatisticRepository;
    private final UserExamHistoryRepository examHistoryRepository;
    private final UserAchievementRepository achievementRepository;
    private final UserBackupRepository backupRepository;
    private final UserFeatureFlagRepository featureFlagRepository;
    private final UserRepository userRepository;

    public SyncService(
            UserWordProgressRepository wordProgressRepository,
            UserWordStatisticRepository wordStatisticRepository,
            UserExamHistoryRepository examHistoryRepository,
            UserAchievementRepository achievementRepository,
            UserBackupRepository backupRepository,
            UserFeatureFlagRepository featureFlagRepository,
            UserRepository userRepository) {
        this.wordProgressRepository = wordProgressRepository;
        this.wordStatisticRepository = wordStatisticRepository;
        this.examHistoryRepository = examHistoryRepository;
        this.achievementRepository = achievementRepository;
        this.backupRepository = backupRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.userRepository = userRepository;
    }

    /**
     * Process a batch of sync requests.
     *
     * @param request Batch sync request
     * @param user Current authenticated user
     * @return BatchSyncResponse with results for each item
     */
    @Transactional
    public BatchSyncResponse processBatchSync(BatchSyncRequest request, User user) {
        List<SyncResponse> results = new ArrayList<>();

        for (SyncRequest item : request.items()) {
            try {
                SyncResponse response = processSingleSync(item, user);
                results.add(response);
            } catch (Exception e) {
                log.error("Error processing sync for entity {}/{}: {}",
                    item.entityType(), item.entityId(), e.getMessage());
                results.add(SyncResponse.failure(
                    item.entityType(),
                    item.entityId(),
                    "Sync failed: " + e.getMessage()
                ));
            }
        }

        return BatchSyncResponse.fromResults(results);
    }

    /**
     * Process a single sync request.
     *
     * @param request Sync request
     * @param user Current authenticated user
     * @return SyncResponse
     */
    @Transactional
    public SyncResponse processSingleSync(SyncRequest request, User user) {
        return switch (request.entityType().toLowerCase()) {
            case "word" -> syncWord(request, user);
            case "statistic" -> syncStatistic(request, user);
            case "exam" -> syncExam(request, user);
            case "achievement" -> syncAchievement(request, user);
            case "userprofile" -> syncUserProfile(request, user);
            case "backup" -> syncBackup(request, user);
            case "featureflag" -> syncFeatureFlag(request, user);
            default -> SyncResponse.failure(
                request.entityType(),
                request.entityId(),
                "Unknown entity type: " + request.entityType()
            );
        };
    }

    /**
     * Sync word learning progress.
     */
    private SyncResponse syncWord(SyncRequest request, User user) {
        log.info("Syncing word for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String word = (String) data.get("word");
        if (word == null) {
            word = request.entityId();
        }

        // Find existing or create new
        UserWordProgress progress = wordProgressRepository
                .findByUserAndWord(user, word)
                .orElseGet(() -> {
                    UserWordProgress newProgress = new UserWordProgress();
                    newProgress.setUser(user);
                    newProgress.setWord(word);
                    return newProgress;
                });

        // Update fields from sync data
        if (data.containsKey("lastReviewed")) {
            progress.setLastReviewed(toLocalDateTime(data.get("lastReviewed")));
        }
        if (data.containsKey("nextReviewDate")) {
            progress.setNextReviewDate(toLocalDateTime(data.get("nextReviewDate")));
        }
        if (data.containsKey("easinessFactor")) {
            progress.setEasinessFactor(((Number) data.get("easinessFactor")).floatValue());
        }
        if (data.containsKey("intervalDays")) {
            progress.setIntervalDays(((Number) data.get("intervalDays")).intValue());
        }
        if (data.containsKey("repetitions")) {
            progress.setRepetitions(((Number) data.get("repetitions")).intValue());
        }
        if (data.containsKey("isFavorite")) {
            progress.setFavorite((Boolean) data.get("isFavorite"));
        }
        if (data.containsKey("secondsSpent")) {
            progress.setSecondsSpent(((Number) data.get("secondsSpent")).intValue());
        }
        if (data.containsKey("isLearned")) {
            progress.setLearned((Boolean) data.get("isLearned"));
        }

        wordProgressRepository.save(progress);
        log.debug("Word progress saved for user {} word {}", user.getId(), word);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync word statistics (correct/wrong counts).
     */
    private SyncResponse syncStatistic(SyncRequest request, User user) {
        log.info("Syncing statistic for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String wordId = (String) data.getOrDefault("wordId", request.entityId());

        // Find existing or create new
        UserWordStatistic statistic = wordStatisticRepository
                .findByUserAndWordId(user, wordId)
                .orElseGet(() -> {
                    UserWordStatistic newStat = new UserWordStatistic();
                    newStat.setUser(user);
                    newStat.setWordId(wordId);
                    return newStat;
                });

        // Update fields from sync data
        if (data.containsKey("correctCount")) {
            statistic.setCorrectCount(((Number) data.get("correctCount")).intValue());
        }
        if (data.containsKey("wrongCount")) {
            statistic.setWrongCount(((Number) data.get("wrongCount")).intValue());
        }
        if (data.containsKey("skippedCount")) {
            statistic.setSkippedCount(((Number) data.get("skippedCount")).intValue());
        }
        if (data.containsKey("lastAnsweredAt")) {
            statistic.setLastAnsweredAt(toLocalDateTime(data.get("lastAnsweredAt")));
        }

        wordStatisticRepository.save(statistic);
        log.debug("Word statistic saved for user {} wordId {}", user.getId(), wordId);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync exam progress.
     */
    private SyncResponse syncExam(SyncRequest request, User user) {
        log.info("Syncing exam for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String examId = (String) data.getOrDefault("examId", request.entityId());

        // Find existing or create new
        UserExamHistory examHistory = examHistoryRepository
                .findByUserAndExamId(user, examId)
                .orElseGet(() -> {
                    UserExamHistory newExam = new UserExamHistory();
                    newExam.setUser(user);
                    newExam.setExamId(examId);
                    return newExam;
                });

        // Update fields from sync data
        if (data.containsKey("completedAt")) {
            examHistory.setCompletedAt(toLocalDateTime(data.get("completedAt")));
        }
        if (data.containsKey("score")) {
            examHistory.setScore(((Number) data.get("score")).intValue());
        }
        if (data.containsKey("totalQuestions")) {
            examHistory.setTotalQuestions(((Number) data.get("totalQuestions")).intValue());
        }
        if (data.containsKey("correctAnswers")) {
            examHistory.setCorrectAnswers(((Number) data.get("correctAnswers")).intValue());
        }
        if (data.containsKey("durationSeconds")) {
            examHistory.setDurationSeconds(((Number) data.get("durationSeconds")).intValue());
        }
        if (data.containsKey("examType")) {
            examHistory.setExamType((String) data.get("examType"));
        }

        examHistoryRepository.save(examHistory);
        log.debug("Exam history saved for user {} examId {}", user.getId(), examId);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync achievement progress.
     */
    private SyncResponse syncAchievement(SyncRequest request, User user) {
        log.info("Syncing achievement for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String achievementId = (String) data.getOrDefault("achievementId", request.entityId());

        // Find existing or create new
        UserAchievement achievement = achievementRepository
                .findByUserAndAchievementId(user, achievementId)
                .orElseGet(() -> {
                    UserAchievement newAchievement = new UserAchievement();
                    newAchievement.setUser(user);
                    newAchievement.setAchievementId(achievementId);
                    return newAchievement;
                });

        // Update fields from sync data
        if (data.containsKey("progress")) {
            achievement.setProgress(((Number) data.get("progress")).intValue());
        }
        if (data.containsKey("targetProgress")) {
            achievement.setTargetProgress(((Number) data.get("targetProgress")).intValue());
        }
        if (data.containsKey("isUnlocked")) {
            achievement.setUnlocked((Boolean) data.get("isUnlocked"));
        }
        if (data.containsKey("unlockedAt")) {
            achievement.setUnlockedAt(toLocalDateTime(data.get("unlockedAt")));
        }

        achievementRepository.save(achievement);
        log.debug("Achievement saved for user {} achievementId {}", user.getId(), achievementId);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync user profile data.
     */
    private SyncResponse syncUserProfile(SyncRequest request, User user) {
        log.info("Syncing user profile for user {}", user.getId());

        Map<String, Object> data = request.data();

        // Update user entity with profile data
        if (data.containsKey("displayName")) {
            user.setDisplayName((String) data.get("displayName"));
        }
        if (data.containsKey("totalScore")) {
            user.setTotalScore(((Number) data.get("totalScore")).intValue());
        }
        if (data.containsKey("totalGamesPlayed")) {
            user.setTotalGamesPlayed(((Number) data.get("totalGamesPlayed")).intValue());
        }
        if (data.containsKey("gamesWon")) {
            user.setGamesWon(((Number) data.get("gamesWon")).intValue());
        }

        userRepository.save(user);
        log.debug("User profile saved for user {}", user.getId());

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync backup data.
     */
    private SyncResponse syncBackup(SyncRequest request, User user) {
        log.info("Syncing backup for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String backupId = (String) data.getOrDefault("backupId", request.entityId());

        // Find existing or create new
        UserBackup backup = backupRepository
                .findByUserAndBackupId(user, backupId)
                .orElseGet(() -> {
                    UserBackup newBackup = new UserBackup();
                    newBackup.setUser(user);
                    newBackup.setBackupId(backupId);
                    return newBackup;
                });

        // Update fields from sync data
        if (data.containsKey("storagePath")) {
            backup.setStoragePath((String) data.get("storagePath"));
        }
        if (data.containsKey("size")) {
            backup.setSizeBytes(((Number) data.get("size")).longValue());
        }
        if (data.containsKey("checksum")) {
            backup.setChecksum((String) data.get("checksum"));
        }
        if (data.containsKey("status")) {
            backup.setStatus((String) data.get("status"));
        } else {
            backup.setStatus("COMPLETED");
        }

        backupRepository.save(backup);
        log.debug("Backup metadata saved for user {} backupId {}", user.getId(), backupId);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Sync feature flag overrides.
     */
    private SyncResponse syncFeatureFlag(SyncRequest request, User user) {
        log.info("Syncing feature flag for user {}: {}", user.getId(), request.entityId());

        Map<String, Object> data = request.data();
        String flagName = (String) data.getOrDefault("flagName", request.entityId());

        // Find existing or create new
        UserFeatureFlag featureFlag = featureFlagRepository
                .findByUserAndFlagName(user, flagName)
                .orElseGet(() -> {
                    UserFeatureFlag newFlag = new UserFeatureFlag();
                    newFlag.setUser(user);
                    newFlag.setFlagName(flagName);
                    return newFlag;
                });

        // Update fields from sync data
        if (data.containsKey("isEnabled")) {
            featureFlag.setEnabled((Boolean) data.get("isEnabled"));
        }
        if (data.containsKey("variant")) {
            featureFlag.setVariant((String) data.get("variant"));
        }

        featureFlagRepository.save(featureFlag);
        log.debug("Feature flag saved for user {} flagName {}", user.getId(), flagName);

        return SyncResponse.success(request.entityType(), request.entityId());
    }

    /**
     * Get server changes since a timestamp for a user.
     *
     * @param userId User ID
     * @param since Timestamp to fetch changes from
     * @return List of changed entities
     */
    public List<Map<String, Object>> getServerChanges(Long userId, long since) {
        log.info("Getting server changes for user {} since {}", userId, since);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("User {} not found for server changes", userId);
            return List.of();
        }

        LocalDateTime sinceDateTime = toLocalDateTime(since);
        List<Map<String, Object>> changes = new ArrayList<>();

        // Get word progress changes
        List<UserWordProgress> wordChanges = wordProgressRepository.findByUserAndUpdatedAtAfter(user, sinceDateTime);
        for (UserWordProgress wp : wordChanges) {
            Map<String, Object> change = new HashMap<>();
            change.put("entityType", "word");
            change.put("entityId", wp.getWord());
            change.put("data", Map.of(
                "word", wp.getWord(),
                "lastReviewed", toTimestamp(wp.getLastReviewed()),
                "nextReviewDate", toTimestamp(wp.getNextReviewDate()),
                "easinessFactor", wp.getEasinessFactor(),
                "intervalDays", wp.getIntervalDays(),
                "repetitions", wp.getRepetitions(),
                "isFavorite", wp.isFavorite(),
                "secondsSpent", wp.getSecondsSpent(),
                "isLearned", wp.isLearned()
            ));
            changes.add(change);
        }

        // Get statistic changes
        List<UserWordStatistic> statChanges = wordStatisticRepository.findByUserAndUpdatedAtAfter(user, sinceDateTime);
        for (UserWordStatistic ws : statChanges) {
            Map<String, Object> change = new HashMap<>();
            change.put("entityType", "statistic");
            change.put("entityId", ws.getWordId());
            change.put("data", Map.of(
                "wordId", ws.getWordId(),
                "correctCount", ws.getCorrectCount(),
                "wrongCount", ws.getWrongCount(),
                "skippedCount", ws.getSkippedCount(),
                "lastAnsweredAt", toTimestamp(ws.getLastAnsweredAt())
            ));
            changes.add(change);
        }

        // Get exam history changes
        List<UserExamHistory> examChanges = examHistoryRepository.findByUserAndUpdatedAtAfter(user, sinceDateTime);
        for (UserExamHistory eh : examChanges) {
            Map<String, Object> change = new HashMap<>();
            change.put("entityType", "exam");
            change.put("entityId", eh.getExamId());
            change.put("data", Map.of(
                "examId", eh.getExamId(),
                "completedAt", toTimestamp(eh.getCompletedAt()),
                "score", eh.getScore(),
                "totalQuestions", eh.getTotalQuestions(),
                "correctAnswers", eh.getCorrectAnswers(),
                "durationSeconds", eh.getDurationSeconds(),
                "examType", eh.getExamType() != null ? eh.getExamType() : ""
            ));
            changes.add(change);
        }

        // Get achievement changes
        List<UserAchievement> achievementChanges = achievementRepository.findByUserAndUpdatedAtAfter(user, sinceDateTime);
        for (UserAchievement ua : achievementChanges) {
            Map<String, Object> change = new HashMap<>();
            change.put("entityType", "achievement");
            change.put("entityId", ua.getAchievementId());
            change.put("data", Map.of(
                "achievementId", ua.getAchievementId(),
                "progress", ua.getProgress(),
                "targetProgress", ua.getTargetProgress(),
                "isUnlocked", ua.isUnlocked(),
                "unlockedAt", toTimestamp(ua.getUnlockedAt())
            ));
            changes.add(change);
        }

        log.info("Found {} server changes for user {} since {}", changes.size(), userId, sinceDateTime);
        return changes;
    }

    /**
     * Convert a timestamp (Long or Number) to LocalDateTime.
     */
    private LocalDateTime toLocalDateTime(Object timestamp) {
        if (timestamp == null) {
            return null;
        }
        long millis = ((Number) timestamp).longValue();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    /**
     * Convert LocalDateTime to timestamp (millis).
     */
    private long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0L;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
