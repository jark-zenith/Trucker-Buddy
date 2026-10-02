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
import com.example.data.model.GradeRecord
import com.example.ui.components.EmptyState
import com.example.ui.components.StatCard
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger

@Composable
fun GradesScreen(
    grades: List<GradeRecord>,
    unitNameHelper: (String) -> String,
    onAddGrade: () -> Unit,
    onDeleteGrade: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val avg = if (grades.isNotEmpty()) {
        (grades.sumOf { (it.score / (if (it.max > 0) it.max else 100.0)) * 100.0 } / grades.size).toInt()
    } else 0

    val best = if (grades.isNotEmpty()) {
        "${grades.maxOf { ((it.score / (if (it.max > 0) it.max else 100.0)) * 100.0).toInt() }}%"
    } else "—"

    Scaffold(
        modifier = modifier.testTag("grades_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGrade,
                containerColor = BrandAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_grade_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add grade")
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
                    text = "Assessment record and academic performance",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Grades",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Average",
                    value = "$avg%",
                    subtitle = "across assessments",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Assessments",
                    value = grades.size.toString(),
                    subtitle = "recorded",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Best",
                    value = best,
                    subtitle = "highest score",
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
                if (grades.isNotEmpty()) {
                    items(grades, key = { it.id }) { grade ->
                        val percent = ((grade.score / (if (grade.max > 0) grade.max else 100.0)) * 100.0).toInt()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("grade_row_${grade.id}"),
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
                                        text = grade.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${unitNameHelper(grade.unitId)} • ${grade.type}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${grade.score}/${grade.max}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8FC0FF)
                                    )
                                    Text(
                                        text = "$percent%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { onDeleteGrade(grade.id) },
                                    modifier = Modifier.size(36.dp).testTag("delete_grade_${grade.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete grade", tint = BrandDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    item {
                        EmptyState(message = "No grades recorded yet.")
                    }
                }
            }
        }
    }
}
