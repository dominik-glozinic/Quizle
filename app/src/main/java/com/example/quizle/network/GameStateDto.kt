package com.example.quizle.network

import com.example.quizle.logic.Question

/**
 * Snapshot of the current game state sent to players via GET /poll.
 *
 * [state]               - GameState, e.g: WAITING, ACTIVE, PAUSED, FINISHED
 * [currentQuestion]     - the active question (null if WAITING or FINISHED)
 * [questionOpenedAtMs]  - ms when the host opened the current question
 */
data class GameStateDto(
    val state: String,
    val currentQuestion: Question?,
    val questionOpenedAtMs: Long?
)

/**
 * Request body sent by player on POST /join.
 */
data class JoinRequestDto(
    val username: String
)

/**
 * Request body sent by player on POST /answer.
 */
data class SubmitAnswerRequestDto(
    val questionId: String,
    val answerId: String
)

/**
 * JSON envelope for success/error responses.
 */
data class ApiResponseDto(
    val success: Boolean,
    val message: String? = null
)
