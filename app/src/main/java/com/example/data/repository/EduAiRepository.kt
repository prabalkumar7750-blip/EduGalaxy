package com.example.data.repository

import android.util.Log
import com.example.data.model.QuestionItem
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "EduAiRepository"

class EduAiRepository {

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    private val questionGenSchema = Schema.obj(
        properties = mapOf(
            "statement" to Schema.string(description = "Clear practice question statement"),
            "options" to Schema.array(
                items = Schema.string(),
                description = "Exactly 4 multiple choice options"
            ),
            "correctAnswer" to Schema.string(description = "Must match one of the 4 options verbatim"),
            "explanationEn" to Schema.string(description = "Step-by-step English explanation"),
            "explanationHi" to Schema.string(description = "Step-by-step Hindi explanation in Devanagari"),
            "topic" to Schema.string(description = "Specific topic name"),
            "difficulty" to Schema.enumeration(listOf("Easy", "Medium", "Hard"))
        )
    )

    private val questionGenModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = questionGenSchema
            }
        )
    }

    fun streamTutorResponse(
        studentQuery: String,
        academicLevel: String,
        subject: String,
        languageMode: String
    ): Flow<String> = flow {
        val systemInstruction = buildString {
            appendLine("You are EduGalaxy AI, a warm, encouraging Indian educational tutor.")
            appendLine("Target Student Level: $academicLevel")
            appendLine("Subject Focus: $subject")
            appendLine("Response Language: $languageMode (if Hindi, explain clearly in Devanagari Hindi with technical terms in English in brackets; if Bilingual, provide clear English followed by concise Hindi summary; if English, clear step-by-step English).")
            appendLine("Rules: Adapt depth strictly to $academicLevel. Use step-by-step reasoning for Math/Science. Never invent or claim guaranteed exam questions or fake previous-year citations.")
            appendLine()
            appendLine("Student Question: $studentQuery")
        }

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chatModel.generateContentStream(systemInstruction).collect { chunk ->
                val text = chunk.text.orEmpty()
                if (text.isNotEmpty()) {
                    accumulated.append(text)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("EduGalaxy AI returned an empty response. Please try again.")
        }
    }

    suspend fun generatePracticeQuestion(
        subject: String,
        academicLevel: String,
        chapter: String,
        difficulty: String
    ): Result<QuestionItem> {
        return try {
            val prompt = """
                Create 1 original practice MCQ question for:
                - Level: $academicLevel
                - Subject: $subject
                - Chapter: $chapter
                - Difficulty: $difficulty
                Note: This is an AI-generated practice question, NOT a previous-year paper question.
                Provide 4 distinct options, the exact matching correctAnswer, and clear step-by-step explanations in both English and Hindi (Devanagari).
            """.trimIndent()

            val response = withTimeout(60_000L) {
                questionGenModel.generateContent(prompt)
            }
            val jsonStr = response.text ?: throw IllegalStateException("Empty AI JSON response")
            val json = JSONObject(jsonStr)
            val optionsArray = json.getJSONArray("options")
            val options = (0 until optionsArray.length()).map { optionsArray.getString(it) }
            val item = QuestionItem(
                id = "ai_${System.currentTimeMillis()}",
                subject = subject,
                academicLevel = academicLevel,
                chapter = chapter,
                topic = json.optString("topic", chapter),
                questionType = "Expected Practice (AI)",
                difficulty = json.optString("difficulty", difficulty),
                marks = 4L,
                year = "AI Practice",
                source = "AI-Generated Practice Question",
                statement = json.getString("statement"),
                options = options,
                correctAnswer = json.getString("correctAnswer"),
                explanationEn = json.getString("explanationEn"),
                explanationHi = json.getString("explanationHi"),
                aiGenerated = true,
                repeatedTag = false,
                published = true
            )
            Result.success(item)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI practice question generation failed", e)
            Result.failure(e)
        }
    }
}
