package com.example.quizle.persistence

import com.example.quizle.logic.Host
import com.example.quizle.logic.IHostNetwork
import com.example.quizle.logic.Quiz

/**
 * QuizManager is the single entry point the UI / ViewModel uses to interact
 * with both the logic layer (Host) and the persistence layer (QuizRepository).
 *
 * Why a separate class instead of injecting the repo into Host?
 * ─────────────────────────────────────────────────────────────
 * • Host stays a pure logic class — no Android/Room imports, easy to unit-test.
 * • The ViewModel only needs to talk to one object for the host-side workflow.
 * • If two teammates work on Host and persistence simultaneously, there is
 *   zero overlap in the files they touch.
 *
 * Typical ViewModel usage
 * ───────────────────────
 *  val manager = QuizManager(repo, network, hostUsername)
 *
 *  // Host creates a quiz and saves it
 *  manager.saveQuiz(myQuiz)
 *
 *  // Host starts a session with a saved quiz
 *  val session = manager.startSessionFor(quizId, hostIp)
 *
 *  // Host advances through questions
 *  val nextQuestion = manager.advanceQuestion()   // null → game finished
 *
 *  // Host ends the session
 *  manager.endSession()
 */
class QuizManager(
    private val repo: QuizRepositoryImpl,
    network: IHostNetwork,
    hostUsername: String
) {
    val host: Host = Host(username = hostUsername, network = network)

    // Persistence functions

    fun saveQuiz(quiz: Quiz) = repo.saveQuiz(quiz)

    fun getAllQuizzes(): List<Quiz> = repo.loadAllQuizzes()

    fun getQuiz(quizId: String): Quiz = repo.loadQuiz(quizId)

    fun deleteQuiz(quiz: Quiz) = repo.removeQuiz(quiz)

    // Session only functions

    fun startSessionFor(quizId: String, hostIp: String, port: Int = 8888): String {
        val quiz = repo.loadQuiz(quizId)
        val session = host.createSession(quiz, hostIp, port)
        return session.getJoinUrl()
    }

    fun advanceQuestion() = host.advanceToNextQuestion()

    fun endSession() = host.endSession()
}
