package com.example.watchme.ui.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watchme.data.model.IncidentType
import com.example.watchme.data.repository.WatchMeRepository
import com.example.watchme.ui.components.PillButton
import com.example.watchme.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(
    repository: WatchMeRepository,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1 state
    var selectedType by remember { mutableStateOf(IncidentType.CRIME) }
    var description by remember { mutableStateOf("") }
    var whenHappened by remember { mutableStateOf("Just now") }
    var isStillHappening by remember { mutableStateOf(true) }

    // Step 2 state
    var mediaList by remember { mutableStateOf(mutableStateListOf("Photo 1 (simulated)", "Photo 2 (simulated)")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report an incident", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            // Progress Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Step $currentStep of 3", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        val stepNum = index + 1
                        val active = stepNum <= currentStep
                        Surface(
                            shape = CircleShape,
                            color = if (active) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(if (stepNum == currentStep) 12.dp else 8.dp)
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (currentStep) {
                1 -> {
                    // STEP 1: What did you see?
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("What did you see?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Help keep your community informed.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Type Grid
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                IncidentType.entries.chunked(2).forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                        row.forEach { type ->
                                            val selected = selectedType == type
                                            Card(
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.surface
                                                ),
                                                border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { selectedType = type }
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp)) {
                                                    Text(
                                                        text = type.displayName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = type.category,
                                                        fontSize = 11.sp,
                                                        color = if (selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Description
                        item {
                            Text("Add a short description", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Describe what you saw e.g. what happened, how many people, direction, etc.", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // When did this happen?
                        item {
                            Text("When did this happen?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("Just now", "Earlier").forEach { option ->
                                    val selected = whenHappened == option
                                    Button(
                                        onClick = { whenHappened = option },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(option, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Is still happening?
                        item {
                            Text("Is still happening?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("Yes, right now." to true, "No, it's over." to false).forEach { (label, value) ->
                                    val selected = isStillHappening == value
                                    Button(
                                        onClick = { isStillHappening = value },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    PillButton(
                        text = "Next ➔",
                        onClick = { currentStep = 2 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                2 -> {
                    // STEP 2: Add photos or videos
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Add photos or videos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Photos and videos help others understand what's happening.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Camera Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Take a photo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Use your camera to capture the incident", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                PillButton(text = "Open camera", onClick = {
                                    mediaList.add("Captured Photo ${mediaList.size + 1}")
                                })
                            }
                        }

                        // Gallery Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Image, contentDescription = "Gallery", tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Choose from gallery", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Upload photos or videos from your device", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                PillButton(text = "Choose files", onClick = {
                                    mediaList.add("Gallery File ${mediaList.size + 1}")
                                })
                            }
                        }

                        // Attached Media List
                        if (mediaList.isNotEmpty()) {
                            Text("Attached media (${mediaList.size}/5)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(mediaList.size) { idx ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrimaryBlue.copy(alpha = 0.15f),
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("📷 #${idx + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                            IconButton(
                                                onClick = { mediaList.removeAt(idx) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        PillButton(text = "< Back", onClick = { currentStep = 1 }, isPrimary = false, modifier = Modifier.weight(1f))
                        PillButton(text = "Next ➔", onClick = { currentStep = 3 }, modifier = Modifier.weight(1f))
                    }
                }

                3 -> {
                    // STEP 3: Review and submit
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Review and submit", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Check the details before sharing with the community.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Editable Rows Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Row 1: Type
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Incident type", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(selectedType.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        TextButton(onClick = { currentStep = 1 }) { Text("Edit", fontSize = 12.sp, color = PrimaryBlue) }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                    // Row 2: Description
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Description", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(description.ifBlank { "No detailed description added." }, fontSize = 13.sp)
                                        }
                                        TextButton(onClick = { currentStep = 1 }) { Text("Edit", fontSize = 12.sp, color = PrimaryBlue) }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                    // Row 3: When
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("When did this happen?", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(whenHappened, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        TextButton(onClick = { currentStep = 1 }) { Text("Edit", fontSize = 12.sp, color = PrimaryBlue) }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                    // Row 4: Is Still Happening
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Is it still happening?", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(if (isStillHappening) "Yes, right now." else "No, it's over.", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        TextButton(onClick = { currentStep = 1 }) { Text("Edit", fontSize = 12.sp, color = PrimaryBlue) }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                    // Row 5: Location Mini Map Preview
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Location", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("142 Bluff, Bluff Rd", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFCBD5E1),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(80.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("📍 Bluff, Durban · Mini Map", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    PillButton(
                        text = "Submit report 🚀",
                        onClick = {
                            repository.submitNewReport(
                                type = selectedType,
                                description = description,
                                whenHappened = whenHappened,
                                isStillHappening = isStillHappening,
                                locationName = "142 Bluff, Bluff Rd"
                            )
                            onSubmitSuccess()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
