package com.example.watchme.data.model

enum class IncidentStatus(val displayName: String) {
    DEVELOPING("Developing"),
    ESCALATING("Escalating"),
    COOLING_DOWN("Cooling down"),
    RESOLVED("Resolved");

    companion object {
        fun fromString(value: String): IncidentStatus {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: DEVELOPING
        }
    }
}

enum class IncidentType(val displayName: String, val category: String) {
    CRIME("Crime", "Robbery, hijacking, violence"),
    ROAD_ISSUE("Road issue", "Accident, hazard, blockade"),
    SUSPICIOUS_ACTIVITY("Suspicious activity", "Suspicious people, prowling"),
    FIRE("Fire", "Fire, smoke, gas leak"),
    SERVICE_DISRUPTION("Service disruption", "Water, power, outage"),
    OTHER("Other", "Something else");

    companion object {
        fun fromString(value: String): IncidentType {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

data class IncidentTimelineUpdate(
    val id: String,
    val timestamp: String,
    val title: String,
    val note: String
)

data class IncidentReport(
    val id: String, // e.g. "WM-101"
    val title: String,
    val type: IncidentType,
    val status: IncidentStatus,
    val locationName: String,
    val distanceText: String, // e.g. "650 m away"
    val timeText: String, // e.g. "14:28" or "2 min ago"
    val fullDateText: String = "10 Oct 2026 - 14:10",
    val confirmationCount: Int,
    val isCommunityCorroborated: Boolean = true,
    val description: String,
    val whenHappened: String = "Just now",
    val isStillHappening: Boolean = true,
    val images: List<String> = emptyList(),
    val latitude: Double = -29.8587,
    val longitude: Double = 31.0218,
    val updates: List<IncidentTimelineUpdate> = emptyList()
)

data class CircleContact(
    val id: String,
    val initials: String,
    val name: String,
    val relationship: String,
    val status: String, // "Accepted" or "Pending"
    val inviteTime: String? = null
)

data class NotificationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timeText: String,
    val status: IncidentStatus? = null
)
