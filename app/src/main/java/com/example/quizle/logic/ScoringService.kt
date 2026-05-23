package com.example.quizle.logic

object ScoringService {
    const val MAX_POINTS = 1000
    const val MIN_POINTS = 100

    fun calculatePoints(
        answeredAtMs: Long,
        questionOpenedAtMs: Long,
        timerSeconds: Int,
        isCorrect: Boolean
    ): Int {
        TODO("Not yet implemented")
    }

    fun buildLeaderboard(session: GameSession, answers: List<PlayerAnswer>): Leaderboard {
        TODO("Not yet implemented")
    }
}
