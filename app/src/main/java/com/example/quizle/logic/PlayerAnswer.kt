package com.example.quizle.logic

data class PlayerAnswer(
    val playerId: String,
    val questionId: String,
    val answerId: String,
    val answeredAtMs: Long
) {

    fun getTimestamp(): Long = answeredAtMs
}
