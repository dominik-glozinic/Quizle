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
 * Dependency (build.gradle):
 *   implementation("com.squareup.okhttp3:okhttp:4.12.0")
 *
 * All calls are synchronous and must be called from a background thread or coroutine
 * (e.g. viewModelScope.launch(Dispatchers.IO) { ... }).
 *
 * [serverUrl] example: "http://192.168.1.42:8080"
 * [playerId]  assigned by the server on /join; must be set before calling submitAnswer.
 */
class PlayerHttpClient(
    private val serverUrl: String
) : IPlayerNetwork {

    private val serializer = MessageSerializer()

    /** Set after a successful [join] call. Required for answer submission. */
    var playerId: String? = null

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // ── IPlayerNetwork ───────────────────────────────────────────────────────

    /**
     * POST /join
     * Registers the player on the host server and stores the returned [Player.userId].
     */
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
     * GET /poll
     * Returns the current [GameStateDto]. The player should call this periodically
     * (e.g. every 1 s) to detect when a new question has arrived or the game has finished.
     */
    override fun pollGameState(): Result<GameStateDto> = runCatching {
        val response = get("$serverUrl/poll")
        serializer.fromJson(response, GameStateDto::class.java)
    }

    /**
     * POST /answer
     * Submits the player's chosen answer for the current question.
     * Requires [playerId] to be set (i.e. [join] must have been called first).
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
     * Fetches the final [Leaderboard] once the game has finished.
     * Returns a failure if the server responds with 404 (not yet available).
     */
    override fun fetchLeaderboard(): Result<Leaderboard> = runCatching {
        val response = get("$serverUrl/leaderboard")
        serializer.fromJson(response, Leaderboard::class.java)
    }

    // ── HTTP helpers ─────────────────────────────────────────────────────────

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
     * Executes a request and returns the body as a string.
     * Throws [IOException] on network failures and [HttpException] on non-2xx responses.
     */
    private fun execute(request: Request): String {
        val response = httpClient.newCall(request).execute()
        val bodyString = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            // Parse the API error message if possible, otherwise use HTTP status
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

/** Thrown when the server returns a non-2xx status code. */
class HttpException(val code: Int, message: String) : Exception("HTTP $code: $message")
