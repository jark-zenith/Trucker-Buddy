package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteDialog(
    initialNote: NoteItem? = null,
    semesterId: String,
    units: List<UnitItem>,
    onDismiss: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var selectedUnitId by remember { mutableStateOf(initialNote?.unitId ?: "") }
    var body by remember { mutableStateOf(initialNote?.body ?: "") }
    var unitExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialNote == null) "New Note" else "Edit Note", fontWeight = FontWeight.Bold) },
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
                    label = { Text("Note Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_note_title")
                )

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
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("note_unit_dropdown")
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

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Note Body") },
                    minLines = 5,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth().testTag("input_note_body")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val note = initialNote?.copy(
                            title = title.trim(),
                            unitId = selectedUnitId,
                            body = body.trim(),
                            updated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        ) ?: NoteItem(
                            semesterId = semesterId,
                            title = title.trim(),
                            unitId = selectedUnitId,
                            body = body.trim(),
                            updated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        )
                        onSave(note)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_note_button")
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceDialog(
    semesterId: String,
    units: List<UnitItem>,
    onDismiss: () -> Unit,
    onSave: (ResourceItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedUnitId by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Link") }
    var url by remember { mutableStateOf("https://") }

    var unitExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    val resourceTypes = listOf("Link", "Video", "Document", "Reference")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Academic Resource", fontWeight = FontWeight.Bold) },
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
                    label = { Text("Resource Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_resource_title")
                )

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
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("resource_unit_dropdown")
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
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("resource_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        resourceTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { type = t; typeExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL / Link *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_resource_url")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        onSave(
                            ResourceItem(
                                semesterId = semesterId,
                                title = title.ifBlank { url }.trim(),
                                unitId = selectedUnitId,
                                type = type,
                                url = url.trim()
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_resource_button")
            ) {
                Text("Save Resource")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddSemesterDialog(
    onDismiss: () -> Unit,
    onSave: (Semester) -> Unit
) {
    var name by remember { mutableStateOf("Semester 4") }
    var year by remember { mutableStateOf("2027") }
    var start by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var end by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() + 86400000L * 90)))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Semester", fontWeight = FontWeight.Bold) },
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
                    label = { Text("Semester name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_semester_name")
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Academic year") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_semester_year")
                )
                OutlinedTextField(
                    value = start,
                    onValueChange = { start = it },
                    label = { Text("Start date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_semester_start")
                )
                OutlinedTextField(
                    value = end,
                    onValueChange = { end = it },
                    label = { Text("End date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_semester_end")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Semester(
                                name = name.trim(),
                                year = year.trim(),
                                start = start.trim(),
                                end = end.trim(),
                                status = "active"
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("create_semester_button")
            ) {
                Text("Create Semester")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    semesterId: String,
    onDismiss: () -> Unit,
    onSave: (CalendarEvent) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var type by remember { mutableStateOf("Event") }
    var typeExpanded by remember { mutableStateOf(false) }
    val eventTypes = listOf("Event", "Exam", "Deadline", "Study session")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Calendar Event", fontWeight = FontWeight.Bold) },
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
                    label = { Text("Event Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_event_title")
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_event_date")
                )
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Event Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor().testTag("event_type_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        eventTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { type = t; typeExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            CalendarEvent(
                                semesterId = semesterId,
                                title = title.trim(),
                                date = date.trim(),
                                type = type
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_event_button")
            ) {
                Text("Save Event")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
