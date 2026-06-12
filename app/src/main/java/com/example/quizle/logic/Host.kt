package com.example.quizle.logic

/**
 * NOTE:
 * Quiz loading/saving is handled by [QuizManager] in the persistence layer.
 */
class Host(
    username: String,
    val roomCode: String = (100000..999999).random().toString(),
    private val network: IHostNetwork
) : User(username = username) {

    // The session is created once per game and lives until the game ends.
    private var activeSession: GameSession? = null

    // Session lifecycle

    /**
     * @param quiz      The quiz to play through.
     * @param hostIp    The LAN IP address of this device
     * @param port      Port to bind (default 8888, see if change bricks the app).
     */
    fun createSession(quiz: Quiz, hostIp: String = "127.0.0.1", port: Int = 8888): GameSession {
        val session = GameSession(quiz = quiz, hostIpAddress = hostIp, port = port)
        activeSession = session
        network.startServer(session)
        return session
    }

    /**
     * Advances to the next question and broadcasts it to all connected players.
     * Returns the new current Question, or null if the quiz is now finished.
     */
    fun advanceToNextQuestion(): Question? {
        val session = requireSession()
        val question = session.advance()
        if (question != null) {
            network.broadcastQuestion(question)
        } else {
            // No more questions — build and broadcast the leaderboard.
            val leaderboard = ScoringService().buildLeaderboard(session, session.getAnswers())
            network.broadcastLeaderboard(leaderboard)
        }
        return question
    }

    /** Stops the HTTP server and clears the active session. */
    fun endSession() {
        network.stopServer()
        activeSession = null
    }


    fun getActiveSession(): GameSession = requireSession()

    fun viewAnswers(): List<PlayerAnswer> = requireSession().getAnswers()

    fun displayResults(): Leaderboard {
        val session = requireSession()
        return ScoringService().buildLeaderboard(session, session.getAnswers())
    }


    private fun requireSession(): GameSession =
        activeSession ?: throw IllegalStateException(
            "No active session. Call createSession() first."
        )
}
