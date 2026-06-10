package com.example.quizle.logic

data class Answer(
    val answerId: String,
    val answerText: String,
    val isCorrect: Boolean
) {
    fun getText(): String = answerText
    fun isCorrectAnswer(): Boolean = isCorrect
}

