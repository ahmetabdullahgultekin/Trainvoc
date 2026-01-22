package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.word.WordExamCrossRef

@Dao
interface WordExamCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: WordExamCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordExamCrossRefs(crossRefs: List<WordExamCrossRef>)

    @Query("SELECT * FROM word_exam_cross_ref WHERE word = :word")
    suspend fun getCrossRefsByWord(word: String): List<WordExamCrossRef>

    @Query("SELECT * FROM word_exam_cross_ref WHERE exam = :exam")
    suspend fun getCrossRefsByExam(exam: String): List<WordExamCrossRef>

    @Query("SELECT exam FROM word_exam_cross_ref WHERE word = :word")
    suspend fun getExamNamesByWord(word: String): List<String>
}