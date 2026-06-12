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

/**
 * Implementation of [IPlayerNetwork] for cases where a Player object is used as a data container. (Default use is as a active client)
 */
class StubPlayerNetwork : IPlayerNetwork {
    override fun join(url: String, username: String) = Result.failure<Player>(UnsupportedOperationException())
    override fun pollGameState() = Result.failure<GameStateDto>(UnsupportedOperationException())
    override fun submitAnswer(questionId: String, answerId: String) = Result.failure<Unit>(UnsupportedOperationException())
    override fun fetchLeaderboard() = Result.failure<Leaderboard>(UnsupportedOperationException())
}
