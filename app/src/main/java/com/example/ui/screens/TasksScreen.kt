package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.components.EmptyState
import com.example.ui.components.TaskRow
import com.example.ui.theme.BrandAccent

@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    unitNameHelper: (String) -> String,
    onAddTask: () -> Unit,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") } // All, Open, Done

    val openCount = tasks.count { it.status != "done" }
    val doneCount = tasks.count { it.status == "done" }

    val filteredTasks = when (selectedFilter) {
        "Open" -> tasks.filter { it.status != "done" }
        "Done" -> tasks.filter { it.status == "done" }
        else -> tasks
    }.sortedWith(compareBy({ it.status == "done" }, { it.due }))

    Scaffold(
        modifier = modifier.testTag("tasks_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = BrandAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add academic work")
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
                    text = "Assignments, CATs, tests, exams, projects & revision",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tasks & Assessments",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text("All (${tasks.size})") },
                        modifier = Modifier.testTag("filter_all_tasks")
                    )
                    FilterChip(
                        selected = selectedFilter == "Open",
                        onClick = { selectedFilter = "Open" },
                        label = { Text("$openCount open") },
                        modifier = Modifier.testTag("filter_open_tasks")
                    )
                    FilterChip(
                        selected = selectedFilter == "Done",
                        onClick = { selectedFilter = "Done" },
                        label = { Text("$doneCount done") },
                        modifier = Modifier.testTag("filter_done_tasks")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (filteredTasks.isNotEmpty()) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            unitName = unitNameHelper(task.unitId),
                            onToggle = { onToggleTask(task.id) },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                } else {
                    item {
                        EmptyState(
                            message = if (selectedFilter == "Done") "No completed tasks yet."
                            else if (selectedFilter == "Open") "Great job! No open tasks."
                            else "No academic work added yet."
                        )
                    }
                }
            }
        }
    }
}
