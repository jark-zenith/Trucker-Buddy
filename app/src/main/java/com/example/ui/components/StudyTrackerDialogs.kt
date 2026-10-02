package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandGood
import com.example.viewmodel.DAYS_OF_WEEK
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditUnitDialog(
    initialUnit: UnitItem? = null,
    semesterId: String,
    onDismiss: () -> Unit,
    onSave: (UnitItem) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialUnit?.name ?: "") }
    var code by remember { mutableStateOf(initialUnit?.code ?: "") }
    var lecturer by remember { mutableStateOf(initialUnit?.lecturer ?: "") }
    var room by remember { mutableStateOf(initialUnit?.room ?: "") }
    var targetMinutes by remember { mutableStateOf((initialUnit?.targetMinutes ?: 1800).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialUnit == null) "Add Unit" else "Edit Unit",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Unit name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_unit_name")
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Unit code (e.g. ICT201)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_unit_code")
                )
                OutlinedTextField(
                    value = lecturer,
                    onValueChange = { lecturer = it },
                    label = { Text("Lecturer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_unit_lecturer")
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Default Room (e.g. L24)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_unit_room")
                )
                OutlinedTextField(
                    value = targetMinutes,
                    onValueChange = { targetMinutes = it },
                    label = { Text("Self-study target (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_unit_target")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val unit = initialUnit?.copy(
                            name = name.trim(),
                            code = code.trim(),
                            lecturer = lecturer.trim(),
                            room = room.trim(),
                            targetMinutes = targetMinutes.toIntOrNull() ?: 1800
                        ) ?: UnitItem(
                            semesterId = semesterId,
                            name = name.trim(),
                            code = code.trim(),
                            lecturer = lecturer.trim(),
                            room = room.trim(),
                            targetMinutes = targetMinutes.toIntOrNull() ?: 1800
                        )
                        onSave(unit)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_unit_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandDanger),
                        modifier = Modifier.testTag("delete_unit_button")
                    ) {
                        Text("Delete")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLessonDialog(
    initialLesson: Lesson? = null,
    semesterId: String,
    units: List<UnitItem>,
    onDismiss: () -> Unit,
    onSave: (Lesson) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var selectedUnitId by remember { mutableStateOf(initialLesson?.unitId ?: units.firstOrNull()?.id ?: "") }
    var day by remember { mutableStateOf(initialLesson?.day ?: "Monday") }
    var start by remember { mutableStateOf(initialLesson?.start ?: "08:00") }
    var end by remember { mutableStateOf(initialLesson?.end ?: "10:00") }
    var room by remember { mutableStateOf(initialLesson?.room ?: "") }
    var reminder by remember { mutableStateOf((initialLesson?.reminder ?: 15).toString()) }

    var dayExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialLesson == null) "Add Lesson" else "Edit Lesson",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Unit Picker
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it }
                ) {
                    OutlinedTextField(
                        value = units.find { it.id == selectedUnitId }?.name ?: "Select Unit",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("lesson_unit_picker")
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u.name) },
                                onClick = {
                                    selectedUnitId = u.id
                                    if (room.isBlank() && u.room.isNotBlank()) room = u.room
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }

                // Day Picker
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = it }
                ) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day of week") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("lesson_day_picker")
                    )
                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        DAYS_OF_WEEK.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    day = d
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it },
                        label = { Text("Start (HH:MM)") },
                        modifier = Modifier.weight(1f).testTag("input_lesson_start")
                    )
                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it },
                        label = { Text("End (HH:MM)") },
                        modifier = Modifier.weight(1f).testTag("input_lesson_end")
                    )
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room (e.g. L21)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_lesson_room")
                )

                OutlinedTextField(
                    value = reminder,
                    onValueChange = { reminder = it },
                    label = { Text("Reminder (minutes before)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_lesson_reminder")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedUnitId.isNotBlank()) {
                        val lesson = initialLesson?.copy(
                            unitId = selectedUnitId,
                            day = day,
                            start = start.trim(),
                            end = end.trim(),
                            room = room.trim(),
                            reminder = reminder.toIntOrNull() ?: 15
                        ) ?: Lesson(
                            semesterId = semesterId,
                            unitId = selectedUnitId,
                            day = day,
                            start = start.trim(),
                            end = end.trim(),
                            room = room.trim(),
                            reminder = reminder.toIntOrNull() ?: 15
                        )
                        onSave(lesson)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_lesson_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = BrandDanger),
                        modifier = Modifier.testTag("delete_lesson_button")
                    ) {
                        Text("Delete")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskDialog(
    initialTask: TaskItem? = null,
    semesterId: String,
    units: List<UnitItem>,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var selectedUnitId by remember { mutableStateOf(initialTask?.unitId ?: "") }
    var type by remember { mutableStateOf(initialTask?.type ?: "Assignment") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: "Normal") }
    var due by remember {
        mutableStateOf(initialTask?.due ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    var unitExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val taskTypes = listOf("Assignment", "CAT", "Test", "Exam", "Project", "Revision")
    val priorities = listOf("Normal", "High", "Critical")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTask == null) "Add Academic Work" else "Edit Task",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_task_title")
                )

                // Unit
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (selectedUnitId.isBlank()) "General" else units.find { it.id == selectedUnitId }?.name ?: "General",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("task_unit_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("General") },
                            onClick = { selectedUnitId = ""; unitExpanded = false }
                        )
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u.name) },
                                onClick = { selectedUnitId = u.id; unitExpanded = false }
                            )
                        }
                    }
                }

                // Type
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("task_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        taskTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { type = t; typeExpanded = false }
                            )
                        }
                    }
                }

                // Priority
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("task_priority_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = { priority = p; priorityExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = due,
                    onValueChange = { due = it },
                    label = { Text("Due date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_task_due")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = initialTask?.copy(
                            title = title.trim(),
                            unitId = selectedUnitId,
                            type = type,
                            priority = priority,
                            due = due.trim()
                        ) ?: TaskItem(
                            semesterId = semesterId,
                            title = title.trim(),
                            unitId = selectedUnitId,
                            type = type,
                            priority = priority,
                            due = due.trim(),
                            status = "open"
                        )
                        onSave(task)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text("Save task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MarkAttendanceDialog(
    unit: UnitItem,
    onDismiss: () -> Unit,
    onMark: (status: String) -> Unit
) {
    val today = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(Date())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Mark Attendance", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${unit.name} • $today",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onMark("present"); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGood),
                        modifier = Modifier.weight(1f).testTag("mark_present_btn")
                    ) {
                        Text("Present")
                    }
                    Button(
                        onClick = { onMark("late"); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        modifier = Modifier.weight(1f).testTag("mark_late_btn")
                    ) {
                        Text("Late")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onMark("absent"); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandDanger),
                        modifier = Modifier.weight(1f).testTag("mark_absent_btn")
                    ) {
                        Text("Absent")
                    }
                    OutlinedButton(
                        onClick = { onMark("excused"); onDismiss() },
                        modifier = Modifier.weight(1f).testTag("mark_excused_btn")
                    ) {
                        Text("Excused")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGradeDialog(
    semesterId: String,
    units: List<UnitItem>,
    onDismiss: () -> Unit,
    onSave: (GradeRecord) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var type by remember { mutableStateOf("CAT") }
    var score by remember { mutableStateOf("") }
    var max by remember { mutableStateOf("100") }

    var unitExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    val gradeTypes = listOf("CAT", "Assignment", "Test", "Exam", "Project")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Grade", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Assessment Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_grade_name")
                )

                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it }
                ) {
                    OutlinedTextField(
                        value = units.find { it.id == selectedUnitId }?.name ?: "Select Unit",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("grade_unit_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u.name) },
                                onClick = { selectedUnitId = u.id; unitExpanded = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("grade_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        gradeTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { type = t; typeExpanded = false }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = score,
                        onValueChange = { score = it },
                        label = { Text("Score *") },
                        modifier = Modifier.weight(1f).testTag("input_grade_score")
                    )
                    OutlinedTextField(
                        value = max,
                        onValueChange = { max = it },
                        label = { Text("Max score") },
                        modifier = Modifier.weight(1f).testTag("input_grade_max")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sVal = score.toDoubleOrNull()
                    val mVal = max.toDoubleOrNull() ?: 100.0
                    if (name.isNotBlank() && sVal != null) {
                        onSave(
                            GradeRecord(
                                semesterId = semesterId,
                                name = name.trim(),
                                unitId = selectedUnitId,
                                type = type,
                                score = sVal,
                                max = mVal
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_grade_button")
            ) {
                Text("Save Grade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSessionDialog(
    semesterId: String,
    initialType: String = "Revision",
    initialMinutes: Int = 45,
    onDismiss: () -> Unit,
    onSave: (StudySession) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf(initialMinutes.toString()) }
    var date by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var type by remember { mutableStateOf(initialType) }
    var note by remember { mutableStateOf("") }

    var typeExpanded by remember { mutableStateOf(false) }
    val sessionTypes = listOf("Revision", "Practice", "Reading", "Notes", "Homework", "Focus")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Study Session", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Topic") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_session_subject")
                )

                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Minutes *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_session_minutes")
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("session_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        sessionTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { type = t; typeExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_session_date")
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_session_note")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = minutes.toIntOrNull() ?: 0
                    if (m > 0) {
                        onSave(
                            StudySession(
                                semesterId = semesterId,
                                subject = subject.ifBlank { "General study" }.trim(),
                                minutes = m,
                                date = date.trim(),
                                type = type,
                                note = note.trim()
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_session_button")
            ) {
                Text("Log session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
