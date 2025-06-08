package com.gultekinahmetabdullah.trainvoc.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gultekinahmetabdullah.trainvoc.classes.word.Exam
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<Exam>)

    @Query("SELECT * FROM exams WHERE exam = :examName")
    suspend fun getExamByName(examName: String): Exam?

    @Query("SELECT * FROM exams")
    fun getAllExams(): Flow<List<Exam>>
}