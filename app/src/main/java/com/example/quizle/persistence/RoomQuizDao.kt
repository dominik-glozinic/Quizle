package com.example.quizle.persistence

import androidx.room.*

@Dao
interface RoomQuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): List<QuizEntity>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    fun getQuizById(id: String): QuizEntity

    @Delete
    fun deleteQuiz(quiz: QuizEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    fun getQuestionsForQuiz(quizId: String): List<QuestionEntity>
}
