package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.YogaViewModel
import com.example.model.WeeklyRoutine
import com.example.model.YogaPose
import com.example.util.YogaMediaUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: YogaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDay by viewModel.selectedDay.collectAsState()
    val routines by viewModel.currentDayRoutines.collectAsState()
    val allPoses by viewModel.allPoses.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var routineToEdit by remember { mutableStateOf<WeeklyRoutine?>(null) }
    var activeTimerRoutine by remember { mutableStateOf<WeeklyRoutine?>(null) }

    val completedCount = routines.count { it.isCompleted == 1 }
    val totalCount = routines.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_routine")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Pose to Routine")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Title & Today's Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weekly Routine Planner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedCount of $totalCount poses completed for $selectedDay",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Progress Pill
                        val progressPercent = if (totalCount > 0) (completedCount * 100 / totalCount) else 0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (progressPercent == 100 && totalCount > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "$progressPercent%",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (progressPercent == 100 && totalCount > 0) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Day of Week Selector Chips (Mon - Sun)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.daysOfWeek) { day ->
                            val isSelected = (day == selectedDay)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectDay(day) },
                                label = {
                                    Text(
                                        text = day.take(3),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF15803D),
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF15803D)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) Color(0xFF15803D) else Color(0xFFDCFCE7),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("chip_day_$day")
                            )
                        }
                    }
                }
            }

            // Routine List
            if (routines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = "No poses",
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No poses scheduled for $selectedDay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tap the + button below to add poses from your 200-pose catalog and build your daily flow.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Pose to $selectedDay")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(routines, key = { it.routineId }) { routine ->
                        RoutineItemCard(
                            routine = routine,
                            onToggleComplete = { viewModel.toggleRoutineCompletion(routine) },
                            onEdit = { routineToEdit = routine },
                            onDelete = { viewModel.deleteRoutineItem(routine.routineId) },
                            onStartTimer = { activeTimerRoutine = routine },
                            onWatchTutorial = {
                                val url = routine.yogaPose?.youtubeUrl ?: "https://www.youtube.com/results?search_query=Yoga+${routine.yogaPose?.englishName ?: "Pose"}"
                                YogaMediaUtils.launchYouTubeTutorial(context, url, routine.yogaPose?.englishName ?: "Yoga Pose")
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add Pose to Routine Dialog
    if (showAddDialog) {
        AddRoutineDialog(
            day = selectedDay,
            allPoses = allPoses,
            onDismiss = { showAddDialog = false },
            onAdd = { poseId, duration, notes ->
                viewModel.addRoutineItem(selectedDay, poseId, duration, notes) {
                    showAddDialog = false
                }
            }
        )
    }

    // Edit Routine Dialog
    routineToEdit?.let { routine ->
        EditRoutineDialog(
            routine = routine,
            onDismiss = { routineToEdit = null },
            onSave = { duration, notes ->
                viewModel.updateRoutineItem(routine.routineId, duration, notes) {
                    routineToEdit = null
                }
            }
        )
    }

    // In-App Practice Timer Modal
    activeTimerRoutine?.let { routine ->
        PracticeTimerDialog(
            routine = routine,
            onDismiss = { activeTimerRoutine = null },
            onFinished = {
                viewModel.toggleRoutineCompletion(routine)
                activeTimerRoutine = null
            }
        )
    }
}

@Composable
fun RoutineItemCard(
    routine: WeeklyRoutine,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStartTimer: () -> Unit,
    onWatchTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = routine.isCompleted == 1
    val pose = routine.yogaPose

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF0FDF4) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isCompleted) Color(0xFF86EFAC) else Color(0xFFDCFCE7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 1.dp else 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("routine_card_${routine.routineId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox for completion
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF15803D),
                        uncheckedColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("checkbox_routine_${routine.routineId}")
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pose?.englishName ?: "Pose ${routine.poseId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF15803D) else Color(0xFF0F291E)
                    )

                    if (pose != null && pose.sanskritName.isNotBlank()) {
                        Text(
                            text = pose.sanskritName,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF4B6358)
                        )
                    }
                }

                // Duration Badge - Fresh Botanical Matcha
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "${routine.targetDuration}s",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14532D)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete routine item",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (!routine.customNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = routine.customNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row: Practice Timer, YouTube Tutorial, Edit Notes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Practice Timer
                OutlinedButton(
                    onClick = onStartTimer,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Timer", fontSize = 12.sp)
                }

                // YouTube Tutorial
                OutlinedButton(
                    onClick = onWatchTutorial,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tutorial", fontSize = 12.sp)
                }

                // Edit Notes/Duration
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit notes",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineDialog(
    day: String,
    allPoses: List<YogaPose>,
    onDismiss: () -> Unit,
    onAdd: (poseId: String, duration: Int, notes: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPoseId by remember { mutableStateOf(allPoses.firstOrNull()?.id ?: "") }
    var durationText by remember { mutableStateOf("45") }
    var notesText by remember { mutableStateOf("") }

    val filteredPoses = remember(searchQuery, allPoses) {
        if (searchQuery.isBlank()) allPoses.take(40)
        else allPoses.filter {
            it.englishName.contains(searchQuery, ignoreCase = true) ||
                    it.sanskritName.contains(searchQuery, ignoreCase = true)
        }.take(40)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Pose to $day") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 200 poses...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select Pose:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Pose Selector List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    items(filteredPoses) { pose ->
                        val isSelected = pose.id == selectedPoseId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable {
                                    selectedPoseId = pose.id
                                    durationText = pose.durationSeconds.toString()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedPoseId = pose.id
                                    durationText = pose.durationSeconds.toString()
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = pose.englishName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${pose.category} • ${pose.difficultyLevel}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it.filter { char -> char.isDigit() } },
                        label = { Text("Duration (sec)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Quick duration presets
                    listOf("30", "45", "60").forEach { preset ->
                        FilterChip(
                            selected = durationText == preset,
                            onClick = { durationText = preset },
                            label = { Text("${preset}s") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Custom Notes (optional)") },
                    placeholder = { Text("e.g. Inhale deeply, hold for 5 breaths") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 45
                    if (selectedPoseId.isNotBlank()) {
                        onAdd(selectedPoseId, duration, notesText)
                    }
                },
                enabled = selectedPoseId.isNotBlank()
            ) {
                Text("Add to Routine")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditRoutineDialog(
    routine: WeeklyRoutine,
    onDismiss: () -> Unit,
    onSave: (duration: Int, notes: String) -> Unit
) {
    var durationText by remember { mutableStateOf(routine.targetDuration.toString()) }
    var notesText by remember { mutableStateOf(routine.customNotes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Routine Pose") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = routine.yogaPose?.englishName ?: "Pose ${routine.poseId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Duration (seconds)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Custom Notes") },
                    placeholder = { Text("e.g. Focus on hamstring stretch") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: routine.targetDuration
                    onSave(duration, notesText)
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PracticeTimerDialog(
    routine: WeeklyRoutine,
    onDismiss: () -> Unit,
    onFinished: () -> Unit
) {
    val totalSeconds = remember { routine.targetDuration.coerceAtLeast(5) }
    var secondsLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        } else if (secondsLeft == 0 && isRunning) {
            isRunning = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = routine.yogaPose?.englishName ?: "Practice Pose",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (routine.yogaPose != null && routine.yogaPose.sanskritName.isNotBlank()) {
                    Text(
                        text = routine.yogaPose.sanskritName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Big Timer Circle
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(if (secondsLeft == 0) Color(0xFF15803D) else Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$secondsLeft",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (secondsLeft == 0) Color.White else Color(0xFF14532D)
                        )
                        Text(
                            text = if (secondsLeft == 0) "NAMASTE!" else "SECONDS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (secondsLeft == 0) Color.White else Color(0xFF14532D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { (totalSeconds - secondsLeft).toFloat() / totalSeconds.toFloat() },
                    color = Color(0xFF15803D),
                    trackColor = Color(0xFFDCFCE7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = { isRunning = !isRunning }) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRunning) "Pause" else "Resume")
                    }

                    OutlinedButton(onClick = {
                        secondsLeft = totalSeconds
                        isRunning = true
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF15803D),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark Completed")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
