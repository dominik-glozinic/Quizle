package com.example.quizle.logic

import com.example.quizle.network.GameStateDto

// ── IHostNetwork ──────────────────────────────────────────────────────────────

interface IHostNetwork {
    fun startServer(session: GameSession)
    fun stopServer()
    fun broadcastQuestion(q: Question)
    fun broadcastLeaderboard(lb: Leaderboard)
}

// ── IPlayerNetwork ────────────────────────────────────────────────────────────

interface IPlayerNetwork {
    fun join(url: String, username: String): Result<Player>
    fun pollGameState(): Result<GameStateDto>
    fun submitAnswer(questionId: String, answerId: String): Result<Unit>
    fun fetchLeaderboard(): Result<Leaderboard>
}
