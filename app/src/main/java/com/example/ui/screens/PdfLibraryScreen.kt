package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.EducationCatalog
import com.example.data.model.PdfMaterial
import com.example.data.model.UserBookmark
import com.example.data.model.UserDownload
import com.example.data.repository.OfflinePdfManager
import com.example.ui.components.EmptyGalaxyState
import com.example.ui.components.GlassCard
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PdfLibraryScreen(
    pdfs: List<PdfMaterial>,
    downloads: List<UserDownload>,
    bookmarks: List<UserBookmark>,
    recentlyViewedIds: List<String>,
    initialSubject: String? = null,
    onViewedPdf: (PdfMaterial) -> Unit,
    onDownloadOffline: (PdfMaterial) -> Unit,
    onDeleteDownload: (UserDownload) -> Unit,
    onToggleBookmark: (PdfMaterial) -> Unit,
    onReadOfflineFile: (String) -> String?
) {
    val context = LocalContext.current
    val pdfManager = remember { OfflinePdfManager(context) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Library, 1 = Offline Downloads, 2 = Bookmarks
    var searchQuery by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf("All") }
    var viewingPdf by remember { mutableStateOf<PdfMaterial?>(null) }
    var viewingOfflineContent by remember { mutableStateOf<Pair<String, String>?>(null) }

    val downloadedIds = remember(downloads) { downloads.map { it.pdfId }.toSet() }
    val bookmarkedIds = remember(bookmarks) {
        bookmarks.filter { it.itemType == "pdf" }.map { it.itemId }.toSet()
    }

    val filteredPdfs = remember(pdfs, searchQuery, categoryFilter, initialSubject) {
        pdfs.filter { p ->
            (categoryFilter == "All" || p.category.equals(categoryFilter, true)) &&
                (initialSubject == null || p.subject.equals(initialSubject, true)) &&
                (searchQuery.isBlank() || p.title.contains(searchQuery, true) || p.subject.contains(searchQuery, true) || p.summary.contains(searchQuery, true))
        }
    }

    if (viewingPdf != null) {
        val pdf = viewingPdf!!
        var inDocSearch by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewingPdf = null },
            containerColor = Color(0xFF0E1538),
            title = { Text(pdf.title, color = TextPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("${pdf.category} • ${pdf.subject} • ${pdf.pagesCount} pages", style = MaterialTheme.typography.labelSmall, color = GalaxyGold)
                    Text("Source: ${pdf.source}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    OutlinedTextField(
                        value = inDocSearch,
                        onValueChange = { inDocSearch = it },
                        label = { Text("Search inside document") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(pdf.summary, style = MaterialTheme.typography.bodyLarge, color = NeonCyan)
                    pdf.contentSections.filter { inDocSearch.isBlank() || it.contains(inDocSearch, true) }
                        .forEach { section ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.05f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(section, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.padding(10.dp))
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { onDownloadOffline(pdf); viewingPdf = null }) {
                    Text("Save Offline", color = NeonGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewingPdf = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    if (viewingOfflineContent != null) {
        AlertDialog(
            onDismissRequest = { viewingOfflineContent = null },
            containerColor = Color(0xFF0E1538),
            title = { Text(viewingOfflineContent!!.first, color = TextPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(viewingOfflineContent!!.second, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingOfflineContent = null }) { Text("Done", color = NeonCyan) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pdf_library_screen")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("Study PDFs & Offline Library", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Text("Authorized notes, formula sheets & offline device reader", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = NeonCyan) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Library (${pdfs.size})") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Offline (${downloads.size})") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Saved (${bookmarkedIds.size})") })
        }

        when (selectedTab) {
            0 -> {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search NCERT notes, formulas, DSA...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(EducationCatalog.pdfCategories) { cat ->
                            FilterChip(selected = categoryFilter == cat, onClick = { categoryFilter = cat }, label = { Text(cat) })
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (recentlyViewedIds.isNotEmpty()) {
                        item {
                            val recent = pdfs.filter { recentlyViewedIds.contains(it.id) }
                            if (recent.isNotEmpty()) {
                                Text("Recently Viewed", style = MaterialTheme.typography.titleMedium, color = GalaxyGold)
                                Spacer(Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(recent) { rp ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = GalaxyGold.copy(alpha = 0.14f),
                                            border = BorderStroke(1.dp, GalaxyGold.copy(alpha = 0.4f)),
                                            onClick = { viewingPdf = rp }
                                        ) {
                                            Text(rp.title, style = MaterialTheme.typography.labelLarge, color = TextPrimary, modifier = Modifier.padding(10.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(filteredPdfs, key = { it.id }) { pdf ->
                        val safeId = pdf.id.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
                        val isDownloaded = downloadedIds.contains(safeId)
                        val isBookmarked = bookmarkedIds.contains(safeId)

                        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonGreen.copy(alpha = 0.45f)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = NeonRose, modifier = Modifier.size(28.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(pdf.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                            Text("${pdf.category} • ${pdf.subject} • ${pdf.pagesCount} pages", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                                        }
                                    }
                                    IconButton(onClick = { onToggleBookmark(pdf) }) {
                                        Icon(
                                            if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = "Bookmark PDF",
                                            tint = if (isBookmarked) GalaxyGold else TextSecondary
                                        )
                                    }
                                }

                                Text(pdf.summary, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onViewedPdf(pdf); viewingPdf = pdf },
                                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                                    ) {
                                        Icon(Icons.Filled.Visibility, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Preview", color = NeonCyan)
                                    }
                                    Button(
                                        onClick = { onDownloadOffline(pdf) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDownloaded) NeonGreen.copy(alpha = 0.25f) else NeonGreen,
                                            contentColor = if (isDownloaded) NeonGreen else GalaxyDeepNavy
                                        )
                                    ) {
                                        Icon(if (isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(if (isDownloaded) "Saved" else "Download", fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = {
                                            val shareIntent = pdfManager.shareMaterialIntent(pdf)
                                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Study Material"))
                                        }
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "Share PDF", tint = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (downloads.isEmpty()) {
                        item {
                            EmptyGalaxyState(
                                title = "No offline files yet",
                                subtitle = "Tap Download on any PDF or formula sheet in the Library tab to read offline anytime."
                            )
                        }
                    } else {
                        items(downloads, key = { it.id }) { dl ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = NeonGreen.copy(alpha = 0.6f),
                                onClick = {
                                    val text = onReadOfflineFile(dl.localFileName) ?: "Offline file not found on device. Re-download from Library."
                                    viewingOfflineContent = dl.title to text
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(dl.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                        Text("${dl.category} • ${dl.subject} • ${dl.sizeKb} KB Offline", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                                    }
                                    IconButton(onClick = { onDeleteDownload(dl) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete Offline File", tint = NeonRose)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                val savedList = bookmarks.filter { it.itemType == "pdf" }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (savedList.isEmpty()) {
                        item {
                            EmptyGalaxyState(
                                title = "No bookmarked PDFs",
                                subtitle = "Tap the bookmark icon on any PDF note to pin it here."
                            )
                        }
                    } else {
                        items(savedList, key = { it.id }) { bm ->
                            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GalaxyGold.copy(alpha = 0.55f)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(bm.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    Text("${bm.subject} • ${bm.subtitle}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
