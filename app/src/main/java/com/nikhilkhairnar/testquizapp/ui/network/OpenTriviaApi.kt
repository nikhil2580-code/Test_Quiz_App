package com.nikhilkhairnar.testquizapp.ui.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTriviaApi {
    @GET("api.php")
    suspend fun getQuizQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("type") type: String = "multiple"
    ): Response<TriviaResponse>
}

data class TriviaResponse(
    val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)
