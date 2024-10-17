package com.nikhilkhairnar.testquizapp.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikhilkhairnar.testquizapp.databinding.QuizItemRecyclerRowBinding
import com.nikhilkhairnar.testquizapp.ui.db.QuizItem

class QuizListAdapter(
    private val quizItems: List<QuizItem>,
    private val onQuizSelected: (QuizItem) -> Unit // Callback for item click
) : RecyclerView.Adapter<QuizListAdapter.QuizViewHolder>() {

    class QuizViewHolder(private val binding: QuizItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quizItem: QuizItem, onQuizSelected: (QuizItem) -> Unit) {
            binding.apply {
                quizTitleText.text = quizItem.title
                quizSubtitleText.text = quizItem.subtitle
                quizTimeText.text = "${quizItem.time} min"

                // Set up click listener to pass selected quiz item
                root.setOnClickListener {
                    onQuizSelected(quizItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizItems[position], onQuizSelected)
    }

    override fun getItemCount() = quizItems.size
}
