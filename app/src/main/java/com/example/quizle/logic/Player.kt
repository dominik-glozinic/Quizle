package com.example.quizle.logic

import java.util.UUID

class Player(
    userId: String = UUID.randomUUID().toString(),
    username: String
) : User(userId = userId, username = username) {

    // These are thin delegation wrappers — the real calls go through IPlayerNetwork.
    // TODO: inject IPlayerNetwork and delegate
    fun joinSession(url: String, username: String): Result<Player> =
        Result.success(this)

    fun pollForQuestion(): Result<Question?> =
        Result.success(null)

    fun submitAnswer(questionId: String, answerId: String): Result<Unit> =
        Result.success(Unit)

    fun viewFinalScores(): Result<Leaderboard> =
        Result.success(Leaderboard(emptyList()))
}