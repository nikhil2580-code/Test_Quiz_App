package com.nikhilkhairnar.testquizapp.ui.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikhilkhairnar.testquizapp.ui.db.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {

    // Fetch quiz questions from API
    fun fetchQuizFromApi(amount: Int, category: Int) {
        viewModelScope.launch {
            repository.fetchQuizQuestionsFromApi(amount, category)
        }
    }

    // Existing methods
    fun loadQuizDataFromAssets(context: Context) {
        viewModelScope.launch {
            repository.loadQuizDataFromAssets(context)
        }
    }

    fun getAllQuizItems() {
        viewModelScope.launch {
            repository.getAllQuizItems()
        }
    }
}
