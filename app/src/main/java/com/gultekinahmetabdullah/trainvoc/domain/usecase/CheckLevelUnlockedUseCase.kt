package com.gultekinahmetabdullah.trainvoc.domain.usecase

import com.gultekinahmetabdullah.trainvoc.classes.enums.WordLevel
import com.gultekinahmetabdullah.trainvoc.repository.IWordRepository
import javax.inject.Inject

/**
 * Use Case for checking if a word level is unlocked.
 * Implements the business rule: level unlocks when previous level is mastered.
 * Follows Dependency Inversion Principle by depending on IWordRepository interface.
 */
class CheckLevelUnlockedUseCase @Inject constructor(
    private val repository: IWordRepository
) {
    /**
     * Checks if a given level is unlocked for the user.
     * A1 is always unlocked. Other levels unlock when the previous level is completed.
     *
     * @param level The level to check
     * @return Result containing true if unlocked, false otherwise
     */
    suspend operator fun invoke(level: WordLevel): Result<Boolean> {
        return try {
            // A1 is always unlocked
            if (level == WordLevel.A1) {
                return Result.success(true)
            }

            // Check if previous level is unlocked
            val previousLevel = WordLevel.entries.getOrNull(level.ordinal - 1)
                ?: return Result.success(false)

            val isUnlocked = repository.isLevelUnlocked(previousLevel)
            Result.success(isUnlocked)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets all levels with their locked/unlocked status.
     */
    suspend fun getAllLevelsStatus(): Result<Map<WordLevel, Boolean>> {
        return try {
            val levelStatus = WordLevel.entries.associate { level ->
                level to invoke(level).getOrDefault(false)
            }
            Result.success(levelStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
