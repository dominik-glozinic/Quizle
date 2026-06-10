package com.example.quizle.logic

data class PlayerAnswer(
    val playerId: String,
    val questionId: String,
    val answerId: String,
    val answeredAtMs: Long
) {
    // isCorrect is resolved by ScoringService against the Question — not stored here,
    // since the answer key lives in the Question object on the host side.
    fun getTimestamp(): Long = answeredAtMs
}
