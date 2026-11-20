package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.core.configureAppCheck
import com.example.data.model.EducationCatalog
import com.example.data.model.SubjectMetadata
import com.example.data.repository.EduAiRepository
import com.example.data.repository.EduFirestoreRepository
import com.example.data.repository.OfflinePdfManager
import com.example.ui.EduGalaxyViewModel
import com.example.ui.UiState
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.components.GalaxyStarfieldBackground
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.AllSubjectsScreen
import com.example.ui.screens.ClassSelectionScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.MockTestScreen
import com.example.ui.screens.MoreHubScreen
import com.example.ui.screens.PdfLibraryScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ProgressAndLeaderboardScreen
import com.example.ui.screens.QuestionsBankScreen
import com.example.ui.screens.SubjectDashboardScreen
import com.example.ui.screens.UniversityDirectoryScreen
import com.example.ui.screens.VideoLearningScreen
import com.example.ui.theme.EduGalaxyTheme
import com.example.ui.theme.GalaxyDeepNavy
import com.example.ui.theme.GalaxyMidnight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mandatory App Check initialization BEFORE any Compose UI, ViewModel, or Firebase access
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            EduGalaxyTheme {
                AppNavigation()
            }
        }
    }
}

private enum class MainTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
    Videos("Videos", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle, "nav_tab_videos"),
    Tests("Tests", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_tab_tests"),
    Downloads("Downloads", Icons.Filled.Download, Icons.Outlined.Download, "nav_tab_downloads"),
    More("More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, "nav_tab_more")
}

private sealed interface SubRoute {
    data class ClassSelection(val initialTierId: String? = null) : SubRoute
    data object AllSubjects : SubRoute
    data class SubjectDashboard(val subject: SubjectMetadata) : SubRoute
    data object GlobalSearch : SubRoute
    data class QuestionsBank(val subjectFilter: String? = null) : SubRoute
    data class AiTutor(val subject: String? = null, val initialPrompt: String? = null) : SubRoute
    data object ProgressLeaderboard : SubRoute
    data object UniversityDirectory : SubRoute
    data object ProfileSettings : SubRoute
    data object AdminDashboard : SubRoute
}

private fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
private fun AppNavigation() {
    val authFlow = remember { Firebase.auth.authStateFlow() }
    val currentUser by authFlow.collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {
            // FirebaseAuth.AuthStateListener emits the signed-in user automatically.
        })
    } else {
        key(user.uid) {
            AuthenticatedEduGalaxyShell(
                uid = user.uid,
                userEmail = user.email.orEmpty()
            )
        }
    }
}

@Composable
private fun AuthenticatedEduGalaxyShell(
    uid: String,
    userEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val viewModel: EduGalaxyViewModel = viewModel(
        key = uid,
        factory = viewModelFactory {
            initializer {
                val appContext = context.applicationContext
                EduGalaxyViewModel(
                    firestoreRepo = EduFirestoreRepository(appContext),
                    aiRepo = EduAiRepository(),
                    pdfManager = OfflinePdfManager(appContext),
                    currentUserId = uid
                )
            }
        }
    )

    val profileState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val bookmarksState by viewModel.bookmarksState.collectAsStateWithLifecycle()
    val downloadsState by viewModel.downloadsState.collectAsStateWithLifecycle()
    val attemptsState by viewModel.testAttemptsState.collectAsStateWithLifecycle()
    val progressState by viewModel.chapterProgressState.collectAsStateWithLifecycle()
    val aiSessionsState by viewModel.aiSessionsState.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()

    val questions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val videos by viewModel.allVideos.collectAsStateWithLifecycle()
    val pdfs by viewModel.allPdfs.collectAsStateWithLifecycle()
    val mockTests by viewModel.allMockTests.collectAsStateWithLifecycle()
    val universities by viewModel.allUniversities.collectAsStateWithLifecycle()
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val adminReports by viewModel.adminReports.collectAsStateWithLifecycle()
    val adminAuditLogs by viewModel.adminAuditLogs.collectAsStateWithLifecycle()

    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val recentlyViewedPdfIds by viewModel.recentlyViewedPdfIds.collectAsStateWithLifecycle()
    val aiChatState by viewModel.aiChatState.collectAsStateWithLifecycle()
    val aiQuestionGenLoading by viewModel.aiQuestionGenLoading.collectAsStateWithLifecycle()
    val activeExam by viewModel.activeExam.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data
    val bookmarks = (bookmarksState as? UiState.Success)?.data.orEmpty()
    val downloads = (downloadsState as? UiState.Success)?.data.orEmpty()
    val attempts = (attemptsState as? UiState.Success)?.data.orEmpty()
    val chapterProgress = (progressState as? UiState.Success)?.data.orEmpty()
    val aiSessions = (aiSessionsState as? UiState.Success)?.data.orEmpty()
    val leaderboard = (leaderboardState as? UiState.Success)?.data.orEmpty()

    val reducedMotion = profile?.reducedMotion ?: false
    val academicLevel = profile?.selectedLevel?.ifBlank { "Class 10" } ?: "Class 10"
    val preferredLanguage = profile?.preferredLanguage?.ifBlank { "Bilingual" } ?: "Bilingual"
    val isAdmin = profile?.role == "admin" ||
        userEmail.equals("prabalkumar7750@gmail.com", ignoreCase = true)

    var selectedTab by remember { mutableStateOf(MainTab.Home) }
    var tabSubjectFilter by remember { mutableStateOf<String?>(null) }
    val subStack = remember { mutableStateListOf<SubRoute>() }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearStatusBanner()
        }
    }

    val currentSubRoute = subStack.lastOrNull()
    val hideNavChrome = currentSubRoute != null || activeExam != null

    if (currentSubRoute == null && selectedTab != MainTab.Home && activeExam == null) {
        BackHandler {
            selectedTab = MainTab.Home
            tabSubjectFilter = null
        }
    }

    fun pushRoute(route: SubRoute) {
        subStack.add(route)
    }

    fun popRoute() {
        if (subStack.isNotEmpty()) {
            subStack.removeAt(subStack.lastIndex)
        }
    }

    GalaxyStarfieldBackground(reducedMotion = reducedMotion) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp

            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Color(0xFF142152),
                            contentColor = TextPrimary,
                            actionColor = NeonCyan,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                },
                bottomBar = {
                    if (!useNavigationRail && !hideNavChrome) {
                        NavigationBar(
                            containerColor = GalaxyMidnight.copy(alpha = 0.94f),
                            contentColor = TextPrimary
                        ) {
                            MainTab.entries.forEach { tab ->
                                val selected = selectedTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        selectedTab = tab
                                        tabSubjectFilter = null
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan.copy(alpha = 0.16f),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag(tab.tag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavigationRail && !hideNavChrome) {
                        NavigationRail(
                            containerColor = GalaxyMidnight.copy(alpha = 0.90f),
                            contentColor = TextPrimary,
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(GalaxyDeepNavy.copy(alpha = 0.45f))
                        ) {
                            Spacer(Modifier.weight(0.2f))
                            MainTab.entries.forEach { tab ->
                                val selected = selectedTab == tab
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = {
                                        selectedTab = tab
                                        tabSubjectFilter = null
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = NeonCyan,
                                        selectedTextColor = NeonCyan,
                                        indicatorColor = NeonCyan.copy(alpha = 0.18f),
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    ),
                                    modifier = Modifier.testTag(tab.tag)
                                )
                            }
                            Spacer(Modifier.weight(1f))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 980.dp)
                        ) {
                            if (currentSubRoute != null) {
                                when (currentSubRoute) {
                                    is SubRoute.ClassSelection -> ClassSelectionScreen(
                                        currentLevel = academicLevel,
                                        currentStream = profile?.selectedStream.orEmpty(),
                                        initialTierId = currentSubRoute.initialTierId,
                                        onSelectAcademicPath = { level, stream, course, univ ->
                                            viewModel.updateAcademicSelection(level, stream, course, univ)
                                        },
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.AllSubjects -> AllSubjectsScreen(
                                        onOpenSubject = { subject -> pushRoute(SubRoute.SubjectDashboard(subject)) },
                                        onBack = { popRoute() }
                                    )

                                    is SubRoute.SubjectDashboard -> SubjectDashboardScreen(
                                        subject = currentSubRoute.subject,
                                        questions = questions,
                                        videos = videos,
                                        pdfs = pdfs,
                                        progressList = chapterProgress,
                                        onOpenQuestionsForSubject = { subName ->
                                            pushRoute(SubRoute.QuestionsBank(subName))
                                        },
                                        onOpenVideosForSubject = { subName ->
                                            subStack.clear()
                                            tabSubjectFilter = subName
                                            selectedTab = MainTab.Videos
                                        },
                                        onOpenPdfsForSubject = { subName ->
                                            subStack.clear()
                                            tabSubjectFilter = subName
                                            selectedTab = MainTab.Downloads
                                        },
                                        onOpenTestsForSubject = { subName ->
                                            subStack.clear()
                                            tabSubjectFilter = subName
                                            selectedTab = MainTab.Tests
                                        },
                                        onAskAiForSubject = { subName ->
                                            pushRoute(SubRoute.AiTutor(subject = subName))
                                        },
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.GlobalSearch -> GlobalSearchScreen(
                                        recentSearches = recentSearches,
                                        questions = questions,
                                        videos = videos,
                                        pdfs = pdfs,
                                        mockTests = mockTests,
                                        universities = universities,
                                        onRecordSearch = { viewModel.recordSearchQuery(it) },
                                        onOpenSubject = { pushRoute(SubRoute.SubjectDashboard(it)) },
                                        onOpenQuestions = { pushRoute(SubRoute.QuestionsBank()) },
                                        onOpenVideos = {
                                            subStack.clear()
                                            selectedTab = MainTab.Videos
                                        },
                                        onOpenDownloads = {
                                            subStack.clear()
                                            selectedTab = MainTab.Downloads
                                        },
                                        onOpenTests = {
                                            subStack.clear()
                                            selectedTab = MainTab.Tests
                                        },
                                        onOpenUniversities = { pushRoute(SubRoute.UniversityDirectory) },
                                        onBack = { popRoute() }
                                    )

                                    is SubRoute.QuestionsBank -> QuestionsBankScreen(
                                        questions = questions,
                                        bookmarks = bookmarks,
                                        initialSubject = currentSubRoute.subjectFilter,
                                        academicLevel = academicLevel,
                                        preferredLanguage = preferredLanguage,
                                        isGeneratingAiQuestion = aiQuestionGenLoading,
                                        onPracticeAnswered = { q -> viewModel.recordQuestionPracticed(q) },
                                        onToggleBookmark = { q ->
                                            viewModel.toggleBookmark(
                                                itemType = "question",
                                                itemId = q.id,
                                                title = q.statement,
                                                subject = q.subject,
                                                subtitle = q.chapter
                                            )
                                        },
                                        onGenerateAiQuestion = { subject, level, chapter, difficulty ->
                                            viewModel.generateAiPracticeQuestion(subject, level, chapter, difficulty)
                                        },
                                        onAskAiExplain = { q ->
                                            pushRoute(
                                                SubRoute.AiTutor(
                                                    subject = q.subject,
                                                    initialPrompt = "Explain step-by-step in simple terms: ${q.statement}"
                                                )
                                            )
                                        },
                                        onReportQuestion = { q, reason ->
                                            viewModel.submitContentReport("question", q.id, reason)
                                        },
                                        onBack = { popRoute() }
                                    )

                                    is SubRoute.AiTutor -> AiTutorScreen(
                                        aiState = aiChatState,
                                        pastSessions = aiSessions,
                                        academicLevel = academicLevel,
                                        initialSubject = currentSubRoute.subject,
                                        initialPrompt = currentSubRoute.initialPrompt,
                                        preferredLanguage = preferredLanguage,
                                        onAskAi = { prompt, subject, level, language ->
                                            viewModel.askEduGalaxyAi(prompt, subject, level, language)
                                        },
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.ProgressLeaderboard -> ProgressAndLeaderboardScreen(
                                        profile = profile,
                                        progressList = chapterProgress,
                                        testAttempts = attempts,
                                        leaderboard = leaderboard,
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.UniversityDirectory -> UniversityDirectoryScreen(
                                        universities = universities,
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.ProfileSettings -> ProfileSettingsScreen(
                                        profile = profile,
                                        bookmarks = bookmarks,
                                        onUpdatePreferences = { lang, goal, motion ->
                                            viewModel.updatePreferences(lang, goal, motion)
                                        },
                                        onChangeClass = { pushRoute(SubRoute.ClassSelection()) },
                                        onSignOut = {
                                            signOut(credentialManager, scope)
                                        },
                                        onBack = { popRoute() }
                                    )

                                    SubRoute.AdminDashboard -> AdminDashboardScreen(
                                        questions = questions,
                                        reports = adminReports,
                                        auditLogs = adminAuditLogs,
                                        onSaveQuestion = { viewModel.adminSaveQuestion(it) },
                                        onDeleteQuestion = { viewModel.adminDeleteQuestion(it) },
                                        onPublishVideo = { viewModel.adminPublishVideo(it) },
                                        onPublishPdf = { viewModel.adminPublishPdf(it) },
                                        onPublishTest = { viewModel.adminPublishTest(it) },
                                        onBroadcastAnnouncement = { title, msg, cat ->
                                            viewModel.adminBroadcastAnnouncement(title, msg, cat)
                                        },
                                        onResolveReport = { id, status ->
                                            viewModel.adminResolveReport(id, status)
                                        },
                                        onBack = { popRoute() }
                                    )
                                }
                            } else {
                                when (selectedTab) {
                                    MainTab.Home -> HomeDashboardScreen(
                                        profile = profile,
                                        announcements = announcements,
                                        reducedMotion = reducedMotion,
                                        onOpenSearch = { pushRoute(SubRoute.GlobalSearch) },
                                        onOpenClassSelection = { pushRoute(SubRoute.ClassSelection()) },
                                        onSelectTier = { tier -> pushRoute(SubRoute.ClassSelection(tier.id)) },
                                        onOpenSubjectDashboard = { subject -> pushRoute(SubRoute.SubjectDashboard(subject)) },
                                        onOpenAllSubjects = { pushRoute(SubRoute.AllSubjects) },
                                        onOpenVideosTab = { selectedTab = MainTab.Videos },
                                        onOpenQuestions = { sub -> pushRoute(SubRoute.QuestionsBank(sub)) },
                                        onOpenTestsTab = { selectedTab = MainTab.Tests },
                                        onOpenDownloadsTab = { selectedTab = MainTab.Downloads },
                                        onOpenAiTutor = { pushRoute(SubRoute.AiTutor()) },
                                        onOpenProgressDashboard = { pushRoute(SubRoute.ProgressLeaderboard) },
                                        onOpenUniversityDirectory = { pushRoute(SubRoute.UniversityDirectory) },
                                        onOpenProfile = { pushRoute(SubRoute.ProfileSettings) }
                                    )

                                    MainTab.Videos -> VideoLearningScreen(
                                        videos = videos,
                                        bookmarks = bookmarks,
                                        initialSubject = tabSubjectFilter,
                                        preferredLanguage = preferredLanguage,
                                        reducedMotion = reducedMotion,
                                        onToggleBookmark = { lesson ->
                                            viewModel.toggleBookmark(
                                                itemType = "video",
                                                itemId = lesson.id,
                                                title = lesson.title,
                                                subject = lesson.subject,
                                                subtitle = lesson.lessonMode
                                            )
                                        },
                                        onMarkCompleted = { lesson -> viewModel.markVideoCompleted(lesson) }
                                    )

                                    MainTab.Tests -> MockTestScreen(
                                        mockTests = mockTests,
                                        pastAttempts = attempts,
                                        activeSession = activeExam,
                                        initialSubject = tabSubjectFilter,
                                        preferredLanguage = preferredLanguage,
                                        onStartTest = { viewModel.startMockTest(it) },
                                        onSelectAnswer = { idx, ans -> viewModel.selectExamAnswer(idx, ans) },
                                        onNavigateQuestion = { viewModel.navigateExamQuestion(it) },
                                        onToggleReview = { viewModel.toggleMarkForReview(it) },
                                        onSubmitTest = { viewModel.submitMockTest() },
                                        onExitSession = { viewModel.exitExamSession() }
                                    )

                                    MainTab.Downloads -> PdfLibraryScreen(
                                        pdfs = pdfs,
                                        downloads = downloads,
                                        bookmarks = bookmarks,
                                        recentlyViewedIds = recentlyViewedPdfIds,
                                        initialSubject = tabSubjectFilter,
                                        onViewedPdf = { viewModel.markPdfViewed(it) },
                                        onDownloadOffline = { viewModel.downloadPdfOffline(it) },
                                        onDeleteDownload = { viewModel.deleteOfflineDownload(it) },
                                        onToggleBookmark = { pdf ->
                                            viewModel.toggleBookmark(
                                                itemType = "pdf",
                                                itemId = pdf.id,
                                                title = pdf.title,
                                                subject = pdf.subject,
                                                subtitle = pdf.category
                                            )
                                        },
                                        onReadOfflineFile = { fileName -> viewModel.readOfflineFile(fileName) }
                                    )

                                    MainTab.More -> MoreHubScreen(
                                        profile = profile,
                                        isAdmin = isAdmin,
                                        onOpenAiTutor = { pushRoute(SubRoute.AiTutor()) },
                                        onOpenQuestions = { pushRoute(SubRoute.QuestionsBank()) },
                                        onOpenProgress = { pushRoute(SubRoute.ProgressLeaderboard) },
                                        onOpenUniversities = { pushRoute(SubRoute.UniversityDirectory) },
                                        onOpenClasses = { pushRoute(SubRoute.ClassSelection()) },
                                        onOpenProfile = { pushRoute(SubRoute.ProfileSettings) },
                                        onOpenAdmin = { pushRoute(SubRoute.AdminDashboard) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
