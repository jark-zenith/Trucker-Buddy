package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySession
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandAccentSecondary

@Composable
fun FocusLabScreen(
    remainingSeconds: Int,
    isRunning: Boolean,
    sessions: List<StudySession>,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onSetDuration: (Int) -> Unit,
    onLogFocusBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    val focusMinutesLogged = sessions
        .filter { it.type.equals("Focus", ignoreCase = true) }
        .sumOf { it.minutes }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("focus_lab_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Deep-work timer & revision focus",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Focus Lab",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Hero Focus Timer Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("focus_timer_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF284E77), Color(0xFF163C68))))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF163C68), Color(0xFF0B1726))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFF142945)
                    ) {
                        Text(
                            text = "FOCUS LAB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8FC0FF),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = timeFormatted,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("focus_timer_digits")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Deep-work timer for revision, coding, reading or assignments.",
                        fontSize = 13.sp,
                        color = Color(0xFF91A2B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onToggle,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                            modifier = Modifier.testTag("focus_toggle_btn")
                        ) {
                            Text(
                                text = if (isRunning) "Pause" else "Start",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedButton(
                            onClick = onReset,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("focus_reset_btn")
                        ) {
                            Text("Reset", modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = remainingSeconds == 25 * 60 && !isRunning,
                            onClick = { onSetDuration(25) },
                            label = { Text("25 min") },
                            modifier = Modifier.testTag("focus_25m_chip")
                        )
                        FilterChip(
                            selected = remainingSeconds == 50 * 60 && !isRunning,
                            onClick = { onSetDuration(50) },
                            label = { Text("50 min") },
                            modifier = Modifier.testTag("focus_50m_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "$focusMinutesLogged focus minutes logged this semester",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8FC0FF)
                    )
                }
            }
        }

        // Focus Workflow Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Text(
                    text = "Focus Workflow",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose one unit, silence distractions, work until the timer ends, then log the block to keep track of your self-study.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onLogFocusBlock,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccentSecondary),
                    modifier = Modifier.testTag("log_focus_block_btn")
                ) {
                    Text("+ Log focus block", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
