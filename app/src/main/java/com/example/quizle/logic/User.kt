package com.example.quizle.logic

import java.util.UUID

abstract class User(
    val userId: String = UUID.randomUUID().toString(),
    private var username: String
) {
    fun getUsername(): String = username
    fun setUsername(name: String) { username = name }
}

