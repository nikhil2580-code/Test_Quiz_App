package com.nikhilkhairnar.testquizapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikhilkhairnar.testquizapp.databinding.ActivityMainBinding
import com.nikhilkhairnar.testquizapp.ui.QuizListAdapter
import com.nikhilkhairnar.testquizapp.ui.db.AppDatabase
import com.nikhilkhairnar.testquizapp.ui.db.QuizItem
import com.nikhilkhairnar.testquizapp.ui.db.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var quizModelList: MutableList<QuizItem>
    private lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        setupRecyclerView()

        val database = AppDatabase.getDatabase(this)
        val quizDao = database.quizDao()
        val quizRepository = QuizRepository(quizDao)

        getDataFromRoom(quizRepository)
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList) { selectedQuiz ->
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("quizData", selectedQuiz)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromRoom(quizRepository: QuizRepository) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            quizRepository.loadQuizDataFromAssets(this@MainActivity)

            val quizItems = withContext(Dispatchers.IO) {
                quizRepository.getAllQuizItems()
            }

            if (quizItems.isNotEmpty()) {
                quizModelList.clear()
                quizModelList.addAll(quizItems)
                adapter.notifyDataSetChanged()

                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
