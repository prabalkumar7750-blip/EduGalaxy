package com.example.data.repository

import android.content.Context
import android.content.Intent
import com.example.data.model.PdfMaterial
import java.io.File

class OfflinePdfManager(private val context: Context) {

    private val notesDir: File
        get() = File(context.filesDir, "edugalaxy_notes").apply {
            if (!exists()) mkdirs()
        }

    fun saveMaterialOffline(pdf: PdfMaterial): Pair<String, Long> {
        val safeName = "${pdf.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")}.txt"
        val file = File(notesDir, safeName)
        val content = buildString {
            appendLine("====================================================")
            appendLine("EDUGALAXY OFFLINE STUDY MATERIAL")
            appendLine("Title: ${pdf.title}")
            appendLine("Category: ${pdf.category} | Subject: ${pdf.subject}")
            appendLine("Level: ${pdf.academicLevel} | Chapter: ${pdf.chapter}")
            appendLine("Source: ${pdf.source}")
            appendLine("====================================================")
            appendLine()
            appendLine("SUMMARY:")
            appendLine(pdf.summary)
            appendLine()
            appendLine("STUDY SECTIONS:")
            pdf.contentSections.forEachIndexed { idx, section ->
                appendLine("${idx + 1}. $section")
                appendLine()
            }
        }
        file.writeText(content)
        val sizeKb = (file.length() / 1024L).coerceAtLeast(1L)
        return safeName to sizeKb
    }

    fun readOfflineMaterial(fileName: String): String? {
        val file = File(notesDir, fileName)
        return if (file.exists()) file.readText() else null
    }

    fun deleteOfflineMaterial(fileName: String): Boolean {
        val file = File(notesDir, fileName)
        return if (file.exists()) file.delete() else false
    }

    fun shareMaterialIntent(pdf: PdfMaterial): Intent {
        val shareText = buildString {
            appendLine("EduGalaxy Study Material: ${pdf.title}")
            appendLine("${pdf.subject} • ${pdf.academicLevel} • ${pdf.category}")
            appendLine()
            appendLine(pdf.summary)
            appendLine()
            pdf.contentSections.forEach { appendLine("• $it") }
        }
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, pdf.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }
}
