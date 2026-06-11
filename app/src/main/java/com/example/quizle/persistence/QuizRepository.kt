package com.example.quizle.persistence

import com.example.quizle.logic.Answer
import com.example.quizle.logic.Question
import com.example.quizle.logic.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    private val gson = Gson()

    // ── QuizRepository delegation ─────────────────────────────────────────────

    override fun insertQuiz(quiz: QuizEntity) = dao.insertQuiz(quiz)
    override fun getAllQuizzes(): List<QuizEntity> = dao.getAllQuizzes()
    override fun getQuizById(id: String): QuizEntity = dao.getQuizById(id)
    override fun deleteQuiz(quiz: QuizEntity) = dao.deleteQuiz(quiz)
    override fun insertQuestions(questions: List<QuestionEntity>) = dao.insertQuestions(questions)
    override fun getQuestionsForQuiz(quizId: String): List<QuestionEntity> = dao.getQuestionsForQuiz(quizId)

    // ── Convenience: load a full Quiz in one call ─────────────────────────────

    /**
     * Loads a [Quiz] domain object by id, fetching its questions in one go.
     * Use this instead of calling [getQuizById] + [getQuestionsForQuiz] manually.
     */
    fun loadQuiz(quizId: String): Quiz {
        val entity = dao.getQuizById(quizId)
        val questionEntities = dao.getQuestionsForQuiz(quizId)
        return toQuiz(entity, questionEntities)
    }

    /**
     * Loads all [Quiz] domain objects from the database.
     */
    fun loadAllQuizzes(): List<Quiz> {
        return dao.getAllQuizzes().map { entity ->
            val questionEntities = dao.getQuestionsForQuiz(entity.id)
            toQuiz(entity, questionEntities)
        }
    }

    /**
     * Persists a [Quiz] domain object (quiz row + all question rows) atomically.
     * Existing rows with the same id are replaced (REPLACE conflict strategy on DAO).
     */
    fun saveQuiz(quiz: Quiz) {
        val (quizEntity, questionEntities) = fromQuiz(quiz)
        dao.insertQuiz(quizEntity)
        dao.insertQuestions(questionEntities)
    }

    /**
     * Deletes a [Quiz] and all its questions from the database.
     */
    fun removeQuiz(quiz: Quiz) {
        // Questions must be deleted first — Room has no FK cascade unless explicitly configured.
        dao.deleteQuestionsByQuizId(quiz.quizId)
        dao.deleteQuiz(QuizEntity(
            id = quiz.quizId,
            title = quiz.title,
            description = quiz.description,
            createdAt = 0L   // @Delete matches by PK only; other fields are ignored
        ))
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    /**
     * Maps a [QuizEntity] + its [QuestionEntity] rows to a [Quiz] domain object.
     *
     * The `answers` column in [QuestionEntity] is a JSON string (List<Answer>),
     * serialised by [fromQuiz] below.
     */
    fun toQuiz(entity: QuizEntity, questions: List<QuestionEntity>): Quiz {
        val answerListType = object : TypeToken<List<Answer>>() {}.type

        val domainQuestions = questions.map { qEntity ->
            val answers: List<Answer> = gson.fromJson(qEntity.answers, answerListType)
            Question(
                questionId = qEntity.questionId,
                quizId = qEntity.quizId,
                questionText = qEntity.questionText,
                timerSeconds = qEntity.timerSeconds,
                answers = answers
            )
        }

        val quiz = Quiz(
            quizId = entity.id,
            title = entity.title,
            description = entity.description
        )
        domainQuestions.forEach { quiz.addQuestion(it) }
        return quiz
    }

    /**
     * Maps a [Quiz] domain object to a [QuizEntity] + [List<QuestionEntity>] pair
     * ready for Room insertion.
     *
     * Each question's answers list is serialised to a JSON string.
     */
    fun fromQuiz(quiz: Quiz): Pair<QuizEntity, List<QuestionEntity>> {
        val quizEntity = QuizEntity(
            id = quiz.quizId,
            title = quiz.title,
            description = quiz.description,
            createdAt = System.currentTimeMillis()
        )

        val questionEntities = quiz.getQuestions().map { q ->
            QuestionEntity(
                questionId = q.questionId,
                quizId = quiz.quizId,
                questionText = q.getText(),
                timerSeconds = q.getTimeLimitSec(),
                answers = gson.toJson(q.getAnswers())   // List<Answer> → JSON string
            )
        }

        return Pair(quizEntity, questionEntities)
    }
}
