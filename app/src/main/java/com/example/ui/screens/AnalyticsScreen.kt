package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.BrandAccent

@Composable
fun AnalyticsScreen(
    units: List<UnitItem>,
    tasks: List<TaskItem>,
    attendance: List<AttendanceRecord>,
    sessions: List<StudySession>,
    goals: Goals,
    modifier: Modifier = Modifier
) {
    val totalMinutes = sessions.sumOf { it.minutes }
    val totalHours = totalMinutes / 60
    val remMins = totalMinutes % 60
    val studyTimeString = if (totalHours > 0) "${totalHours}h ${remMins}m" else "${remMins}m"

    val doneTasks = tasks.count { it.status == "done" }
    val taskRate = if (tasks.isNotEmpty()) ((doneTasks.toDouble() / tasks.size) * 100).toInt() else 0

    val presentAtt = attendance.count { it.status == "present" }
    val attRate = if (attendance.isNotEmpty()) ((presentAtt.toDouble() / attendance.size) * 100).toInt() else 100

    val semTargetRate = if (goals.semesterTargetMinutes > 0) {
        ((totalMinutes.toDouble() / goals.semesterTargetMinutes) * 100).toInt().coerceAtMost(100)
    } else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Patterns and progress across your semester",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Analytics",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Stats grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Study time",
                    value = studyTimeString,
                    subtitle = "logged this semester",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Task completion",
                    value = if (tasks.isNotEmpty()) "$taskRate%" else "—",
                    subtitle = "$doneTasks / ${tasks.size} done",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Attendance",
                    value = "$attRate%",
                    subtitle = "present classes",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Semester target",
                    value = "$semTargetRate%",
                    subtitle = "$totalMinutes / ${goals.semesterTargetMinutes}m",
                    isAccent = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(title = "Study effort by unit")
        }

        if (units.isNotEmpty()) {
            items(units, key = { it.id }) { unit ->
                val unitMins = sessions
                    .filter { it.subject.equals(unit.name, ignoreCase = true) }
                    .sumOf { it.minutes }
                val target = if (unit.targetMinutes > 0) unit.targetMinutes else 1800
                val progress = (unitMins.toFloat() / target.toFloat()).coerceIn(0f, 1f)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unit.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$unitMins min",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8FC0FF)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = BrandAccent,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Target: $target min • ${(progress * 100).toInt()}% completed",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                EmptyState(message = "No units found.")
            }
        }
    }
}
