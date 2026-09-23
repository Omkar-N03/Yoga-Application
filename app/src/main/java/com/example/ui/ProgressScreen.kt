package com.example.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.YogaViewModel
import com.example.model.ProgressTracker
import com.example.model.YogaPose
import com.example.util.YogaMediaUtils
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: YogaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progressLogs by viewModel.progressLogs.collectAsState()
    val allPoses by viewModel.allPoses.collectAsState()

    var showCaptureDialog by remember { mutableStateOf(false) }
    var selectedLogForPreview by remember { mutableStateOf<ProgressTracker?>(null) }

    // Camera photo capture path
    var tempPhotoPath by remember { mutableStateOf<String?>(null) }
    var pendingPoseName by remember { mutableStateOf("Downward-Facing Dog") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoPath != null) {
            viewModel.addProgressLog(tempPhotoPath!!, pendingPoseName) {
                Toast.makeText(context, "Progress photo logged!", Toast.LENGTH_SHORT).show()
                showCaptureDialog = false
            }
        }
    }

    // Fallback Photo Picker for environments without a physical camera
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = copyUriToInternalStorage(context, uri)
            if (savedPath != null) {
                viewModel.addProgressLog(savedPath, pendingPoseName) {
                    Toast.makeText(context, "Progress photo logged!", Toast.LENGTH_SHORT).show()
                    showCaptureDialog = false
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCaptureDialog = true },
                icon = { Icon(Icons.Default.PhotoCamera, contentDescription = null) },
                text = { Text("Log Photo Proof") },
                containerColor = Color(0xFF15803D),
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_capture_proof")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Text(
                        text = "Personal Progress Gallery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F291E)
                    )
                    Text(
                        text = "${progressLogs.size} practice proofs recorded",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF15803D)
                    )
                }
            }

            if (progressLogs.isEmpty()) {
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
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = Color(0xFF15803D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Progress Photos Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F291E)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Capture real-time camera proofs of your yoga poses to build your visual practice journal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4B6358),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { showCaptureDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF15803D),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capture First Proof")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(progressLogs, key = { it.logId }) { log ->
                        ProgressPhotoCard(
                            log = log,
                            onClick = { selectedLogForPreview = log },
                            onDelete = { viewModel.deleteProgressLog(log.logId) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Capture / Upload Dialog
    if (showCaptureDialog) {
        CaptureProgressDialog(
            allPoses = allPoses,
            initialPose = pendingPoseName,
            onDismiss = { showCaptureDialog = false },
            onLaunchCamera = { poseName ->
                pendingPoseName = poseName
                try {
                    val file = YogaMediaUtils.createImageFile(context, "YOGA_PROOF")
                    tempPhotoPath = file.absolutePath
                    val uri = YogaMediaUtils.getUriForFile(context, file)
                    takePictureLauncher.launch(uri)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onPickFromGallery = { poseName ->
                pendingPoseName = poseName
                photoPickerLauncher.launch("image/*")
            }
        )
    }

    // Full Screen Preview Dialog
    selectedLogForPreview?.let { log ->
        PhotoPreviewDialog(
            log = log,
            onDismiss = { selectedLogForPreview = null },
            onDelete = {
                viewModel.deleteProgressLog(log.logId)
                selectedLogForPreview = null
            }
        )
    }
}

@Composable
fun ProgressPhotoCard(
    log: ProgressTracker,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("progress_card_${log.logId}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF0FDF4))
            ) {
                val file = remember(log.imagePath) { File(log.imagePath) }
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Yoga Proof: ${log.poseName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Delete quick icon
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete proof",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = log.poseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F291E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = log.dateLogged,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4B6358),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureProgressDialog(
    allPoses: List<YogaPose>,
    initialPose: String,
    onDismiss: () -> Unit,
    onLaunchCamera: (poseName: String) -> Unit,
    onPickFromGallery: (poseName: String) -> Unit
) {
    var selectedPoseName by remember { mutableStateOf(initialPose) }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Practice Proof")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select Pose Practiced:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPoseName,
                        onValueChange = { selectedPoseName = it },
                        readOnly = false,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        allPoses.take(25).forEach { pose ->
                            DropdownMenuItem(
                                text = { Text(pose.englishName) },
                                onClick = {
                                    selectedPoseName = pose.englishName
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Capture with Camera or select a photo from your gallery:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onLaunchCamera(selectedPoseName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF15803D),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Camera")
                    }

                    OutlinedButton(
                        onClick = { onPickFromGallery(selectedPoseName) },
                        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF15803D))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PhotoPreviewDialog(
    log: ProgressTracker,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    val file = remember(log.imagePath) { File(log.imagePath) }
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = log.poseName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = log.poseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Logged: ${log.dateLogged}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete Proof")
                    }

                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val targetFile = YogaMediaUtils.createImageFile(context, "YOGA_PROOF_GALLERY")
        val outputStream = FileOutputStream(targetFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        targetFile.absolutePath
    } catch (e: Exception) {
        null
    }
}
