package com.example.quizle.logic

class Answer(
    private val answerId: String,
    private val answerText: String,
    private val isCorrect: Boolean
) {
    fun getText(): String = answerText
    fun isCorrectAnswer(): Boolean = isCorrect
}
