package com.example.quizle.logic

data class LeaderboardEntry(
    val playerId: String,
    val username: String,
    val score: Int
)

class Leaderboard(
    private val entries: List<LeaderboardEntry>
) {
    fun getRanking(): List<LeaderboardEntry> = entries.sortedByDescending { it.score }

    fun getWinner(): LeaderboardEntry {
        return getRanking().first()
    }
}
