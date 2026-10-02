package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goals
import com.example.data.model.Preferences
import com.example.data.model.Profile
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profile: Profile,
    preferences: Preferences,
    goals: Goals,
    currentTheme: String,
    syncStatus: String,
    onSaveSettings: (
        name: String,
        institution: String,
        program: String,
        weeklyMinutes: Int,
        semesterTargetMinutes: Int,
        syncServerUrl: String,
        syncKey: String,
        theme: String
    ) -> Unit,
    onPullServer: () -> Unit,
    onSyncServer: () -> Unit,
    onExportBackup: () -> String,
    onImportBackup: (String) -> Unit,
    onResetAll: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var name by remember(profile) { mutableStateOf(profile.name) }
    var institution by remember(profile) { mutableStateOf(profile.institution) }
    var program by remember(profile) { mutableStateOf(profile.program) }
    var weeklyMinutes by remember(goals) { mutableStateOf(goals.weeklyMinutes.toString()) }
    var semesterTargetMinutes by remember(goals) { mutableStateOf(goals.semesterTargetMinutes.toString()) }
    var syncServerUrl by remember(preferences) { mutableStateOf(preferences.syncServerUrl) }
    var syncKey by remember(preferences) { mutableStateOf(preferences.syncKey) }
    var themeSelection by remember(currentTheme) { mutableStateOf(currentTheme) }

    var showResetConfirm by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFF142945)
            ) {
                Text(
                    text = "PRUDEN TECHNOLOGIES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8FC0FF),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "System Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Student Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Student Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_student_name")
                )
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Institution") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_institution")
                )
                OutlinedTextField(
                    value = program,
                    onValueChange = { program = it },
                    label = { Text("Program (e.g. ICT)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_program")
                )
            }
        }

        // Study Goals Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Study Targets",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = weeklyMinutes,
                    onValueChange = { weeklyMinutes = it },
                    label = { Text("Weekly study target (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_weekly_minutes")
                )
                OutlinedTextField(
                    value = semesterTargetMinutes,
                    onValueChange = { semesterTargetMinutes = it },
                    label = { Text("Semester study target (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_semester_target_minutes")
                )
            }
        }

        // Appearance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Appearance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = themeSelection == "dark",
                        onClick = { themeSelection = "dark" },
                        label = { Text("Dark Theme") },
                        modifier = Modifier.weight(1f).testTag("theme_dark_chip")
                    )
                    FilterChip(
                        selected = themeSelection == "light",
                        onClick = { themeSelection = "light" },
                        label = { Text("Light Theme") },
                        modifier = Modifier.weight(1f).testTag("theme_light_chip")
                    )
                }
            }
        }

        // Backend Sync Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Backend Sync (Optional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (syncStatus == "Synced") Color(0xFF142945) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = syncStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (syncStatus == "Synced") Color(0xFF86EFAC) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = syncServerUrl,
                    onValueChange = { syncServerUrl = it },
                    label = { Text("Server URL (e.g. http://10.0.2.2:3000)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_sync_url")
                )
                OutlinedTextField(
                    value = syncKey,
                    onValueChange = { syncKey = it },
                    label = { Text("Sync Key (if required by server)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_sync_key")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onPullServer,
                        modifier = Modifier.weight(1f).testTag("btn_pull_server")
                    ) {
                        Text("Pull state", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onSyncServer,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                        modifier = Modifier.weight(1f).testTag("btn_push_server")
                    ) {
                        Text("Push state", fontSize = 12.sp)
                    }
                }
            }
        }

        // Action Buttons
        Button(
            onClick = {
                onSaveSettings(
                    name.trim(),
                    institution.trim(),
                    program.trim(),
                    weeklyMinutes.toIntOrNull() ?: 300,
                    semesterTargetMinutes.toIntOrNull() ?: 7200,
                    syncServerUrl.trim(),
                    syncKey.trim(),
                    themeSelection
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
            modifier = Modifier.fillMaxWidth().testTag("save_settings_button")
        ) {
            Text("Save settings", fontWeight = FontWeight.Bold)
        }

        // Backup and Restore Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Backup & Data",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val json = onExportBackup()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("StudyTrackerBackup", json))

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_SUBJECT, "Study Tracker Backup")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Backup"))
                            onShowMessage("Backup copied to clipboard and share opened")
                        },
                        modifier = Modifier.weight(1f).testTag("btn_export_backup")
                    ) {
                        Text("Export backup", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.weight(1f).testTag("btn_import_backup")
                    ) {
                        Text("Import backup", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = { showResetConfirm = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger),
                    modifier = Modifier.fillMaxWidth().testTag("btn_reset_all")
                ) {
                    Text("Reset all local data", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "Study Tracker is local-first. The backend is an optional REST + SQLite layer for synchronization across devices.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
            modifier = Modifier.padding(bottom = 80.dp)
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Data?") },
            text = { Text("This will reset all units, tasks, lessons, grades, and study sessions back to default sample state.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAll()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger)
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Backup JSON") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Paste your exported JSON backup text below:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        label = { Text("Backup JSON") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importText.isNotBlank()) {
                            onImportBackup(importText)
                            showImportDialog = false
                            importText = ""
                        }
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
