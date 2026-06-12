package com.example.quizle.logic

import java.util.UUID
import kotlin.jvm.Transient

/**
 * Represents a quiz participant.
 *
 * All network operations are delegated to the injected [network] (IPlayerNetwork).
 * This keeps Player testable without a real HTTP stack — just pass a mock.
 *
 * Typical call sequence:
 *  1. joinSession()       → establishes connection, stores playerId internally
 *  2. pollForQuestion()   → called repeatedly until a Question arrives
 *  3. submitAnswer()      → called once per question
 *  4. pollForQuestion()   → when GameState is FINISHED, call viewFinalScores()
 *  5. viewFinalScores()   → fetches the Leaderboard
 */
class Player(
    userId: String = UUID.randomUUID().toString(),
    username: String,
    @Transient private val network: IPlayerNetwork = StubPlayerNetwork()
) : User(userId = userId, username = username) {


    /**
     * POST /join — registers this player on the host server.
     */
    fun joinSession(url: String, username: String): Result<Player> =
        network.join(url, username)

    /**
     * GET /poll — returns the current question if the game is ACTIVE, or null
     * if still WAITING.  (NOTE: Returns a failure if the session is FINISHED so the
     * caller knows to switch to [viewFinalScores])
     */
    fun pollForQuestion(): Result<Question?> = runCatching {
        val dto = network.pollGameState().getOrThrow()
        when (dto.state) {
            GameState.FINISHED.name ->
                throw GameFinishedException("Game has finished — fetch the leaderboard")
            else -> dto.currentQuestion
        }
    }

    /**
     * POST /answer — submits the players chosen answer for the active question.
     */
    fun submitAnswer(questionId: String, answerId: String): Result<Unit> =
        network.submitAnswer(questionId, answerId)

    /**
     * GET /leaderboard — fetches the final scores once the game is FINISHED.
     */
    fun viewFinalScores(): Result<Leaderboard> =
        network.fetchLeaderboard()
}

/** when server signals FINISHED. */
class GameFinishedException(message: String) : Exception(message)
