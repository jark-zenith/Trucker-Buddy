package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.viewmodel.DAYS_OF_WEEK
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    activeSemester: Semester,
    units: List<UnitItem>,
    lessons: List<Lesson>,
    tasks: List<TaskItem>,
    sessions: List<StudySession>,
    goals: Goals,
    weeklyMinutes: Int,
    nextLesson: Lesson?,
    unitNameHelper: (String) -> String,
    onNavigateToTimetable: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onQuickTask: () -> Unit,
    onEditLesson: (Lesson) -> Unit,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMinutes = sessions.sumOf { it.minutes }
    val totalHours = totalMinutes / 60
    val remMinutes = totalMinutes % 60
    val studyTimeString = if (totalHours > 0) "${totalHours}h ${remMinutes}m" else "${remMinutes}m"
    val openTasks = tasks.filter { it.status != "done" }

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val upcomingTasks = tasks
        .filter { it.status != "done" && it.due >= todayStr }
        .sortedBy { it.due }
        .take(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Hero
        item {
            HeroBanner(
                semesterName = activeSemester.name,
                dateRange = "${activeSemester.start} → ${activeSemester.end}",
                onQuickTask = onQuickTask
            )
        }

        // Stats Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Units",
                    value = units.size.toString(),
                    subtitle = "active semester",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Classes / week",
                    value = lessons.size.toString(),
                    subtitle = "recurring lessons",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Study time",
                    value = studyTimeString,
                    subtitle = "${sessions.size} logged sessions",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Open work",
                    value = openTasks.size.toString(),
                    subtitle = "$weeklyMinutes min in 7 days",
                    isAccent = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Next Lesson
        item {
            SectionHeader(
                title = "Next lesson",
                actionText = "Timetable →",
                onAction = onNavigateToTimetable
            )
            if (nextLesson != null) {
                LessonCard(
                    lesson = nextLesson,
                    unitName = unitNameHelper(nextLesson.unitId),
                    onClick = { onEditLesson(nextLesson) }
                )
            } else {
                EmptyState(message = "No lessons scheduled for this semester.")
            }
        }

        // Upcoming Work
        item {
            SectionHeader(
                title = "Upcoming work",
                actionText = "Tasks →",
                onAction = onNavigateToTasks
            )
            if (upcomingTasks.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    upcomingTasks.forEach { task ->
                        TaskRow(
                            task = task,
                            unitName = unitNameHelper(task.unitId),
                            onToggle = { onToggleTask(task.id) },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                }
            } else {
                EmptyState(message = "No upcoming academic deadlines.")
            }
        }

        // Weekly Target
        item {
            val progress = if (goals.weeklyMinutes > 0) weeklyMinutes.toFloat() / goals.weeklyMinutes.toFloat() else 0f
            ProgressCard(
                title = "Weekly Study Target",
                progressLabel = "$weeklyMinutes / ${goals.weeklyMinutes} min",
                progressRatio = progress
            )
        }

        // Week at a glance (Classes per day)
        item {
            SectionHeader(title = "Week at a glance (Classes / day)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DAYS_OF_WEEK.forEach { day ->
                    val dayLessons = lessons.filter { it.day.equals(day, ignoreCase = true) }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.take(3),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayLessons.size.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (dayLessons.isNotEmpty()) Color(0xFF8FC0FF) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
