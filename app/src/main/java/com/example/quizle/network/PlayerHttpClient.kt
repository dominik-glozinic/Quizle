package com.example.quizle.network

import com.example.quizle.logic.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * HTTP client used by the Player to communicate with the Host's [LocalHttpServer].
 *
 * [serverUrl] example: "http://192.168.1.42:8080" BUT only the IP part is important --> How to implement wiwth QR?
 * [playerId]  assigned by the server. Required for answer submition. Currently multiple players with the sname name are allowed.
 *              SHould that be changed?
 */
class PlayerHttpClient(
    private val serverUrl: String
) : IPlayerNetwork {

    private val serializer = MessageSerializer()

    var playerId: String? = null

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    //IPlayerNetwork


    override fun join(url: String, username: String): Result<Player> {
        val body = JoinRequestDto(username = username)
        return runCatching {
            val response = post("$serverUrl/join", body)
            val player = serializer.fromJson(response, Player::class.java)
            playerId = player.userId   // store for subsequent requests
            player
        }
    }

    /**
     * Returns the current [GameStateDto]
     * Should be called every 1s maybe
     */
    override fun pollGameState(): Result<GameStateDto> = runCatching {
        val response = get("$serverUrl/poll")
        serializer.fromJson(response, GameStateDto::class.java)
    }

    /**
     * POST /answer
     * Submits the player's chosen answer for the current question.
     */
    override fun submitAnswer(questionId: String, answerId: String): Result<Unit> = runCatching {
        val pid = playerId
            ?: throw IllegalStateException("playerId is null — call join() before submitting an answer")

        val body = SubmitAnswerRequestDto(questionId = questionId, answerId = answerId)
        post("$serverUrl/answer", body, extraHeaders = mapOf("X-Player-Id" to pid))
        Unit
    }

    /**
     * GET /leaderboard
     * Fetches the final [Leaderboard] once the game has finished. --> Currently just a placeholder
     */
    override fun fetchLeaderboard(): Result<Leaderboard> = runCatching {
        val response = get("$serverUrl/leaderboard")
        serializer.fromJson(response, Leaderboard::class.java)
    }

    // HTTP helpers

    private fun get(url: String): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return execute(request)
    }

    private fun post(
        url: String,
        body: Any,
        extraHeaders: Map<String, String> = emptyMap()
    ): String {
        val jsonBody = serializer.toJson(body)
            .toRequestBody(MEDIA_TYPE_JSON)

        val builder = Request.Builder()
            .url(url)
            .post(jsonBody)

        extraHeaders.forEach { (k, v) -> builder.addHeader(k, v) }

        return execute(builder.build())
    }

    /**
     * [IOException] - network failures
     * [HttpException] - non-2xx responses.
     */
    private fun execute(request: Request): String {
        val response = httpClient.newCall(request).execute()
        val bodyString = response.body?.string() ?: ""
        if (!response.isSuccessful) {

            val message = try {
                serializer.fromJson(bodyString, ApiResponseDto::class.java).message
                    ?: "HTTP ${response.code}"
            } catch (_: Exception) {
                "HTTP ${response.code}"
            }
            throw HttpException(response.code, message)
        }
        return bodyString
    }

    companion object {
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}


class HttpException(val code: Int, message: String) : Exception("HTTP $code: $message")
