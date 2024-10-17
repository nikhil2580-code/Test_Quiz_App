package com.nikhilkhairnar.testquizapp.ui.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "quiz_table")
@TypeConverters(QuestionConverter::class)
data class QuizItem(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val time: Int,
    val questionList: List<Question>,
) : Serializable

data class Question(
    val question: String,
    val options: List<String>,
    val correct_answer: String
) : Serializable
