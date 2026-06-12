package com.example.quizle.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.R
import com.example.quizle.logic.Question

class QuestionAdapter(
    private val questions: List<Question>,
    private val onClick: (Question) -> Unit,
    private val onDelete: (Question) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView = view.findViewById(R.id.tvQuestionNumber)
        val questionText: TextView = view.findViewById(R.id.tvQuestionText)
        val btnDelete: View = view.findViewById(R.id.btnDeleteQuestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.questionNumber.text = "${position + 1}."
        holder.questionText.text = question.questionText
        holder.itemView.setOnClickListener { onClick(question) }
        holder.btnDelete.setOnClickListener { onDelete(question) }
    }

    override fun getItemCount() = questions.size
}
