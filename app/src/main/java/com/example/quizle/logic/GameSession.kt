package com.example.quizle.logic

class GameSession(
    val sessionId: String,
    val hostIpAddress: String,
    val port: Int,
    var state: GameState = GameState.WAITING,
    var currentQuestionIndex: Int = 0,
    var startedAt: Long? = null
) {
    private val playerAnswers: MutableList<PlayerAnswer> = mutableListOf()
    private var quiz: Quiz? = null

    fun getJoinUrl(): String = "http://$hostIpAddress:$port/join"

    fun getCurrentQuestion(): Question? {
        TODO("Not yet implemented")
    }

    fun advance() {
        TODO("Not yet implemented")
    }

    fun isFinished(): Boolean {
        TODO("Not yet implemented")
    }
}
