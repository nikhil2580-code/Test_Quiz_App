package com.nikhilkhairnar.testquizapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nikhilkhairnar.testquizapp.R
import com.nikhilkhairnar.testquizapp.databinding.ActivityQuizBinding
import com.nikhilkhairnar.testquizapp.ui.db.AppDatabase
import com.nikhilkhairnar.testquizapp.ui.db.Question
import com.nikhilkhairnar.testquizapp.ui.db.QuizDao
import com.nikhilkhairnar.testquizapp.ui.db.QuizItem
import com.nikhilkhairnar.testquizapp.ui.db.QuizRepository
import com.nikhilkhairnar.testquizapp.ui.network.QuizViewModel
import com.nikhilkhairnar.testquizapp.ui.network.QuizViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class QuizActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var questionModelList: List<Question> = listOf()
    }

    lateinit var binding: ActivityQuizBinding
    private lateinit var selectedQuiz: QuizItem
    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(QuizRepository(AppDatabase.getDatabase(this).quizDao()))
    }

    var currentQuestionIndex = 0
    var selectedAnswer = ""
    var score = 0
    var remainingTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        selectedQuiz = intent.getSerializableExtra("quizData") as QuizItem

        if (savedInstanceState != null) {
            // Restore state
            currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex", 0)
            score = savedInstanceState.getInt("score", 0)
            remainingTimeInMillis = savedInstanceState.getLong("remainingTimeInMillis", 0)
        }
        quizViewModel.fetchQuizFromApi(10, 9)

        loadQuizData()

        // Fetch time from the selected quiz item and start the timer
        val quizTimeInMinutes = selectedQuiz.time  // Assuming `time` is a property of QuizItem
        startTimer(quizTimeInMinutes)
    }

    private fun loadQuizData() {
        val database = AppDatabase.getDatabase(this)
        val quizDao = database.quizDao()

        lifecycleScope.launch {
            val quizItems = withContext(Dispatchers.IO) {
                quizDao.getAllQuizItems()
            }
            questionModelList = quizItems.flatMap { it.questionList }
            startQuiz()
        }
    }

    private fun startQuiz() {
        questionModelList = selectedQuiz.questionList
        loadQuestion()
    }

    private fun loadQuestion() {
        selectedAnswer = ""
        if (currentQuestionIndex == questionModelList.size) {
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex + 1} / ${questionModelList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex + 1).toFloat() / questionModelList.size.toFloat() * 100).toInt()

            val currentQuestion = questionModelList[currentQuestionIndex]
            questionTextview.text = currentQuestion.question
            btn0.text = currentQuestion.options[0]
            btn1.text = currentQuestion.options[1]
            btn2.text = currentQuestion.options[2]
            btn3.text = currentQuestion.options[3]
        }
    }

    private fun startTimer(timeInMinutes: Int) {
        // Convert minutes to milliseconds
        val totalTimeInMillis = timeInMinutes * 60 * 1000L

        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                // Finish the quiz when the timer ends
                finishQuiz()
            }
        }.start()
    }

    private fun finishQuiz() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("totalQuestions", questionModelList.size)
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.grey))
            btn1.setBackgroundColor(getColor(R.color.grey))
            btn2.setBackgroundColor(getColor(R.color.grey))
            btn3.setBackgroundColor(getColor(R.color.grey))
        }

        val clickedBtn = view as Button
        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(applicationContext, "Please select an answer to continue", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedAnswer == questionModelList[currentQuestionIndex].correct_answer) {
                score++
                Log.i("Score of quiz", score.toString())
            }
            currentQuestionIndex++
            loadQuestion()
        } else {
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.orange))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save state
        outState.putInt("currentQuestionIndex", currentQuestionIndex)
        outState.putInt("score", score)
        outState.putLong("remainingTimeInMillis", remainingTimeInMillis)
    }
}
