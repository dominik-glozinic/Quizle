package com.example.quizle.logic

abstract class User(
    private val userId: String,
    private var username: String
) {
    fun getUsername(): String = username
    fun setUsername(name: String) { username = name }
}
