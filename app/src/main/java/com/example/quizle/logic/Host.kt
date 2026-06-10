package com.example.quizle.logic

import java.util.UUID

class Host(
    username: String,
    val roomCode: String = (100000..999999).random().toString()
) : User(username = username) {

    fun createSession(quiz: Quiz): GameSession =
        GameSession(quiz = quiz, hostIpAddress = "127.0.0.1", port = 8888)

    // TODO: delegate to IHostNetwork
    fun startSession(sessionId: String) {}

    // TODO: advance question index and call IHostNetwork.broadcastQuestion
    fun advanceToNextQuestion(sessionId: String) {}

    fun viewAnswers(session: GameSession): List<PlayerAnswer> = session.getAnswers()

    fun displayResults(session: GameSession): Leaderboard =
        ScoringService().buildLeaderboard(session, session.getAnswers())
}

