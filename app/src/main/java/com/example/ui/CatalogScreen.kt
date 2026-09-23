package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.YogaPose
import com.example.util.YogaMediaUtils

fun getCategoryColorPair(category: String): Pair<Color, Color> {
    return when (category) {
        "Inversion" -> Color(0xFFDCFCE7) to Color(0xFF14532D)    // Deep Pine Green
        "Backbend" -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)     // Forest Leaf
        "Standing" -> Color(0xFFD1FAE5) to Color(0xFF047857)     // Jade Green
        "Balancing" -> Color(0xFFECFDF5) to Color(0xFF065F46)    // Emerald Sprout
        "Restorative" -> Color(0xFFF0FDF4) to Color(0xFF15803D)  // Soothing Sage
        "Core" -> Color(0xFFDCFCE7) to Color(0xFF16A34A)         // Vibrant Sprout
        "Arm Balance" -> Color(0xFFE0F2E9) to Color(0xFF0F5132)  // Matcha Green
        "Seated" -> Color(0xFFD1FAE5) to Color(0xFF0F766E)       // Tea Clover
        else -> Color(0xFFDCFCE7) to Color(0xFF15803D)
    }
}

fun getDifficultyColorPair(difficulty: String): Pair<Color, Color> {
    return when (difficulty) {
        "Beginner" -> Color(0xFFF0FDF4) to Color(0xFF16A34A)     // Fresh Leaf Green
        "Intermediate" -> Color(0xFFDCFCE7) to Color(0xFF15803D) // Solid Forest Green
        "Advanced" -> Color(0xFFBBF7D0) to Color(0xFF14532D)     // Deep Pine Green
        else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: YogaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val poses by viewModel.allPoses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()

    var selectedPoseForDetail by remember { mutableStateOf<YogaPose?>(null) }
    var poseForRoutineAdd by remember { mutableStateOf<YogaPose?>(null) }

    val difficulties = listOf("All", "Beginner", "Intermediate", "Advanced")

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "Yoga Poses Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${poses.size} poses available in database",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.filterPoses(query = it) },
                        placeholder = { Text("Search by name, Sanskrit, or muscle...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.filterPoses(query = "") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("catalog_search_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = (category == selectedCategory)
                            val (bgColor, textColor) = getCategoryColorPair(category)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.filterPoses(category = category)
                                },
                                label = { Text(category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = textColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = bgColor,
                                    labelColor = textColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) textColor else bgColor,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Difficulty Filter Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(difficulties) { diff ->
                            val isSelected = (diff == selectedDifficulty)
                            val (bgColor, textColor) = getDifficultyColorPair(diff)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.filterPoses(difficulty = diff)
                                },
                                label = { Text(diff, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = textColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = bgColor,
                                    labelColor = textColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) textColor else bgColor,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Pose List
            if (poses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No yoga poses matched your filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            viewModel.filterPoses(query = "", category = "All", difficulty = "All")
                        }) {
                            Text("Reset Filters")
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
                    items(poses, key = { it.id }) { pose ->
                        PoseCatalogCard(
                            pose = pose,
                            onClick = { selectedPoseForDetail = pose },
                            onWatchTutorial = {
                                YogaMediaUtils.launchYouTubeTutorial(context, pose.youtubeUrl, pose.englishName)
                            },
                            onAddToRoutine = { poseForRoutineAdd = pose }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Pose Detail Dialog
    selectedPoseForDetail?.let { pose ->
        PoseDetailDialog(
            pose = pose,
            onDismiss = { selectedPoseForDetail = null },
            onWatchTutorial = {
                YogaMediaUtils.launchYouTubeTutorial(context, pose.youtubeUrl, pose.englishName)
            },
            onAddToRoutine = {
                selectedPoseForDetail = null
                poseForRoutineAdd = pose
            }
        )
    }

    // Quick Add to Routine Dialog
    poseForRoutineAdd?.let { pose ->
        QuickAddToRoutineDialog(
            pose = pose,
            daysOfWeek = viewModel.daysOfWeek,
            onDismiss = { poseForRoutineAdd = null },
            onConfirm = { day, duration, notes ->
                viewModel.addRoutineItem(day, pose.id, duration, notes) {
                    poseForRoutineAdd = null
                }
            }
        )
    }
}

@Composable
fun PoseCatalogCard(
    pose: YogaPose,
    onClick: () -> Unit,
    onWatchTutorial: () -> Unit,
    onAddToRoutine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (catBg, catText) = getCategoryColorPair(pose.category)
    val (diffColor, diffTextColor) = getDifficultyColorPair(pose.difficultyLevel)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("pose_card_${pose.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pose.englishName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F291E)
                    )
                    Text(
                        text = pose.sanskritName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF4B6358)
                    )
                }

                // Difficulty Chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = diffColor
                ) {
                    Text(
                        text = pose.difficultyLevel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = diffTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row: Category + Target Muscles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = catBg
                ) {
                    Text(
                        text = pose.category,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = catText
                    )
                }

                Text(
                    text = "•  ${pose.targetMuscles}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4B6358),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onWatchTutorial,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartDisplay,
                        contentDescription = "Watch YouTube Tutorial",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tutorial", fontSize = 12.sp, color = Color(0xFF0F291E))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAddToRoutine,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF15803D),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Routine", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PoseDetailDialog(
    pose: YogaPose,
    onDismiss: () -> Unit,
    onWatchTutorial: () -> Unit,
    onAddToRoutine: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = pose.englishName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pose.sanskritName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category & Difficulty Pill Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = pose.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${pose.difficultyLevel} • ${pose.durationSeconds}s",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Muscles Section
                Column {
                    Text(
                        text = "Target Muscles",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = pose.targetMuscles,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Benefits Section
                Column {
                    Text(
                        text = "Benefits",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = pose.benefits,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Contraindications / Caution Section
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Contraindications",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Contraindications / Caution",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = pose.contraindications,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onWatchTutorial,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Icon(Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Watch YouTube Tutorial")
            }
        },
        dismissButton = {
            TextButton(onClick = onAddToRoutine) {
                Text("+ Add to Routine")
            }
        }
    )
}

@Composable
fun QuickAddToRoutineDialog(
    pose: YogaPose,
    daysOfWeek: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (day: String, duration: Int, notes: String) -> Unit
) {
    var selectedDay by remember { mutableStateOf(daysOfWeek.firstOrNull() ?: "Monday") }
    var durationText by remember { mutableStateOf(pose.durationSeconds.toString()) }
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add \"${pose.englishName}\"") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select Day of Week:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(daysOfWeek) { day ->
                        val isSelected = (day == selectedDay)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDay = day },
                            label = { Text(day.take(3), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF15803D),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF15803D)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) Color(0xFF15803D) else Color(0xFFDCFCE7),
                                enabled = true,
                                selected = isSelected
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                    label = { Text("Duration (seconds)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Custom Notes") },
                    placeholder = { Text("e.g. Focus on alignment") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: pose.durationSeconds
                    onConfirm(selectedDay, duration, notesText)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF15803D),
                    contentColor = Color.White
                )
            ) {
                Text("Add to $selectedDay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
