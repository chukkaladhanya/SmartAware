package com.smartaware.monitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NetworkEventType {
    WIFI_ON,      // WiFi Turned ON
    WIFI_OFF,     // WiFi Turned OFF
    CELLULAR_ON,  // Cellular Turned ON
    CELLULAR_OFF  // Cellular Turned OFF
}

@Entity(tableName = "network_events")
data class NetworkEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val eventType: NetworkEventType,
    val details: String? = null  // e.g., "Signal: 85%", "4G Network"
)
