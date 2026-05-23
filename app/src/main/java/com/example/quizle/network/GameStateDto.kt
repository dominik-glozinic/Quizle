package com.example.quizle.network

import com.example.quizle.logic.Question

data class GameStateDto(
    val state: String,
    val currentQuestion: Question?,
    val questionOpenedAtMs: Long?
)
