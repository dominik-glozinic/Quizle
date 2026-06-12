/**
 * NetworkIntegrationTest.kt
 * ─────────────────────────
 * Plain Kotlin (JVM) integration test — no Android runtime needed.
 * Run with:  kotlinc *.kt -include-runtime -d test.jar && java -jar test.jar
 *
 * Or add the files to a plain JVM Gradle module and run as a main() function.
 *
 * Dependencies required on the classpath:
 *   - org.nanohttpd:nanohttpd:2.3.1
 *   - com.squareup.okhttp3:okhttp:4.12.0
 *   - com.google.code.gson:gson:2.10.1
 */

package com.example.quizle.test

import com.example.quizle.logic.*
import com.example.quizle.network.LocalHttpServer
import com.example.quizle.network.PlayerHttpClient
import org.junit.Test

class NetworkIntegrationTest {

    @Test
    fun testNetworkIntegration() {
        println("=== Quizle Network Integration Test ===\n")

    // 1. Build a sample quiz
    val quiz = Quiz(title = "Geography Quiz")
    quiz.addQuestion(
        Question(
            questionId = "q1",
            quizId = quiz.quizId,
            questionText = "What is the capital of France?",
            timerSeconds = 10,
            answers = listOf(
                Answer("a1", "Berlin",  isCorrect = false),
                Answer("a2", "Madrid",  isCorrect = false),
                Answer("a3", "Paris",   isCorrect = true),
                Answer("a4", "Lisbon",  isCorrect = false)
            )
        )
    )
    quiz.addQuestion(
        Question(
            questionId = "q2",
            quizId = quiz.quizId,
            questionText = "Which continent is Brazil in?",
            timerSeconds = 8,
            answers = listOf(
                Answer("b1", "Africa",        isCorrect = false),
                Answer("b2", "South America", isCorrect = true),
                Answer("b3", "Asia",          isCorrect = false),
                Answer("b4", "Europe",        isCorrect = false)
            )
        )
    )
    println("[OK] Quiz built: '${quiz.title}' with ${quiz.getQuestions().size} questions")

    // 2. Host creates and starts a session
    val server = LocalHttpServer(port = 8888)
    val host = Host(username = "HostUser", network = server)
    val session = host.createSession(quiz)
    println("[OK] Server started on port 8888  (join URL: ${session.getJoinUrl()})")

    Thread.sleep(200) // give NanoHTTPD a moment to bind

    // 3. Player joins
    val client = PlayerHttpClient(serverUrl = "http://127.0.0.1:8888")
    val joinResult = client.join(url = "http://127.0.0.1:8888", username = "Domy")
    check(joinResult.isSuccess) { "join() failed: ${joinResult.exceptionOrNull()}" }
    val player = joinResult.getOrThrow()
    println("[OK] Player joined: id=${player.userId}, username=${player.getUsername()}")

    // 4. Host advances to question 1
    val q1 = session.advance()!!
    server.broadcastQuestion(q1)
    println("[OK] Host broadcast question 1: '${q1.getText()}'")

    // 5. Player polls and sees the question
    Thread.sleep(100)
    val pollResult = client.pollGameState()
    check(pollResult.isSuccess) { "pollGameState() failed: ${pollResult.exceptionOrNull()}" }
    val gameState = pollResult.getOrThrow()
    check(gameState.currentQuestion?.questionId == "q1") {
        "Expected q1, got ${gameState.currentQuestion?.questionId}"
    }
    println("[OK] Player polled state: ${gameState.state}, question='${gameState.currentQuestion?.getText()}'")

    //6. Player submits a correct answer
    Thread.sleep(500)
    val answerResult = client.submitAnswer(questionId = "q1", answerId = "a3")
    check(answerResult.isSuccess) { "submitAnswer() failed: ${answerResult.exceptionOrNull()}" }
    println("[OK] Player submitted answer a3 (correct)")

    //7. Host advances to question 2, player answers incorrectly
    val q2 = session.advance()!!
    server.broadcastQuestion(q2)
    Thread.sleep(100)
    client.submitAnswer(questionId = "q2", answerId = "b3") // wrong answer
    println("[OK] Player submitted answer b3 (wrong)")

    // 8. Host finishes the game and builds the leaderboard
    session.advance() // advances past last question -> state = finished
    val leaderboard = ScoringService().buildLeaderboard(session, session.getAnswers())
    server.broadcastLeaderboard(leaderboard)
    println("[OK] Leaderboard broadcast")

    // 9. Player fetches leaderboard
    Thread.sleep(100)
    val lbResult = client.fetchLeaderboard()
    check(lbResult.isSuccess) { "fetchLeaderboard() failed: ${lbResult.exceptionOrNull()}" }
    val lb = lbResult.getOrThrow()
    println("\n=== Final Leaderboard ===")
    lb.getRanking().forEachIndexed { i, entry ->
        println("  ${i + 1}. ${entry.username}  —  ${entry.score} pts")
    }
    check(lb.getWinner().username == "Domy") { "Expected Domy to win" }
    check(lb.getWinner().score > 0) { "Winner should have more than 0 points" }

    // 10. Cleanup
    server.stopServer()
    println("\n[OK] Server stopped — all checks passed ✓")
}
}
