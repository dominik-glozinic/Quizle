package com.example.quizle.logic

class Question(
    private val questionId: String,
    private val quizId: String,
    private val questionText: String,
    private val timerSeconds: Int,
    private val answers: List<Answer> = emptyList()
) {
    fun getText(): String = questionText

    fun getAnswers(): List<Answer> = answers

    fun isCorrect(answerId: String): Boolean {
        TODO("Not yet implemented")
    }

    fun getTimeLimitSec(): Int = timerSeconds
}
