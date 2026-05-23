package com.example.quizle.network

import com.example.quizle.logic.*

/**
 * HTTP server running on the host device (LAN).
 * Uses NanoHTTPD — add to build.gradle: implementation("org.nanohttpd:nanohttpd:2.3.1")
 */
class LocalHttpServer(
    private val port: Int,
    private val session: GameSession
) : IHostNetwork {

    private val serializer = MessageSerializer()

    override fun startServer(session: GameSession) {
        TODO("Start NanoHTTPD server on [port]")
    }

    override fun stopServer() {
        TODO("Stop NanoHTTPD server")
    }

    override fun broadcastQuestion(q: Question) {
        TODO("Store current question so /poll returns it")
    }

    override fun broadcastLeaderboard(lb: Leaderboard) {
        TODO("Store leaderboard so /leaderboard returns it")
    }

    private fun handleJoin(/* req: NanoHTTPD.IHTTPSession */): Any /* NanoHTTPD.Response */ {
        TODO("Parse username, register player, return Player JSON")
    }

    private fun handleSubmitAnswer(/* req */): Any {
        TODO("Parse questionId + answerId, record PlayerAnswer")
    }

    private fun handlePoll(/* req */): Any {
        TODO("Return current GameStateDto as JSON")
    }
}
