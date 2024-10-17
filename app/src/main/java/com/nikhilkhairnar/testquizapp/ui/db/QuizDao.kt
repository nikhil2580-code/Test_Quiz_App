package com.nikhilkhairnar.testquizapp.ui.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quizItems: List<QuizItem>)

    @Query("SELECT * FROM quiz_table")
    suspend fun getAllQuizItems(): List<QuizItem>

    @Query("SELECT * FROM quiz_table WHERE id = :quizId")
    suspend fun getQuizById(quizId: String): QuizItem


}

