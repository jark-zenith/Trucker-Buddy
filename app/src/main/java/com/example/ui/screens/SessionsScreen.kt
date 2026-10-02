package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySession
import com.example.ui.components.EmptyState
import com.example.ui.components.StatCard
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger

@Composable
fun SessionsScreen(
    sessions: List<StudySession>,
    weeklyMinutes: Int,
    onLogSession: () -> Unit,
    onDeleteSession: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMinutes = sessions.sumOf { it.minutes }
    val totalHours = totalMinutes / 60
    val remMins = totalMinutes % 60
    val totalTimeString = if (totalHours > 0) "${totalHours}h ${remMins}m" else "${remMins}m"
    val avgSession = if (sessions.isNotEmpty()) (totalMinutes / sessions.size) else 0

    Scaffold(
        modifier = modifier.testTag("sessions_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onLogSession,
                containerColor = BrandAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("log_session_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log study session")
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
                    text = "Independent study outside scheduled classes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Study Sessions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total",
                    value = totalTimeString,
                    subtitle = "this semester",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sessions",
                    value = sessions.size.toString(),
                    subtitle = "logged blocks",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Average",
                    value = "${avgSession}m",
                    subtitle = "per session",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Weekly",
                    value = "${weeklyMinutes}m",
                    subtitle = "last 7 days",
                    isAccent = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (sessions.isNotEmpty()) {
                    items(sessions.sortedByDescending { it.date }, key = { it.id }) { session ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("session_card_${session.id}"),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.subject,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${session.type} • ${session.date}${if (session.note.isNotBlank()) " • " + session.note else ""}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF142945)
                                ) {
                                    Text(
                                        text = "${session.minutes} min",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8FC0FF),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { onDeleteSession(session.id) },
                                    modifier = Modifier.size(36.dp).testTag("delete_session_${session.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete session", tint = BrandDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    item {
                        EmptyState(message = "No study sessions logged yet.")
                    }
                }
            }
        }
    }
}
