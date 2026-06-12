package com.example.quizle.logic

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val quiz: Quiz,
    val hostIpAddress: String,
    val port: Int
) {
    var state: GameState = GameState.WAITING
        private set

    var currentQuestionIndex: Int = -1
        private set

    var startedAt: Long? = null
        private set

    /** ms timestamps of when each question was broadcast; used for scoring */
    val questionOpenedTimestamps: Map<String, Long>
        get() = _questionOpenedTimestamps

    private val _questionOpenedTimestamps = ConcurrentHashMap<String, Long>()

    private val players = ConcurrentHashMap<String, Player>()
    private val answers = mutableListOf<PlayerAnswer>()
    private val answersLock = Any()

    // Public API

    fun getJoinUrl(): String = "http://$hostIpAddress:$port"

    fun getCurrentQuestion(): Question? {
        val questions = quiz.getQuestions()
        return if (currentQuestionIndex in questions.indices) questions[currentQuestionIndex] else null
    }

    /**
     * Moves to the next question (or marks the session FINISHED if all questions are done).
     * Returns the new current question, or null if game just finished.
     */
    fun advance(): Question? {
        val questions = quiz.getQuestions()
        currentQuestionIndex++
        return if (currentQuestionIndex < questions.size) {
            state = GameState.ACTIVE
            if (startedAt == null) startedAt = System.currentTimeMillis()
            val q = questions[currentQuestionIndex]
            _questionOpenedTimestamps[q.questionId] = System.currentTimeMillis()
            q
        } else {
            state = GameState.FINISHED
            null
        }
    }

    fun isFinished(): Boolean = state == GameState.FINISHED

    //Player management (called from network thread)

    fun registerPlayer(username: String): Player {
        val player = Player(username = username)
        players[player.userId] = player
        return player
    }

    fun getPlayers(): List<Player> = players.values.toList()

    //Answer recording (called from network thread)

    fun recordAnswer(answer: PlayerAnswer) {
        synchronized(answersLock) {
            answers.add(answer)
        }
    }

    fun getAnswers(): List<PlayerAnswer> {
        synchronized(answersLock) {
            return answers.toList()
        }
    }
}
