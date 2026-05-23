package com.example.quizle.logic

class Player(
    userId: String,
    username: String
) : User(userId, username) {

    fun joinSession(url: String, username: String): Result<Player> {
        TODO("Not yet implemented")
    }

    fun pollForQuestion(): Result<Question?> {
        TODO("Not yet implemented")
    }

    fun submitAnswer(questionId: String, answerId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    fun viewFinalScores(): Result<Leaderboard> {
        TODO("Not yet implemented")
    }
}
