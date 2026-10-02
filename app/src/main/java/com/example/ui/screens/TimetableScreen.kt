package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.ui.components.EmptyState
import com.example.ui.components.LessonCard
import com.example.ui.theme.BrandAccent
import com.example.viewmodel.DAYS_OF_WEEK
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    semesterName: String,
    lessons: List<Lesson>,
    unitNameHelper: (String) -> String,
    onAddLesson: () -> Unit,
    onEditLesson: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayName = SimpleDateFormat("EEEE", Locale.US).format(Calendar.getInstance().time)
    var selectedDay by remember {
        mutableStateOf(if (DAYS_OF_WEEK.contains(todayName)) todayName else "Monday")
    }

    val dayLessons = lessons
        .filter { it.day.equals(selectedDay, ignoreCase = true) }
        .sortedBy { it.start }

    Scaffold(
        modifier = modifier.testTag("timetable_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddLesson,
                containerColor = BrandAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_lesson_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add lesson")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Recurring weekly timetable",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = semesterName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Scrollable Day Tabs
            ScrollableTabRow(
                selectedTabIndex = DAYS_OF_WEEK.indexOf(selectedDay).coerceAtLeast(0),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth().testTag("timetable_day_tabs"),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrandAccent
            ) {
                DAYS_OF_WEEK.forEach { day ->
                    val count = lessons.count { it.day.equals(day, ignoreCase = true) }
                    Tab(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(day.take(3), fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Normal)
                                if (count > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (selectedDay == day) BrandAccent else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = count.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedDay == day) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (dayLessons.isNotEmpty()) {
                    items(dayLessons, key = { it.id }) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            unitName = unitNameHelper(lesson.unitId),
                            onClick = { onEditLesson(lesson) }
                        )
                    }
                } else {
                    item {
                        EmptyState(message = "Free day! No lessons scheduled for $selectedDay.")
                    }
                }
            }
        }
    }
}
