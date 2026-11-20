package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.AiSession
import com.example.data.model.EducationCatalog
import com.example.ui.AiChatState
import com.example.ui.components.BilingualToggleChip
import com.example.ui.components.GlassCard
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun AiTutorScreen(
    aiState: AiChatState,
    pastSessions: List<AiSession>,
    academicLevel: String,
    initialSubject: String? = null,
    initialPrompt: String? = null,
    preferredLanguage: String,
    onAskAi: (prompt: String, subject: String, level: String, language: String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    var promptText by remember(initialPrompt) { mutableStateOf(initialPrompt.orEmpty()) }
    var selectedSubject by remember(initialSubject) { mutableStateOf(initialSubject ?: "Mathematics") }
    var languageMode by remember(preferredLanguage) { mutableStateOf(preferredLanguage) }

    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) ttsReady = true
        }
        instance
    }
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                promptText = spoken
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_tutor_screen")
    ) {
        TopScreenBar(
            title = "EduGalaxy AI Tutor",
            subtitle = "Adapted for $academicLevel • Hindi & English",
            onBack = onBack
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BilingualToggleChip(selectedLanguage = languageMode, onSelect = { languageMode = it })

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(EducationCatalog.subjects.take(12)) { sub ->
                    FilterChip(
                        selected = selectedSubject == sub.name,
                        onClick = { selectedSubject = sub.name },
                        label = { Text(sub.name) }
                    )
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val presets = listOf(
                    "Explain this topic simply" to "Explain the most fundamental concept in $selectedSubject for $academicLevel step-by-step.",
                    "Solve Math Step-by-Step" to "Solve x^2 - 5x + 6 = 0 step-by-step and explain the discriminant.",
                    "Chapter Summary" to "Create a crisp chapter summary and key formulas for $selectedSubject ($academicLevel).",
                    "Revision Notes" to "Make bullet-point exam revision notes for $selectedSubject ($academicLevel).",
                    "Practice Questions" to "Create 3 labeled AI practice questions with answers for $selectedSubject ($academicLevel)."
                )
                items(presets) { (label, fullPrompt) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NeonPurple.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.55f)),
                        onClick = {
                            promptText = fullPrompt
                            onAskAi(fullPrompt, selectedSubject, academicLevel, languageMode)
                        }
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                when (aiState) {
                    AiChatState.Idle -> {
                        GlassCard(borderColor = NeonCyan.copy(alpha = 0.5f)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Ask any study doubt!", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    Text("Type or tap the mic to ask in Hindi or English. EduGalaxy AI explains concepts step-by-step for $academicLevel.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                }
                            }
                        }
                    }
                    AiChatState.Loading -> {
                        GlassCard(borderColor = NeonCyan) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = NeonCyan, strokeWidth = 2.5.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("EduGalaxy AI is thinking...", style = MaterialTheme.typography.bodyLarge, color = NeonCyan)
                            }
                        }
                    }
                    is AiChatState.Streaming -> {
                        AiResponseCard(
                            text = aiState.text,
                            isStreaming = true,
                            onSpeak = {
                                if (ttsReady) {
                                    tts.language = if (languageMode == "Hindi") Locale("hi", "IN") else Locale.US
                                    tts.speak(aiState.text, TextToSpeech.QUEUE_FLUSH, null, "ai_tts")
                                }
                            }
                        )
                    }
                    is AiChatState.Success -> {
                        AiResponseCard(
                            text = aiState.text,
                            isStreaming = false,
                            onSpeak = {
                                if (ttsReady) {
                                    tts.language = if (languageMode == "Hindi") Locale("hi", "IN") else Locale.US
                                    tts.speak(aiState.text, TextToSpeech.QUEUE_FLUSH, null, "ai_tts")
                                }
                            }
                        )
                    }
                    is AiChatState.Error -> {
                        GlassCard(borderColor = NeonRose) {
                            Text(aiState.message, color = NeonRose, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            if (pastSessions.isNotEmpty()) {
                item {
                    Text("Saved Doubt History (${pastSessions.size})", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                }
                items(pastSessions.sortedByDescending { it.createdAt?.seconds ?: 0L }.take(10), key = { it.id }) { sess ->
                    GlassCard(borderColor = Color.White.copy(alpha = 0.18f)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${sess.subject} • ${sess.academicLevel} • ${sess.language}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                            Text("Q: ${sess.prompt}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(sess.response, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Composer Row
        Surface(
            color = Color(0xFF0C1230),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (languageMode == "Hindi") "hi-IN" else "en-IN")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask EduGalaxy AI...")
                        }
                        try {
                            voiceLauncher.launch(intent)
                        } catch (_: Exception) {
                            // Fallback gracefully if no speech engine installed on device
                        }
                    }
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Input", tint = GalaxyGold)
                }

                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    placeholder = { Text("Ask any question in Hindi or English...") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input")
                )

                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (promptText.isNotBlank()) {
                            onAskAi(promptText, selectedSubject, academicLevel, languageMode)
                            promptText = ""
                        }
                    },
                    modifier = Modifier.testTag("ai_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send to AI", tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun AiResponseCard(
    text: String,
    isStreaming: Boolean,
    onSpeak: () -> Unit
) {
    GlassCard(borderColor = NeonCyan.copy(alpha = 0.7f), glowColor = NeonCyan.copy(alpha = 0.16f)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isStreaming) "EduGalaxy AI (Streaming...)" else "EduGalaxy AI", style = MaterialTheme.typography.labelLarge, color = NeonCyan)
                }
                IconButton(onClick = onSpeak) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Read Aloud", tint = GalaxyGold)
                }
            }
            Text(text, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}
