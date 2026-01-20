package com.smartaware.monitor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartaware.monitor.MainActivity

class NetworkMonitorService : Service() {

    private lateinit var networkReceiver: NetworkStateReceiver
    private val CHANNEL_ID = "network_monitor_service"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        networkReceiver = NetworkStateReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createForegroundNotification())
        registerReceivers()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            // Receiver not registered
        }
    }

    private fun registerReceivers() {
        val intentFilter = IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }
        registerReceiver(networkReceiver, intentFilter)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Network Monitor Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background monitoring of network status"
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SmartAware")
            .setContentText("Monitoring network status...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }
}
