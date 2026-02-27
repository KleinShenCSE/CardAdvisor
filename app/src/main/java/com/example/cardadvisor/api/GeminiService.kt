package com.example.cardadvisor.api

import android.util.Base64
import com.example.cardadvisor.BuildConfig
import com.example.cardadvisor.domain.Category
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

data class GeminiResult(
    val category: Category,
    val description: String
)

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val endpoint =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun identifyPurchase(imageFile: File): Result<GeminiResult> = runCatching {
        val imageBytes = imageFile.readBytes()
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        val categoryNames = Category.entries.joinToString(", ") { it.name }
        val prompt = """
            Look at this image and identify what type of purchase or product this is.
            Return ONLY a JSON object with this exact format:
            {"category": "<CATEGORY>", "description": "<brief description>"}

            Category must be one of: $categoryNames
            Choose the most specific matching category. Use OTHER if unsure.
            Do not include any text outside the JSON.
        """.trimIndent()

        val requestBody = JsonObject().apply {
            add("contents", gson.toJsonTree(listOf(mapOf(
                "parts" to listOf(
                    mapOf("text" to prompt),
                    mapOf("inline_data" to mapOf(
                        "mime_type" to "image/jpeg",
                        "data" to base64Image
                    ))
                )
            ))))
        }

        val request = Request.Builder()
            .url(endpoint)
            .post(gson.toJson(requestBody).toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: error("Empty response from Gemini")

        if (!response.isSuccessful) error("Gemini API error ${response.code}: $body")

        val json = gson.fromJson(body, JsonObject::class.java)
        val text = json
            .getAsJsonArray("candidates")
            .get(0).asJsonObject
            .getAsJsonObject("content")
            .getAsJsonArray("parts")
            .get(0).asJsonObject
            .get("text").asString
            .trim()
            .removePrefix("```json").removePrefix("```").removeSuffix("```")
            .trim()

        val result = gson.fromJson(text, JsonObject::class.java)
        GeminiResult(
            category = Category.fromString(result.get("category").asString),
            description = result.get("description").asString
        )
    }
}
