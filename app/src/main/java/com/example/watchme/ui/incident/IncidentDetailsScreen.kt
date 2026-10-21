package com.example.watchme.ui.incident

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watchme.data.model.IncidentReport
import com.example.watchme.data.repository.WatchMeRepository
import com.example.watchme.ui.components.PillButton
import com.example.watchme.ui.components.StatusBadge
import com.example.watchme.ui.components.WatchMeBottomNavBar
import com.example.watchme.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailsScreen(
    repository: WatchMeRepository,
    incidentId: String,
    onBackClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val incidents by repository.incidents.collectAsState()
    val report = incidents.find { it.id == incidentId } ?: incidents.first()

    var showAddUpdateSheet by remember { mutableStateOf(false) }
    var showObservationReceivedSheet by remember { mutableStateOf(false) }
    var showFlagSheet by remember { mutableStateOf(false) }

    var selectedObservationOption by remember { mutableStateOf("Still happening") }
    var observationText by remember { mutableStateOf("") }
    var flagReason by remember { mutableStateOf("Inaccurate location or information") }
    var flagContext by remember { mutableStateOf("") }
    var lastSubmittedOption by remember { mutableStateOf("Still happening") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Incident details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = PrimaryBlue) {
                            Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            WatchMeBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            item {
                Column {
                    Text(
                        text = report.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusBadge(status = report.status)
                        Text("• ${report.type.displayName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• ${report.id}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${report.locationName} · ${report.distanceText} · ${report.fullDateText}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Photo Strip (Simulated thumbnails)
            item {
                Column {
                    Text("Media photos (2)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryBlue.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(100.dp, 70.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📷 Photo 1", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryBlue.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(100.dp, 70.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("📷 Photo 2", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Community Corroborated Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👥 Community corroborated", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${report.confirmationCount} community confirmations. Reports describe a gathering; impact remains uncertain.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No official update available",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Updates & Discussion Timeline
            item {
                Column {
                    Text("Updates & discussion", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))

                    report.updates.forEach { update ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(update.timestamp, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.width(50.dp))
                                Column {
                                    Text(update.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(update.note, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // What Are You Observing? Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("What are you observing?", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Still happening", "Getting worse", "It is over").forEach { option ->
                                OutlinedButton(
                                    onClick = {
                                        selectedObservationOption = option
                                        showAddUpdateSheet = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text(option, fontSize = 11.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PillButton(
                                text = "Add update",
                                onClick = { showAddUpdateSheet = true },
                                modifier = Modifier.weight(1f)
                            )
                            PillButton(
                                text = "Flag",
                                onClick = { showFlagSheet = true },
                                isPrimary = false,
                                icon = Icons.Default.Flag,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Share only what you observe. No names or identifying accusations. Expiry archives a report; it does not resolve it.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Add An Update Bottom Sheet
    if (showAddUpdateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddUpdateSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Add an update", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("${report.id} · ${report.title}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Text("What changed?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "Still happening" to "No change in activity observed",
                    "Getting worse" to "Only if you observed worsening activity",
                    "It is over" to "Needs corroboration before resolution"
                ).forEach { (option, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedObservationOption = option }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedObservationOption == option,
                            onClick = { selectedObservationOption = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(option, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Your observation", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = observationText,
                    onValueChange = { observationText = it },
                    placeholder = { Text("Describe what you observe...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No names, personal trails or identifying accusations. Demo only; not emergency dispatch.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillButton(
                    text = "Submit update",
                    onClick = {
                        repository.submitUpdate(report.id, selectedObservationOption, observationText)
                        lastSubmittedOption = selectedObservationOption
                        showAddUpdateSheet = false
                        showObservationReceivedSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Observation Received Bottom Sheet
    if (showObservationReceivedSheet) {
        ModalBottomSheet(
            onDismissRequest = { showObservationReceivedSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF16A34A), modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Observation received", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your \"$lastSubmittedOption\" update is saved.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${report.id} - simulated receipt at 14:32 SAST. Confirmation count is now ${report.confirmationCount} in this displayed state.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatusBadge(status = report.status)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Still happening does not mean escalating. 'Getting worse' needs explicit worsening evidence. No real message or dispatch occurred.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                PillButton(
                    text = "Back to report",
                    onClick = { showObservationReceivedSheet = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Flag This Report Bottom Sheet
    if (showFlagSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFlagSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Flag this report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${report.id} · Choose a reason. A flag requests review; it does not prove the report is false.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    "Inaccurate location or information",
                    "Personal or identifying information",
                    "Duplicate report",
                    "Abusive or harmful content"
                ).forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { flagReason = reason }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = flagReason == reason,
                            onClick = { flagReason = reason }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(reason, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Extra context (optional)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = flagContext,
                    onValueChange = { flagContext = it },
                    placeholder = { Text("Describe the issue without repeating personal details.", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillButton(
                    text = "Send flag",
                    onClick = { showFlagSheet = false },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Review and delivery are simulated.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
