package com.example.quizle.logic

import java.util.UUID

data class Quiz(
    val quizId: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    private val questions: MutableList<Question> = mutableListOf()
) {
    fun getQuestions(): List<Question> = questions.toList()
    fun addQuestion(q: Question) { questions.add(q) }
}