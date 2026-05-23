package com.example.quizle.logic

interface IHostNetwork {
    fun startServer(session: GameSession)
    fun stopServer()
    fun broadcastQuestion(q: Question)
    fun broadcastLeaderboard(lb: Leaderboard)
}

interface IPlayerNetwork {
    fun join(url: String, username: String): Result<Player>
    fun pollGameState(): Result<com.example.quizle.network.GameStateDto>
    fun submitAnswer(questionId: String, answerId: String): Result<Unit>
    fun fetchLeaderboard(): Result<Leaderboard>
}
