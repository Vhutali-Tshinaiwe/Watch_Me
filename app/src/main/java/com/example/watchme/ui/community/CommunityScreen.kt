package com.example.watchme.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watchme.data.repository.WatchMeRepository
import com.example.watchme.ui.components.FilterChipComponent
import com.example.watchme.ui.components.PillButton
import com.example.watchme.ui.components.ReportCard
import com.example.watchme.ui.components.WatchMeBottomNavBar
import com.example.watchme.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    repository: WatchMeRepository,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onReportClick: () -> Unit,
    onIncidentClick: (String) -> Unit,
    onNotificationClick: () -> Unit
) {
    val incidents by repository.incidents.collectAsState()
    var selectedDistanceFilter by remember { mutableStateOf("Within 5 km") }
    var selectedTypeFilter by remember { mutableStateOf("Type ⌄") }
    var selectedStatusFilter by remember { mutableStateOf("Status ⌄") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("42 Currie Rd, Mus - Durban", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(10.dp), color = PrimaryBlue) {
                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Community", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = onNotificationClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Filter Chips Row
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChipComponent(
                        label = "Within 5 km",
                        selected = selectedDistanceFilter == "Within 5 km",
                        onClick = { selectedDistanceFilter = "Within 5 km" }
                    )
                    FilterChipComponent(
                        label = "Type ⌄",
                        selected = false,
                        onClick = { }
                    )
                    FilterChipComponent(
                        label = "Status ⌄",
                        selected = false,
                        onClick = { }
                    )
                }
            }

            // "+ Report an incident" Primary Button
            item {
                PillButton(
                    text = "+ Report an incident",
                    onClick = onReportClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Header "Recent reports"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent reports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Latest update ⬇",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Reports Feed
            items(incidents) { report ->
                ReportCard(
                    report = report,
                    onClick = { onIncidentClick(report.id) },
                    onStillHappening = {
                        repository.submitUpdate(report.id, "Still happening", "Still present")
                    },
                    onItsOver = {
                        repository.submitUpdate(report.id, "It is over", "Situation resolved")
                    }
                )
            }

            // Footer note
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Discussions stay with each report. No general chat.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
