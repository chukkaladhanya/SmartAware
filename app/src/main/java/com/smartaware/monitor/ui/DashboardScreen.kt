package com.smartaware.monitor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartaware.monitor.data.AppDatabase
import com.smartaware.monitor.data.NetworkEvent
import com.smartaware.monitor.data.NetworkEventType
import com.smartaware.monitor.util.NetworkUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }
    
    val events by database.networkEventDao().getRecentEvents(100).collectAsState(initial = emptyList())
    
    var currentNetworkType by remember { mutableStateOf("Checking...") }
    var wifiSignal by remember { mutableStateOf("N/A") }

    // Update network status
    LaunchedEffect(Unit) {
        currentNetworkType = NetworkUtils.getCurrentNetworkType(context)
        if (NetworkUtils.isWifiConnected(context)) {
            val strength = NetworkUtils.getWifiSignalStrength(context)
            val level = NetworkUtils.getWifiSignalLevel(strength)
            wifiSignal = "$strength% ($level)"
        } else {
            wifiSignal = "N/A"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartAware") },
                actions = {
                    IconButton(onClick = {
                        currentNetworkType = NetworkUtils.getCurrentNetworkType(context)
                        if (NetworkUtils.isWifiConnected(context)) {
                            val strength = NetworkUtils.getWifiSignalStrength(context)
                            val level = NetworkUtils.getWifiSignalLevel(strength)
                            wifiSignal = "$strength% ($level)"
                        } else {
                            wifiSignal = "N/A"
                        }
                    }) {
                        Icon(Icons.Default.NetworkCheck, "Check Network")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            database.networkEventDao().deleteAllEvents()
                        }
                    }) {
                        Icon(Icons.Default.Delete, "Clear logs")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Network Status Card
            NetworkStatusCard(
                networkType = currentNetworkType,
                wifiSignal = wifiSignal
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Event Logs
            Text(
                text = "Event Logs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events logged yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(events) { event ->
                        LogItem(event = event)
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkStatusCard(networkType: String, wifiSignal: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Current Network Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Network Type",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = networkType,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (wifiSignal != "N/A") {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "WiFi Signal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = wifiSignal,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogItem(event: NetworkEvent) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()) }
    val emoji = when (event.eventType) {
        NetworkEventType.WIFI_ON -> "✅"
        NetworkEventType.WIFI_OFF -> "❌"
        NetworkEventType.CELLULAR_ON -> "✅"
        NetworkEventType.CELLULAR_OFF -> "❌"
    }
    
    val eventTitle = when (event.eventType) {
        NetworkEventType.WIFI_ON -> "WiFi Turned ON"
        NetworkEventType.WIFI_OFF -> "WiFi Turned OFF"
        NetworkEventType.CELLULAR_ON -> "Cellular Turned ON"
        NetworkEventType.CELLULAR_OFF -> "Cellular Turned OFF"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eventTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                if (event.details != null) {
                    Text(
                        text = event.details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(event.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
