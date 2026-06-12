package com.example.quizle.logic

import java.io.Serializable

data class Question(
    val questionId: String,
    val quizId: String,
    val questionText: String,
    val timerSeconds: Int,
    private val answers: List<Answer>
) : Serializable {
    fun getText(): String = questionText
    fun getAnswers(): List<Answer> = answers
    fun getTimeLimitSec(): Int = timerSeconds
    fun isCorrect(answerId: String): Boolean =
        answers.any { it.answerId == answerId && it.isCorrect }
}
