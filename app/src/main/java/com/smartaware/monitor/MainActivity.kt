package com.smartaware.monitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.*
import com.smartaware.monitor.service.NetworkMonitorService
import com.smartaware.monitor.ui.DashboardScreen
import com.smartaware.monitor.ui.theme.SmartAwareTheme
import com.smartaware.monitor.util.NotificationHelper
import com.smartaware.monitor.worker.WiFiReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private var hasNotificationPermission by mutableStateOf(false)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            setupServiceAndWorker()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                setupServiceAndWorker()
            }
        } else {
            hasNotificationPermission = true
            setupServiceAndWorker()
        }

        setContent {
            SmartAwareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionRequestScreen {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        DashboardScreen()
                    }
                }
            }
        }
    }

    private fun setupServiceAndWorker() {
        // Start network monitoring service
        val serviceIntent = Intent(this, NetworkMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Setup periodic WiFi reminder (every 6 hours)
        val wifiReminderRequest = PeriodicWorkRequestBuilder<WiFiReminderWorker>(
            6, TimeUnit.HOURS,
            15, TimeUnit.MINUTES // Flex interval
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "wifi_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            wifiReminderRequest
        )
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📱 SmartAware",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Permission Required",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "SmartAware needs notification permission to alert you about network status changes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}
