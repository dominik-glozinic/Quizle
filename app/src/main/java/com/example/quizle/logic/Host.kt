package com.example.quizle.logic

class Host(
    userId: String,
    username: String,
    private var roomCode: String = ""
) : User(userId, username) {

    fun createSession(quiz: Quiz): GameSession {
        TODO("Not yet implemented")
    }

    fun startSession(sessionId: String) {
        TODO("Not yet implemented")
    }

    fun advanceToNextQuestion(sessionId: String) {
        TODO("Not yet implemented")
    }

    fun viewAnswers(): List<PlayerAnswer> {
        TODO("Not yet implemented")
    }

    fun displayResults(): Leaderboard {
        TODO("Not yet implemented")
    }
}
