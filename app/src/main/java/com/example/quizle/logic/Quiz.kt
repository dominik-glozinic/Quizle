package com.example.quizle.logic

class Quiz(
    private val quizId: String,
    private val title: String,
    private val description: String
) {
    private val questions: MutableList<Question> = mutableListOf()

    fun getQuestions(): List<Question> = questions.toList()

    fun addQuestion(q: Question) {
        questions.add(q)
    }
}
