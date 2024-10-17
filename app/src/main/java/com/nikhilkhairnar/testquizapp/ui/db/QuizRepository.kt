package com.nikhilkhairnar.testquizapp.ui.db


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nikhilkhairnar.testquizapp.ui.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class QuizRepository(private val quizDao: QuizDao) {

    // Fetch questions from Open Trivia API and store in Room database
    suspend fun fetchQuizQuestionsFromApi(amount: Int, category: Int): List<QuizItem>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getQuizQuestions(amount, category)
                if (response.isSuccessful) {
                    val questions = response.body()?.results ?: emptyList()

                    // Convert API data to QuizItem format
                    val quizItems = questions.mapIndexed { index, triviaQuestion ->
                        QuizItem(
                            id = index.toString(),
                            title = "Quiz ${index + 1}",
                            subtitle = "Generated from Open Trivia API",
                            time = 30,
                            questionList = listOf(
                                Question(
                                    question = triviaQuestion.question,
                                    options = triviaQuestion.incorrect_answers + triviaQuestion.correct_answer,
                                    correct_answer = triviaQuestion.correct_answer
                                )
                            )
                        )
                    }

                    // Insert fetched data into Room database
                    quizDao.insertAll(quizItems)
                    return@withContext quizItems
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    // Existing methods
    suspend fun loadQuizDataFromAssets(context: Context): List<QuizItem> {
        return withContext(Dispatchers.IO) {
            // Load the JSON from assets (existing method)
            val inputStream = context.assets.open("quiz_data.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val quizType = object : TypeToken<List<QuizItem>>() {}.type
            val quizItems: List<QuizItem> = gson.fromJson(reader, quizType)

            // Insert data into Room database
            quizDao.insertAll(quizItems)
            quizItems
        }
    }

    suspend fun getAllQuizItems(): List<QuizItem> {
        return withContext(Dispatchers.IO) {
            quizDao.getAllQuizItems()
        }
    }
}

