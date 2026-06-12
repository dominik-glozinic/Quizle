package com.example.quizle.network

import com.example.quizle.logic.*
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

/**
 * HTTP server running on the host device (LAN).
 *
 * MEthod Map:
 *
 *  POST /join        body: { "username": "..." }
 *                    → 200 { Player JSON } | 400 on bad input | 409 if session not WAITING
 *
 *  POST /answer      body: { "questionId": "...", "answerId": "..." }
 *                    → 200 { success: true } | 400 on bad input | 404 unknown player
 *
 *  GET  /poll        → 200 { GameStateDto JSON }
 *
 *  GET  /leaderboard → 200 { Leaderboard JSON } | 404 if not yet available
 */
class LocalHttpServer(
    private val port: Int
) : IHostNetwork {

    private var session: GameSession? = null

    private val serializer = MessageSerializer()

    // NanoHTTPD inner server
    private var nano: NanoHTTPD? = null

    // Volatile state shared between server thread andhost UI thread
    @Volatile private var currentGameState: GameStateDto =
        GameStateDto(state = GameState.WAITING.name, currentQuestion = null, questionOpenedAtMs = null)

    @Volatile private var currentLeaderboard: Leaderboard? = null

    // IHostNetwork

    override fun startServer(session: GameSession) {
        this.session = session
        nano = object : NanoHTTPD(port) {
            override fun serve(httpSession: IHTTPSession): Response {
                return try {
                    route(httpSession)
                } catch (e: Exception) {
                    newFixedLengthResponse(
                        Response.Status.INTERNAL_ERROR,
                        MIME_JSON,
                        serializer.toJson(ApiResponseDto(success = false, message = e.message))
                    )
                }
            }
        }
        try {
            nano!!.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
        } catch (e: IOException) {
            throw RuntimeException("Failed to start HTTP server on port $port", e)
        }
    }

    override fun stopServer() {
        nano?.stop()
        nano = null
    }

    /** advancing to the next question. */
    override fun broadcastQuestion(q: Question) {
        currentGameState = GameStateDto(
            state = GameState.ACTIVE.name,
            currentQuestion = q,
            questionOpenedAtMs = System.currentTimeMillis()
        )
    }

    /** game finishes AND results are ready. */
    override fun broadcastLeaderboard(lb: Leaderboard) {
        currentLeaderboard = lb
        currentGameState = currentGameState.copy(state = GameState.FINISHED.name)
    }

    //Routing

    private fun route(req: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val uri = req.uri.trimEnd('/')
        return when {
            req.method == NanoHTTPD.Method.POST && uri == "/join"    -> handleJoin(req)
            req.method == NanoHTTPD.Method.POST && uri == "/answer"  -> handleSubmitAnswer(req)
            req.method == NanoHTTPD.Method.GET  && uri == "/poll"    -> handlePoll()
            req.method == NanoHTTPD.Method.GET  && uri == "/leaderboard" -> handleLeaderboard()
            else -> NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.NOT_FOUND,
                MIME_JSON,
                serializer.toJson(ApiResponseDto(success = false, message = "Not found"))
            )
        }
    }

    // Handlers

    private fun handleJoin(req: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val body = readBody(req)
        val joinRequest = serializer.fromJson(body, JoinRequestDto::class.java)

        if (joinRequest.username.isBlank()) {
            return errorResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "Username must not be blank")
        }

        // Only allow joining while waiting
        val s = session ?: return errorResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "Server not initialized")
        if (s.state != GameState.WAITING) {
            return errorResponse(NanoHTTPD.Response.Status.valueOf("409 Conflict"), "Session already started")
        }

        val player = s.registerPlayer(joinRequest.username)
        return okResponse(player)
    }

    private fun handleSubmitAnswer(req: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val body = readBody(req)
        val answerReq = serializer.fromJson(body, SubmitAnswerRequestDto::class.java)

        val playerId = req.headers["x-player-id"]
            ?: return errorResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "Missing X-Player-Id header")

        if (answerReq.questionId.isBlank() || answerReq.answerId.isBlank()) {
            return errorResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "questionId and answerId are required")
        }

        val questionOpenedAt = currentGameState.questionOpenedAtMs
            ?: return errorResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "No active question")

        val playerAnswer = PlayerAnswer(
            playerId = playerId,
            questionId = answerReq.questionId,
            answerId = answerReq.answerId,
            answeredAtMs = System.currentTimeMillis()
        )

        session?.recordAnswer(playerAnswer)
        return okResponse(ApiResponseDto(success = true))
    }

    private fun handlePoll(): NanoHTTPD.Response = okResponse(currentGameState)

    private fun handleLeaderboard(): NanoHTTPD.Response {
        val lb = currentLeaderboard
            ?: return errorResponse(NanoHTTPD.Response.Status.NOT_FOUND, "Leaderboard not yet available")
        return okResponse(lb)
    }

    // Helpers 2

    private fun okResponse(obj: Any): NanoHTTPD.Response =
        NanoHTTPD.newFixedLengthResponse(
            NanoHTTPD.Response.Status.OK,
            MIME_JSON,
            serializer.toJson(obj)
        )

    private fun errorResponse(status: NanoHTTPD.Response.Status, msg: String): NanoHTTPD.Response =
        NanoHTTPD.newFixedLengthResponse(
            status,
            MIME_JSON,
            serializer.toJson(ApiResponseDto(success = false, message = msg))
        )

    /** Reads request body as a UTF-8 string. */
    private fun readBody(req: NanoHTTPD.IHTTPSession): String {
        val files = HashMap<String, String>()
        return try {
            req.parseBody(files)
            files["postData"] ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        private const val MIME_JSON = "application/json"
    }
}
