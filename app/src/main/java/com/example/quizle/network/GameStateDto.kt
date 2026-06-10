package com.example.quizle.network

import com.example.quizle.logic.Question

/**
 * Snapshot of the current game state sent to players via GET /poll.
 *
 * [state]               - one of the GameState enum names: WAITING, ACTIVE, PAUSED, FINISHED
 * [currentQuestion]     - the active question (null if WAITING or FINISHED)
 * [questionOpenedAtMs]  - epoch-ms when the host opened the current question (used for scoring)
 */
data class GameStateDto(
    val state: String,
    val currentQuestion: Question?,
    val questionOpenedAtMs: Long?
)

/**
 * Request body sent by the player on POST /join.
 */
data class JoinRequestDto(
    val username: String
)

/**
 * Request body sent by the player on POST /answer.
 */
data class SubmitAnswerRequestDto(
    val questionId: String,
    val answerId: String
)

/**
 * Generic JSON envelope for simple success/error responses.
 */
data class ApiResponseDto(
    val success: Boolean,
    val message: String? = null
)