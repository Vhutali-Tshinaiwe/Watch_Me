package com.example.watchme.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watchme.data.model.IncidentReport
import com.example.watchme.data.model.IncidentStatus
import com.example.watchme.data.repository.WatchMeRepository
import com.example.watchme.ui.components.FilterChipComponent
import com.example.watchme.ui.components.PillButton
import com.example.watchme.ui.components.StatusBadge
import com.example.watchme.ui.components.WatchMeBottomNavBar
import com.example.watchme.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    repository: WatchMeRepository,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onIncidentDetailsClick: (String) -> Unit,
    onReportClick: () -> Unit,
    onSosClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val incidents by repository.incidents.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedIncident by remember { mutableStateOf<IncidentReport?>(incidents.firstOrNull()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showHistoricalSheet by remember { mutableStateOf(false) }

    // Filter sheet states
    var selectedStatuses by remember { mutableStateOf(setOf("Developing", "Escalating")) }
    var selectedType by remember { mutableStateOf("All types") }
    var showHistoricalOverlay by remember { mutableStateOf(true) }
    var showWatchZoneOverlay by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            WatchMeBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Search Bar & Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("42 Currie Rd, Mus - Durban", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Around you", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row {
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.Person, contentDescription = "Profile")
                            }
                            IconButton(onClick = onNotificationClick) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search areas around you", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // Simulated Map Canvas View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFE2E8F0))
                ) {
                    // Map Overlay Graphics (Simulated Streets, Watch Zones & Pins)
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Dashed Watch Zone Circle (Home Zone)
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue.copy(alpha = 0.12f),
                            border = BorderStroke(2.dp, PrimaryBlue),
                            modifier = Modifier
                                .size(180.dp)
                                .align(Alignment.CenterStart)
                                .offset(x = 30.dp, y = (-20).dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Home zone", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Historical Hotspot Area Overlay
                        if (showHistoricalOverlay) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEA580C).copy(alpha = 0.15f),
                                border = BorderStroke(1.5.dp, Color(0xFFEA580C)),
                                modifier = Modifier
                                    .size(130.dp)
                                    .align(Alignment.BottomStart)
                                    .offset(x = 20.dp, y = (-60).dp)
                                    .clickable { showHistoricalSheet = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Historic - H-01", fontSize = 10.sp, color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Pin 1: Developing
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEA580C),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.Center)
                                .offset(x = 40.dp, y = 20.dp)
                                .clickable { selectedIncident = incidents.getOrNull(0) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Warning, contentDescription = "Developing", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Pin 2: Escalating
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-60).dp, y = 60.dp)
                                .clickable { selectedIncident = incidents.getOrNull(1) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FireTruck, contentDescription = "Escalating", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }

                        // Top Map Chip Legend
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
                                Text("● Live report", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
                                Text("● Historical", fontSize = 10.sp, color = Color(0xFFEA580C), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
                                Text("● Watch zone", fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        // Floating "+ Report" Pill Button
                        Button(
                            onClick = onReportClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Report", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                // Bottom Incident Preview Card & SOS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    selectedIncident?.let { incident ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIncidentDetailsClick(incident.id) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusBadge(status = incident.status)
                                    Text("${incident.distanceText} · ${incident.timeText}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(incident.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Community corroborated · ${incident.confirmationCount}+", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    TextButton(onClick = { onIncidentDetailsClick(incident.id) }) {
                                        Text("Details ↗", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Red SOS Button
                    Button(
                        onClick = onSosClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("- Tap & Hold for SOS -", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // Map Filters Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Map filters", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("42 Currie Rd, Mus - Durban· within 5 km", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Report status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Developing", "Escalating", "Cooling down", "Resolved").forEach { status ->
                        val selected = selectedStatuses.contains(status)
                        FilterChipComponent(
                            label = status,
                            selected = selected,
                            onClick = {
                                selectedStatuses = if (selected) selectedStatuses - status else selectedStatuses + status
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Incident type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All types", "Protest", "Roadblock").forEach { type ->
                        FilterChipComponent(
                            label = type,
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Historical Hotspots Toggle
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Historical hotspots", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Past reports only - not live warnings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = showHistoricalOverlay,
                            onCheckedChange = { showHistoricalOverlay = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Watch Zones Toggle
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Watch zones", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Home - Work - School boundaries", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = showWatchZoneOverlay,
                            onCheckedChange = { showWatchZoneOverlay = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Expired reports are archived, not marked resolved.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                PillButton(
                    text = "Show results",
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        selectedStatuses = setOf("Developing", "Escalating")
                        selectedType = "All types"
                        showHistoricalOverlay = true
                        showWatchZoneOverlay = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset filters", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // Historical Context Bottom Sheet
    if (showHistoricalSheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistoricalSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Historical context", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFEF3C7)) {
                        Text("Historical - not a live incident", fontSize = 11.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("H-01 · Paperbark sector", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("This fictional pattern is historical context, not a prediction or a label that an intersection is dangerous.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("• Record period: 01 Jul - 30 Sep 2026", fontSize = 12.sp)
                        Text("• Coverage: fictional 500 m sector · Durban", fontSize = 12.sp)
                        Text("• Source: seeded demo dataset · 12 records", fontSize = 12.sp)
                        Text("• Refreshed: 09 Oct 2026 · 9 days ago", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Incomplete coverage; not independently verified.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PillButton(
                    text = "Hide historical layer",
                    onClick = {
                        showHistoricalOverlay = false
                        showHistoricalSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                PillButton(
                    text = "Back to map",
                    onClick = { showHistoricalSheet = false },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
