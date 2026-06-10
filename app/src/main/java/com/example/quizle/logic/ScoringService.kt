package com.example.quizle.logic

/**
 * Calculates points for a single answer and builds the final leaderboard.
 *
 * Scoring model (matches the design decision in Systementscheidungen):
 *  - Only correct answers score points.
 *  - The faster the answer relative to the question's time limit, the more points.
 *  - Score scales linearly between MIN_POINTS and MAX_POINTS.
 *
 *  formula:  points = MAX_POINTS - (elapsed / timerMs) * (MAX_POINTS - MIN_POINTS)
 *            clamped to [MIN_POINTS, MAX_POINTS]
 */
class ScoringService {

    companion object {
        const val MAX_POINTS = 1000
        const val MIN_POINTS = 100
    }

    fun calculatePoints(
        answeredAtMs: Long,
        questionOpenedAtMs: Long,
        timerSeconds: Int,
        isCorrect: Boolean
    ): Int {
        if (!isCorrect) return 0

        val elapsedMs = (answeredAtMs - questionOpenedAtMs).coerceAtLeast(0)
        val timerMs = timerSeconds * 1000L

        // If somehow answered after the timer expired, award minimum points anyway
        if (elapsedMs >= timerMs) return MIN_POINTS

        val ratio = elapsedMs.toDouble() / timerMs.toDouble()
        val points = MAX_POINTS - (ratio * (MAX_POINTS - MIN_POINTS))
        return points.toInt().coerceIn(MIN_POINTS, MAX_POINTS)
    }

    fun buildLeaderboard(session: GameSession, answers: List<PlayerAnswer>): Leaderboard {
        val quiz = session.quiz
        val questionOpenedTimes = session.questionOpenedTimestamps

        // Map playerId -> total score
        val scoreMap = mutableMapOf<String, Int>()

        for (answer in answers) {
            val question = quiz.getQuestions().find { it.questionId == answer.questionId }
                ?: continue
            val openedAt = questionOpenedTimes[answer.questionId] ?: continue

            val pts = calculatePoints(
                answeredAtMs = answer.answeredAtMs,
                questionOpenedAtMs = openedAt,
                timerSeconds = question.getTimeLimitSec(),
                isCorrect = question.isCorrect(answer.answerId)
            )
            scoreMap[answer.playerId] = (scoreMap[answer.playerId] ?: 0) + pts
        }

        val entries = session.getPlayers().map { player ->
            LeaderboardEntry(
                playerId = player.userId,
                username = player.getUsername(),
                score = scoreMap[player.userId] ?: 0
            )
        }

        return Leaderboard(entries)
    }
}