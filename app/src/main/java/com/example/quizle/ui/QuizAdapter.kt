package com.example.quizle.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizle.R
import com.example.quizle.logic.Quiz

class QuizAdapter(
    private val items: List<Quiz>,
    private val onHost: (Quiz) -> Unit,
    private val onDelete: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvQuizTitle)
        val btnHost: Button = v.findViewById(R.id.btnHost)
        val btnDelete: Button = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_quiz, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val quiz = items[position]
        holder.title.text = quiz.title
        holder.btnHost.setOnClickListener { onHost(quiz) }
        holder.btnDelete.setOnClickListener { onDelete(quiz) }
    }
}