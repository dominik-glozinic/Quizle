package com.example.quizle.persistence

import com.example.quizle.logic.Quiz

interface QuizRepository {
    fun insertQuiz(quiz: QuizEntity)
    fun getAllQuizzes(): List<QuizEntity>
    fun getQuizById(id: String): QuizEntity
    fun deleteQuiz(quiz: QuizEntity)
    fun insertQuestions(questions: List<QuestionEntity>)
    fun getQuestionsForQuiz(quizId: String): List<QuestionEntity>
}

class QuizRepositoryImpl(
    private val dao: RoomQuizDao
) : QuizRepository {

    override fun insertQuiz(quiz: QuizEntity) = dao.insertQuiz(quiz)
    override fun getAllQuizzes(): List<QuizEntity> = dao.getAllQuizzes()
    override fun getQuizById(id: String): QuizEntity = dao.getQuizById(id)
    override fun deleteQuiz(quiz: QuizEntity) = dao.deleteQuiz(quiz)
    override fun insertQuestions(questions: List<QuestionEntity>) = dao.insertQuestions(questions)
    override fun getQuestionsForQuiz(quizId: String): List<QuestionEntity> = dao.getQuestionsForQuiz(quizId)

    /** Convert a DB entity + its question rows into a domain Quiz object. */
    fun toQuiz(entity: QuizEntity, questions: List<QuestionEntity>): Quiz {
        TODO("Map QuizEntity + List<QuestionEntity> → Quiz domain object")
    }

    /** Convert a domain Quiz into a DB entity pair ready for insertion. */
    fun fromQuiz(quiz: Quiz): Pair<QuizEntity, List<QuestionEntity>> {
        TODO("Map Quiz → Pair<QuizEntity, List<QuestionEntity>>")
    }
}
