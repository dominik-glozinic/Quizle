package com.example.quizle.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * JSON serializer/deserializer for all network messages.
 */
class MessageSerializer {

    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    fun toJson(obj: Any): String = gson.toJson(obj)

    fun <T> fromJson(json: String, cls: Class<T>): T = gson.fromJson(json, cls)
}
