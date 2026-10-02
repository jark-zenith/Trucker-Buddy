package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.data.model.Lesson
import com.example.data.model.StudySession
import com.example.data.model.UnitItem
import com.example.ui.components.EmptyState
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger

@Composable
fun UnitsScreen(
    units: List<UnitItem>,
    lessons: List<Lesson>,
    sessions: List<StudySession>,
    onAddUnit: () -> Unit,
    onEditUnit: (UnitItem) -> Unit,
    onDeleteUnit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.testTag("units_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddUnit,
                containerColor = BrandAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_unit_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add unit")
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
                    text = "Subjects, credits and self-study targets",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Academic Units",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (units.isNotEmpty()) {
                    items(units, key = { it.id }) { unit ->
                        val unitLessons = lessons.filter { it.unitId == unit.id }
                        val studiedMinutes = sessions
                            .filter { it.subject.equals(unit.name, ignoreCase = true) }
                            .sumOf { it.minutes }
                        val progress = if (unit.targetMinutes > 0) studiedMinutes.toFloat() / unit.targetMinutes.toFloat() else 0f

                        val initials = unit.name.split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .take(2)
                            .joinToString("")
                            .ifBlank { "U" }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("unit_card_${unit.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF142945)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initials,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF8FC0FF)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = unit.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${if (unit.code.isNotBlank()) unit.code + " • " else ""}${if (unit.room.isNotBlank()) "Room " + unit.room else "Room not set"}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEditUnit(unit) },
                                        modifier = Modifier.size(36.dp).testTag("edit_unit_${unit.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit unit", tint = BrandAccent, modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = { onDeleteUnit(unit.id) },
                                        modifier = Modifier.size(36.dp).testTag("delete_unit_${unit.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete unit", tint = BrandDanger, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${unitLessons.size} lessons/week${if (unit.lecturer.isNotBlank()) " • " + unit.lecturer else ""}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { progress.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = BrandAccent,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$studiedMinutes / ${unit.targetMinutes} min study target",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8FC0FF)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        EmptyState(message = "No units added yet. Tap + to add a unit.")
                    }
                }
            }
        }
    }
}
