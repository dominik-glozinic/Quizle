package com.example.quizle.logic

class PlayerAnswer(
    private val playerId: String,
    private val questionId: String,
    private val answerId: String,
    private val answeredAtMs: Long
) {
    fun isCorrect(): Boolean {
        TODO("Not yet implemented")
    }

    fun getTimestamp(): Long = answeredAtMs
}
