package com.example.quizle.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val createdAt: Long
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val questionId: String,
    val quizId: String,
    val questionText: String,
    val timerSeconds: Int,
    val answers: String
)
