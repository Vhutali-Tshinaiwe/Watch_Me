package com.example.watchme.data.repository

import com.example.watchme.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WatchMeRepository {

    private val _incidents = MutableStateFlow(
        listOf(
            IncidentReport(
                id = "WM-101",
                title = "Gathering near Theworkshop, centre",
                type = IncidentType.CRIME,
                status = IncidentStatus.DEVELOPING,
                locationName = "CBD - Durban",
                distanceText = "650 m away",
                timeText = "Updated 2 min ago",
                fullDateText = "10 Oct 2026 - 14:10",
                confirmationCount = 21,
                isCommunityCorroborated = true,
                description = "21 community confirmations. Reports describe a gathering; impact remains uncertain. No official update available.",
                whenHappened = "Just now",
                isStillHappening = true,
                images = listOf("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"),
                latitude = -29.8587,
                longitude = 31.0218,
                updates = listOf(
                    IncidentTimelineUpdate("u1", "14:28", "Gathering still reported", "15+ confirmations - no worsening evidence"),
                    IncidentTimelineUpdate("u2", "14:10", "First community report", "Unconfirmed -> community corroborated")
                )
            ),
            IncidentReport(
                id = "WM-102",
                title = "Road access restricted",
                type = IncidentType.ROAD_ISSUE,
                status = IncidentStatus.ESCALATING,
                locationName = "Berea Rd - Durban",
                distanceText = "1.2 km away",
                timeText = "14:26",
                fullDateText = "10 Oct 2026 - 14:00",
                confirmationCount = 5,
                isCommunityCorroborated = true,
                description = "Road blockade and burning tires reported on Berea Rd. Traffic diverted.",
                whenHappened = "Just now",
                isStillHappening = true,
                images = emptyList(),
                latitude = -29.8530,
                longitude = 31.0100,
                updates = listOf(
                    IncidentTimelineUpdate("u3", "14:26", "Two updates explicitly report worsening access", "Getting worse")
                )
            ),
            IncidentReport(
                id = "WM-103",
                title = "Fire activity decreasing",
                type = IncidentType.FIRE,
                status = IncidentStatus.COOLING_DOWN,
                locationName = "Devonport - Durban",
                distanceText = "2.1 km away",
                timeText = "14:20",
                fullDateText = "10 Oct 2026 - 13:45",
                confirmationCount = 12,
                isCommunityCorroborated = true,
                description = "Grass fire contained by local response team. Smoke clearing.",
                whenHappened = "Earlier",
                isStillHappening = false,
                images = emptyList(),
                latitude = -29.8650,
                longitude = 31.0300,
                updates = listOf(
                    IncidentTimelineUpdate("u4", "14:20", "Multiple confirmations situation is calming", "It is over")
                )
            )
        )
    )
    val incidents: StateFlow<List<IncidentReport>> = _incidents.asStateFlow()

    private val _circleContacts = MutableStateFlow(
        listOf(
            CircleContact("c1", "NM", "Naledi Gumede", "sister", "Accepted"),
            CircleContact("c2", "TD", "Thabo Dlamini", "friend", "Accepted"),
            CircleContact("c3", "AK", "Ayesha Madi", "friend", "Pending", "Invited 14:00")
        )
    )
    val circleContacts: StateFlow<List<CircleContact>> = _circleContacts.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("n1", "Road access restricted", "WM-102 - Berea Rd - 14:26 Two updates explicitly report worsening access.", "14:26", IncidentStatus.ESCALATING),
            NotificationItem("n2", "Live sharing ends at 15:00", "Naledi & Thabo · 28 min remaining", "14:32", null)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _archivedIncidents = MutableStateFlow<List<IncidentReport>>(emptyList())
    val archivedIncidents: StateFlow<List<IncidentReport>> = _archivedIncidents.asStateFlow()

    fun archiveExpiredReports() {
        // Expired reports are archived, NOT marked as resolved
        val active = _incidents.value.filter { it.status != IncidentStatus.RESOLVED }
        val expired = _incidents.value.filter { it.status == IncidentStatus.RESOLVED }
        _archivedIncidents.value = _archivedIncidents.value + expired
        _incidents.value = active
    }

    fun submitUpdate(reportId: String, newStatusOption: String, observation: String) {
        val currentList = _incidents.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == reportId }
        if (index != -1) {
            val oldReport = currentList[index]
            val newCount = oldReport.confirmationCount + 1
            // Simple status rules:
            // "It is over" -> Cooling down
            // "Getting worse" -> Escalating
            // "Still happening" -> retains existing status
            val newStatus = when (newStatusOption) {
                "It is over", "It is over\nNeeds corroboration before resolution" -> IncidentStatus.COOLING_DOWN
                "Getting worse", "Getting worse\nOnly if you observed worsening activity" -> IncidentStatus.ESCALATING
                else -> oldReport.status
            }
            val newUpdate = IncidentTimelineUpdate(
                id = "u_${System.currentTimeMillis()}",
                timestamp = "Just now",
                title = "User observation added",
                note = observation.ifBlank { "User submitted: $newStatusOption" }
            )
            val updatedReport = oldReport.copy(
                status = newStatus,
                confirmationCount = newCount,
                updates = listOf(newUpdate) + oldReport.updates
            )
            currentList[index] = updatedReport
            _incidents.value = currentList
        }
    }

    fun submitNewReport(
        type: IncidentType,
        description: String,
        whenHappened: String,
        isStillHappening: Boolean,
        locationName: String
    ): String {
        val newId = "WM-${100 + _incidents.value.size + 1}"
        val newReport = IncidentReport(
            id = newId,
            title = "${type.displayName} reported",
            type = type,
            status = if (isStillHappening) IncidentStatus.DEVELOPING else IncidentStatus.COOLING_DOWN,
            locationName = locationName,
            distanceText = "350 m away",
            timeText = "Just now",
            fullDateText = "Just now",
            confirmationCount = 1,
            isCommunityCorroborated = true,
            description = description.ifBlank { "${type.displayName} reported nearby." },
            whenHappened = whenHappened,
            isStillHappening = isStillHappening
        )
        _incidents.value = listOf(newReport) + _incidents.value
        return newId
    }

    fun addCircleInvite(name: String, email: String) {
        val initials = name.take(2).uppercase()
        val newContact = CircleContact(
            id = "c_${System.currentTimeMillis()}",
            initials = if (initials.length == 2) initials else "C",
            name = name,
            relationship = "friend",
            status = "Pending",
            inviteTime = "Invited just now"
        )
        _circleContacts.value = _circleContacts.value + newContact
    }
}
