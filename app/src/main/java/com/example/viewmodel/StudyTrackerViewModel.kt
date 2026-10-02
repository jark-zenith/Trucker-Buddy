package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.StudyTrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

val DAYS_OF_WEEK = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

class StudyTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyTrackerRepository(application)
    val uiState: StateFlow<AppState> = repository.state
    val syncStatus: StateFlow<String> = repository.syncStatus

    // Focus Lab State
    private val _focusRemaining = MutableStateFlow(25 * 60)
    val focusRemaining: StateFlow<Int> = _focusRemaining.asStateFlow()

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private var focusJob: Job? = null

    // Toast message trigger
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun getActiveSemester(): Semester {
        val state = uiState.value
        return state.semesters.find { it.id == state.activeSemesterId }
            ?: state.semesters.firstOrNull()
            ?: Semester(name = "Active Semester", year = "2026", start = "2026-01-01", end = "2026-12-31")
    }

    fun getActiveUnits(): List<UnitItem> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.units.filter { it.semesterId == sId }
    }

    fun getActiveLessons(): List<Lesson> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.lessons.filter { it.semesterId == sId }
    }

    fun getActiveTasks(): List<TaskItem> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.tasks.filter { it.semesterId == sId }
    }

    fun getActiveAttendance(): List<AttendanceRecord> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.attendance.filter { it.semesterId == sId }
    }

    fun getActiveSessions(): List<StudySession> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.sessions.filter { it.semesterId == sId }
    }

    fun getActiveEvents(): List<CalendarEvent> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.events.filter { it.semesterId == sId }
    }

    fun getActiveGrades(): List<GradeRecord> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.grades.filter { it.semesterId == sId }
    }

    fun getActiveNotes(): List<NoteItem> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.notes.filter { it.semesterId == sId }
    }

    fun getActiveResources(): List<ResourceItem> {
        val sId = uiState.value.activeSemesterId
        return uiState.value.resources.filter { it.semesterId == sId }
    }

    fun getUnitName(unitId: String): String {
        return uiState.value.units.find { it.id == unitId }?.name ?: "General"
    }

    fun getWeeklyMinutes(): Int {
        val sId = uiState.value.activeSemesterId
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val cutoff = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        return uiState.value.sessions
            .filter { it.semesterId == sId && it.date >= cutoff }
            .sumOf { it.minutes }
    }

    fun getNextLesson(): Lesson? {
        val lessons = getActiveLessons()
        if (lessons.isEmpty()) return null

        val now = Calendar.getInstance()
        val dayName = SimpleDateFormat("EEEE", Locale.US).format(now.time)
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val parsedLessons = lessons.map { l ->
            val parts = l.start.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val startMinutes = h * 60 + m
            l to startMinutes
        }.sortedBy { it.second }

        // Find later today
        val todayUpcoming = parsedLessons.firstOrNull { it.first.day.equals(dayName, ignoreCase = true) && it.second > currentMinutes }
        if (todayUpcoming != null) return todayUpcoming.first

        // Find upcoming day this week
        val currentDayIdx = DAYS_OF_WEEK.indexOfFirst { it.equals(dayName, ignoreCase = true) }
        val nextDayLesson = parsedLessons.firstOrNull {
            val lessonDayIdx = DAYS_OF_WEEK.indexOfFirst { d -> d.equals(it.first.day, ignoreCase = true) }
            lessonDayIdx > currentDayIdx
        }
        if (nextDayLesson != null) return nextDayLesson.first

        return parsedLessons.firstOrNull()?.first
    }

    // Active Semester & Theme
    fun setActiveSemester(semId: String) {
        viewModelScope.launch {
            repository.updateState(uiState.value.copy(activeSemesterId = semId))
            showMessage("Active semester changed")
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme = if (uiState.value.theme == "dark") "light" else "dark"
            repository.updateState(uiState.value.copy(theme = newTheme))
        }
    }

    // Units CRUD
    fun saveUnit(unit: UnitItem) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.units.indexOfFirst { it.id == unit.id }
            val updatedList = state.units.toMutableList()
            if (existing >= 0) {
                updatedList[existing] = unit
            } else {
                updatedList.add(unit)
            }
            repository.updateState(state.copy(units = updatedList))
            showMessage("Unit saved")
        }
    }

    fun deleteUnit(unitId: String) {
        viewModelScope.launch {
            val state = uiState.value
            val updatedUnits = state.units.filter { it.id != unitId }
            val updatedLessons = state.lessons.filter { it.unitId != unitId }
            repository.updateState(state.copy(units = updatedUnits, lessons = updatedLessons))
            showMessage("Unit deleted")
        }
    }

    // Lessons CRUD
    fun saveLesson(lesson: Lesson) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.lessons.indexOfFirst { it.id == lesson.id }
            val updatedList = state.lessons.toMutableList()
            if (existing >= 0) {
                updatedList[existing] = lesson
            } else {
                updatedList.add(lesson)
            }
            repository.updateState(state.copy(lessons = updatedList))
            showMessage("Timetable updated")
        }
    }

    fun deleteLesson(lessonId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(lessons = state.lessons.filter { it.id != lessonId }))
            showMessage("Lesson deleted")
        }
    }

    // Tasks CRUD
    fun saveTask(task: TaskItem) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.tasks.indexOfFirst { it.id == task.id }
            val updatedList = state.tasks.toMutableList()
            if (existing >= 0) {
                updatedList[existing] = task
            } else {
                updatedList.add(task)
            }
            repository.updateState(state.copy(tasks = updatedList))
            showMessage("Task saved")
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            val state = uiState.value
            val updated = state.tasks.map {
                if (it.id == taskId) it.copy(status = if (it.status == "done") "open" else "done") else it
            }
            repository.updateState(state.copy(tasks = updated))
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(tasks = state.tasks.filter { it.id != taskId }))
            showMessage("Task deleted")
        }
    }

    // Attendance
    fun markAttendance(unitId: String, status: String) {
        viewModelScope.launch {
            val state = uiState.value
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val record = AttendanceRecord(
                semesterId = state.activeSemesterId,
                unitId = unitId,
                date = todayStr,
                status = status
            )
            repository.updateState(state.copy(attendance = state.attendance + record))
            showMessage("Attendance saved")
        }
    }

    // Grades CRUD
    fun saveGrade(grade: GradeRecord) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.grades.indexOfFirst { it.id == grade.id }
            val updated = state.grades.toMutableList()
            if (existing >= 0) updated[existing] = grade else updated.add(grade)
            repository.updateState(state.copy(grades = updated))
            showMessage("Grade saved")
        }
    }

    fun deleteGrade(gradeId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(grades = state.grades.filter { it.id != gradeId }))
            showMessage("Grade deleted")
        }
    }

    // Study Sessions CRUD
    fun saveSession(session: StudySession) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.sessions.indexOfFirst { it.id == session.id }
            val updated = state.sessions.toMutableList()
            if (existing >= 0) updated[existing] = session else updated.add(session)
            repository.updateState(state.copy(sessions = updated))
            showMessage("Study session logged")
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(sessions = state.sessions.filter { it.id != sessionId }))
            showMessage("Session deleted")
        }
    }

    // Focus Lab
    fun setFocusDuration(minutes: Int) {
        if (_isFocusRunning.value) return
        _focusRemaining.value = minutes * 60
    }

    fun toggleFocusTimer() {
        if (_isFocusRunning.value) {
            pauseFocusTimer()
        } else {
            startFocusTimer()
        }
    }

    private fun startFocusTimer() {
        _isFocusRunning.value = true
        focusJob?.cancel()
        focusJob = viewModelScope.launch {
            while (_focusRemaining.value > 0) {
                delay(1000)
                _focusRemaining.value = _focusRemaining.value - 1
            }
            _isFocusRunning.value = false
            triggerVibration()
            showMessage("Focus block complete! Great work.")
            _focusRemaining.value = 25 * 60
        }
    }

    private fun pauseFocusTimer() {
        _isFocusRunning.value = false
        focusJob?.cancel()
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        _focusRemaining.value = 25 * 60
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Notes CRUD
    fun saveNote(note: NoteItem) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.notes.indexOfFirst { it.id == note.id }
            val updated = state.notes.toMutableList()
            val noteWithDate = if (note.updated.isBlank()) {
                note.copy(updated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            } else note

            if (existing >= 0) updated[existing] = noteWithDate else updated.add(noteWithDate)
            repository.updateState(state.copy(notes = updated))
            showMessage("Note saved")
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(notes = state.notes.filter { it.id != noteId }))
            showMessage("Note deleted")
        }
    }

    // Resources CRUD
    fun saveResource(res: ResourceItem) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.resources.indexOfFirst { it.id == res.id }
            val updated = state.resources.toMutableList()
            if (existing >= 0) updated[existing] = res else updated.add(res)
            repository.updateState(state.copy(resources = updated))
            showMessage("Resource saved")
        }
    }

    fun deleteResource(resId: String) {
        viewModelScope.launch {
            val state = uiState.value
            repository.updateState(state.copy(resources = state.resources.filter { it.id != resId }))
            showMessage("Resource deleted")
        }
    }

    // Calendar Events CRUD
    fun saveEvent(event: CalendarEvent) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.events.indexOfFirst { it.id == event.id }
            val updated = state.events.toMutableList()
            if (existing >= 0) updated[existing] = event else updated.add(event)
            repository.updateState(state.copy(events = updated))
            showMessage("Event saved")
        }
    }

    // Semesters CRUD
    fun saveSemester(sem: Semester) {
        viewModelScope.launch {
            val state = uiState.value
            val existing = state.semesters.indexOfFirst { it.id == sem.id }
            val updated = state.semesters.toMutableList()
            if (existing >= 0) updated[existing] = sem else updated.add(sem)
            repository.updateState(state.copy(semesters = updated, activeSemesterId = sem.id))
            showMessage("Semester created and activated")
        }
    }

    // Settings
    fun saveSettings(
        name: String,
        institution: String,
        program: String,
        weeklyMinutes: Int,
        semesterTargetMinutes: Int,
        syncServerUrl: String,
        syncKey: String,
        theme: String
    ) {
        viewModelScope.launch {
            val state = uiState.value
            val updated = state.copy(
                theme = theme,
                profile = state.profile.copy(
                    name = name,
                    institution = institution,
                    program = program
                ),
                goals = state.goals.copy(
                    weeklyMinutes = weeklyMinutes,
                    semesterTargetMinutes = semesterTargetMinutes
                ),
                preferences = state.preferences.copy(
                    syncServerUrl = syncServerUrl,
                    syncKey = syncKey,
                    syncEnabled = syncServerUrl.isNotBlank()
                )
            )
            repository.updateState(updated)
            showMessage("Settings saved")
        }
    }

    fun pullServerState() {
        viewModelScope.launch {
            val success = repository.pullFromServer()
            if (success) showMessage("Server state loaded successfully")
            else showMessage("Could not pull server state")
        }
    }

    fun syncToServer() {
        viewModelScope.launch {
            val success = repository.syncToServer()
            if (success) showMessage("Synced to server")
            else showMessage("Sync failed")
        }
    }

    fun exportBackupJson(): String {
        return repository.exportBackupJson()
    }

    fun importBackupJson(json: String) {
        viewModelScope.launch {
            val success = repository.importBackupJson(json)
            if (success) showMessage("Backup successfully imported")
            else showMessage("Failed to import backup")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            showMessage("All data reset to defaults")
        }
    }
}
