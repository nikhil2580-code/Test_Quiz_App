package com.nikhilkhairnar.testquizapp.ui.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nikhilkhairnar.testquizapp.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var sharedPreferences: SharedPreferences

    private var score: Int = 0
    private var totalQuestions: Int = 0
    private var percentage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get score and total questions from the intent
        score = intent.getIntExtra("score", 0)
        totalQuestions = intent.getIntExtra("totalQuestions", 0)

        // Calculate the percentage score
        percentage = ((score.toFloat() / totalQuestions) * 100).toInt()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)

        // Display the score progress
        binding.scoreProgressIndicator.progress = percentage
        binding.scoreProgressText.text = "$percentage%"

        // Update the result based on the percentage
        if (percentage > 60) {
            binding.scoreTitle.text = "Congrats! You have passed"
            binding.scoreTitle.setTextColor(Color.BLUE)
        } else {
            binding.scoreTitle.text = "Oops! You have failed"
            binding.scoreTitle.setTextColor(Color.RED)
        }

        // Display the score details
        binding.scoreSubtitle.text = "$score out of $totalQuestions questions are correct"

        // Load the saved high score from SharedPreferences
        val highScore = sharedPreferences.getInt("highScore", 0)

        // Check if the current score is a new high score
        if (percentage > highScore) {
            saveHighScore(percentage)
            binding.highScoreText.text = "New High Score: $percentage%"
        } else {
            binding.highScoreText.text = "High Score: $highScore%"
        }

        // Finish button click listener
        binding.finishBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the ResultActivity
        }
    }

    // Function to save the high score in SharedPreferences
    private fun saveHighScore(score: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("highScore", score)
        editor.apply()
    }
}
