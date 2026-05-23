package com.example.quizle.network

import com.example.quizle.logic.*

/**
 * HTTP client used by the Player to talk to the Host's LocalHttpServer.
 * Use OkHttp or Ktor client — add to build.gradle:
 *   implementation("com.squareup.okhttp3:okhttp:4.12.0")
 */
class PlayerHttpClient(
    private val serverUrl: String
) : IPlayerNetwork {

    private val serializer = MessageSerializer()

    override fun join(url: String, username: String): Result<Player> {
        TODO("POST $serverUrl/join with username, parse Player response")
    }

    override fun pollGameState(): Result<GameStateDto> {
        TODO("GET $serverUrl/poll, parse GameStateDto")
    }

    override fun submitAnswer(questionId: String, answerId: String): Result<Unit> {
        TODO("POST $serverUrl/answer with questionId + answerId")
    }

    override fun fetchLeaderboard(): Result<Leaderboard> {
        TODO("GET $serverUrl/leaderboard, parse List<LeaderboardEntry>")
    }
}
