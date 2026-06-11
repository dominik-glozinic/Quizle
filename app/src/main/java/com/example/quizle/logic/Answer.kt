package com.example.quizle.logic

import java.io.Serializable

data class Answer(
    val answerId: String,
    val answerText: String,
    val isCorrect: Boolean
) : Serializable {
    fun getText(): String = answerText
    fun isCorrectAnswer(): Boolean = isCorrect
}

