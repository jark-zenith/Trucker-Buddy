package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.BrandAccent
import com.example.viewmodel.StudyTrackerViewModel
import kotlinx.coroutines.launch

enum class Screen(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Timetable("Timetable", Icons.Default.DateRange),
    Tasks("Tasks", Icons.Default.CheckCircle),
    Units("Units", Icons.Default.List),
    More("More", Icons.Default.MoreVert),
    Attendance("Attendance", Icons.Default.AccountBox),
    Grades("Grades", Icons.Default.Star),
    Sessions("Study Sessions", Icons.Default.PlayArrow),
    FocusLab("Focus Lab", Icons.Default.Notifications),
    Notes("Notes", Icons.Default.Edit),
    Resources("Resources", Icons.Default.Share),
    CalendarView("Calendar", Icons.Default.DateRange),
    Analytics("Analytics", Icons.Default.Info),
    Semesters("Semesters", Icons.Default.List),
    Settings("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTrackerApp(
    viewModel: StudyTrackerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val focusRemaining by viewModel.focusRemaining.collectAsStateWithLifecycle()
    val isFocusRunning by viewModel.isFocusRunning.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
    var semesterMenuExpanded by remember { mutableStateOf(false) }

    // Dialog States
    var showAddUnitDialog by remember { mutableStateOf(false) }
    var editingUnit by remember { mutableStateOf<UnitItem?>(null) }

    var showAddLessonDialog by remember { mutableStateOf(false) }
    var editingLesson by remember { mutableStateOf<Lesson?>(null) }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }

    var attendanceTargetUnit by remember { mutableStateOf<UnitItem?>(null) }
    var showAddGradeDialog by remember { mutableStateOf(false) }

    var showLogSessionDialog by remember { mutableStateOf(false) }
    var logSessionType by remember { mutableStateOf("Revision") }
    var logSessionMins by remember { mutableStateOf(45) }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<NoteItem?>(null) }

    var showAddResourceDialog by remember { mutableStateOf(false) }
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Back handler
    BackHandler(enabled = currentScreen != Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    val activeSemester = viewModel.getActiveSemester()
    val activeUnits = viewModel.getActiveUnits()
    val activeLessons = viewModel.getActiveLessons()
    val activeTasks = viewModel.getActiveTasks()
    val activeAttendance = viewModel.getActiveAttendance()
    val activeSessions = viewModel.getActiveSessions()
    val activeEvents = viewModel.getActiveEvents()
    val activeGrades = viewModel.getActiveGrades()
    val activeNotes = viewModel.getActiveNotes()
    val activeResources = viewModel.getActiveResources()
    val weeklyMins = viewModel.getWeeklyMinutes()
    val nextLesson = viewModel.getNextLesson()

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("app_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pruden "P" Mark
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D2A4B))
                                .border(1.dp, Color(0xFF356AA5), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 1.dp, y = (-1).dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = currentScreen.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = activeSemester.name,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != Screen.Dashboard) {
                        IconButton(
                            onClick = { currentScreen = Screen.Dashboard },
                            modifier = Modifier.testTag("top_bar_back_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    // Semester Selector Dropdown
                    Box {
                        TextButton(
                            onClick = { semesterMenuExpanded = true },
                            modifier = Modifier.testTag("semester_dropdown_btn")
                        ) {
                            Text(
                                text = activeSemester.name.take(9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandAccent
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BrandAccent)
                        }

                        DropdownMenu(
                            expanded = semesterMenuExpanded,
                            onDismissRequest = { semesterMenuExpanded = false }
                        ) {
                            uiState.semesters.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.name} (${s.year})") },
                                    onClick = {
                                        viewModel.setActiveSemester(s.id)
                                        semesterMenuExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ New Semester") },
                                onClick = {
                                    showAddSemesterDialog = true
                                    semesterMenuExpanded = false
                                }
                            )
                        }
                    }

                    // Theme Toggle
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Text(
                            text = "◐",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(Screen.Dashboard, Screen.Timetable, Screen.Tasks, Screen.Units, Screen.More)
                navItems.forEach { screen ->
                    val isSelected = if (screen == Screen.More) {
                        currentScreen in listOf(
                            Screen.More, Screen.Attendance, Screen.Grades, Screen.Sessions,
                            Screen.FocusLab, Screen.Notes, Screen.Resources, Screen.CalendarView,
                            Screen.Analytics, Screen.Semesters, Screen.Settings
                        )
                    } else {
                        currentScreen == screen
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandAccent,
                            selectedTextColor = BrandAccent,
                            indicatorColor = Color(0xFF142945)
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(
                    activeSemester = activeSemester,
                    units = activeUnits,
                    lessons = activeLessons,
                    tasks = activeTasks,
                    sessions = activeSessions,
                    goals = uiState.goals,
                    weeklyMinutes = weeklyMins,
                    nextLesson = nextLesson,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onNavigateToTimetable = { currentScreen = Screen.Timetable },
                    onNavigateToTasks = { currentScreen = Screen.Tasks },
                    onQuickTask = { showAddTaskDialog = true },
                    onEditLesson = {
                        editingLesson = it
                        showAddLessonDialog = true
                    },
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )

                Screen.Timetable -> TimetableScreen(
                    semesterName = activeSemester.name,
                    lessons = activeLessons,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onAddLesson = {
                        editingLesson = null
                        showAddLessonDialog = true
                    },
                    onEditLesson = {
                        editingLesson = it
                        showAddLessonDialog = true
                    }
                )

                Screen.Tasks -> TasksScreen(
                    tasks = activeTasks,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onAddTask = {
                        editingTask = null
                        showAddTaskDialog = true
                    },
                    onToggleTask = { viewModel.toggleTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )

                Screen.Units -> UnitsScreen(
                    units = activeUnits,
                    lessons = activeLessons,
                    sessions = activeSessions,
                    onAddUnit = {
                        editingUnit = null
                        showAddUnitDialog = true
                    },
                    onEditUnit = {
                        editingUnit = it
                        showAddUnitDialog = true
                    },
                    onDeleteUnit = { viewModel.deleteUnit(it) }
                )

                Screen.More -> MoreMenuScreen(
                    onSelect = { currentScreen = it }
                )

                Screen.Attendance -> AttendanceScreen(
                    units = activeUnits,
                    attendance = activeAttendance,
                    onMarkAttendance = { unit ->
                        attendanceTargetUnit = unit
                    }
                )

                Screen.Grades -> GradesScreen(
                    grades = activeGrades,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onAddGrade = { showAddGradeDialog = true },
                    onDeleteGrade = { viewModel.deleteGrade(it) }
                )

                Screen.Sessions -> SessionsScreen(
                    sessions = activeSessions,
                    weeklyMinutes = weeklyMins,
                    onLogSession = {
                        logSessionType = "Revision"
                        logSessionMins = 45
                        showLogSessionDialog = true
                    },
                    onDeleteSession = { viewModel.deleteSession(it) }
                )

                Screen.FocusLab -> FocusLabScreen(
                    remainingSeconds = focusRemaining,
                    isRunning = isFocusRunning,
                    sessions = activeSessions,
                    onToggle = { viewModel.toggleFocusTimer() },
                    onReset = { viewModel.resetFocusTimer() },
                    onSetDuration = { viewModel.setFocusDuration(it) },
                    onLogFocusBlock = {
                        logSessionType = "Focus"
                        logSessionMins = 25
                        showLogSessionDialog = true
                    }
                )

                Screen.Notes -> NotesScreen(
                    notes = activeNotes,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onAddNote = {
                        editingNote = null
                        showAddNoteDialog = true
                    },
                    onEditNote = {
                        editingNote = it
                        showAddNoteDialog = true
                    },
                    onDeleteNote = { viewModel.deleteNote(it) }
                )

                Screen.Resources -> ResourcesScreen(
                    resources = activeResources,
                    unitNameHelper = { viewModel.getUnitName(it) },
                    onAddResource = { showAddResourceDialog = true },
                    onDeleteResource = { viewModel.deleteResource(it) }
                )

                Screen.CalendarView -> CalendarScreen(
                    events = activeEvents,
                    tasks = activeTasks,
                    onAddEvent = { showAddEventDialog = true }
                )

                Screen.Analytics -> AnalyticsScreen(
                    units = activeUnits,
                    tasks = activeTasks,
                    attendance = activeAttendance,
                    sessions = activeSessions,
                    goals = uiState.goals
                )

                Screen.Semesters -> SemestersScreen(
                    semesters = uiState.semesters,
                    activeSemesterId = uiState.activeSemesterId,
                    onActivateSemester = { viewModel.setActiveSemester(it) },
                    onAddSemester = { showAddSemesterDialog = true }
                )

                Screen.Settings -> SettingsScreen(
                    profile = uiState.profile,
                    preferences = uiState.preferences,
                    goals = uiState.goals,
                    currentTheme = uiState.theme,
                    syncStatus = syncStatus,
                    onSaveSettings = { name, inst, prog, wMin, semMin, url, key, theme ->
                        viewModel.saveSettings(name, inst, prog, wMin, semMin, url, key, theme)
                    },
                    onPullServer = { viewModel.pullServerState() },
                    onSyncServer = { viewModel.syncToServer() },
                    onExportBackup = { viewModel.exportBackupJson() },
                    onImportBackup = { viewModel.importBackupJson(it) },
                    onResetAll = { viewModel.resetAllData() },
                    onShowMessage = { viewModel.showMessage(it) }
                )
            }
        }
    }

    // Dialogs
    if (showAddUnitDialog) {
        AddEditUnitDialog(
            initialUnit = editingUnit,
            semesterId = activeSemester.id,
            onDismiss = { showAddUnitDialog = false; editingUnit = null },
            onSave = { viewModel.saveUnit(it) },
            onDelete = if (editingUnit != null) { { viewModel.deleteUnit(editingUnit!!.id) } } else null
        )
    }

    if (showAddLessonDialog) {
        AddEditLessonDialog(
            initialLesson = editingLesson,
            semesterId = activeSemester.id,
            units = activeUnits,
            onDismiss = { showAddLessonDialog = false; editingLesson = null },
            onSave = { viewModel.saveLesson(it) },
            onDelete = if (editingLesson != null) { { viewModel.deleteLesson(editingLesson!!.id) } } else null
        )
    }

    if (showAddTaskDialog) {
        AddEditTaskDialog(
            initialTask = editingTask,
            semesterId = activeSemester.id,
            units = activeUnits,
            onDismiss = { showAddTaskDialog = false; editingTask = null },
            onSave = { viewModel.saveTask(it) }
        )
    }

    attendanceTargetUnit?.let { unit ->
        MarkAttendanceDialog(
            unit = unit,
            onDismiss = { attendanceTargetUnit = null },
            onMark = { status ->
                viewModel.markAttendance(unit.id, status)
            }
        )
    }

    if (showAddGradeDialog) {
        AddGradeDialog(
            semesterId = activeSemester.id,
            units = activeUnits,
            onDismiss = { showAddGradeDialog = false },
            onSave = { viewModel.saveGrade(it) }
        )
    }

    if (showLogSessionDialog) {
        LogSessionDialog(
            semesterId = activeSemester.id,
            initialType = logSessionType,
            initialMinutes = logSessionMins,
            onDismiss = { showLogSessionDialog = false },
            onSave = { viewModel.saveSession(it) }
        )
    }

    if (showAddNoteDialog) {
        AddEditNoteDialog(
            initialNote = editingNote,
            semesterId = activeSemester.id,
            units = activeUnits,
            onDismiss = { showAddNoteDialog = false; editingNote = null },
            onSave = { viewModel.saveNote(it) }
        )
    }

    if (showAddResourceDialog) {
        AddResourceDialog(
            semesterId = activeSemester.id,
            units = activeUnits,
            onDismiss = { showAddResourceDialog = false },
            onSave = { viewModel.saveResource(it) }
        )
    }

    if (showAddSemesterDialog) {
        AddSemesterDialog(
            onDismiss = { showAddSemesterDialog = false },
            onSave = { viewModel.saveSemester(it) }
        )
    }

    if (showAddEventDialog) {
        AddEventDialog(
            semesterId = activeSemester.id,
            onDismiss = { showAddEventDialog = false },
            onSave = { viewModel.saveEvent(it) }
        )
    }
}

@Composable
fun MoreMenuScreen(
    onSelect: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Attendance,
        Screen.Grades,
        Screen.Sessions,
        Screen.FocusLab,
        Screen.Notes,
        Screen.Resources,
        Screen.CalendarView,
        Screen.Analytics,
        Screen.Semesters,
        Screen.Settings
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("more_menu_screen")
    ) {
        Text(
            text = "Academic Tools & Sections",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { screen ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(screen) }
                        .testTag("more_item_${screen.name.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF142945)
                        ) {
                            Icon(
                                screen.icon,
                                contentDescription = null,
                                tint = Color(0xFF8FC0FF),
                                modifier = Modifier.padding(8.dp).size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = screen.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
