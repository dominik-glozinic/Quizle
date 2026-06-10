package com.example.quizle.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * JSON serializer/deserializer for all network messages.
 *
 * Dependency (build.gradle):
 *   implementation("com.google.code.gson:gson:2.10.1")
 */
class MessageSerializer {

    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    fun toJson(obj: Any): String = gson.toJson(obj)

    fun <T> fromJson(json: String, cls: Class<T>): T = gson.fromJson(json, cls)
}