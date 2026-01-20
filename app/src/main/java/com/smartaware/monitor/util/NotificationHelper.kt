package com.smartaware.monitor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.smartaware.monitor.MainActivity
import com.smartaware.monitor.R

object NotificationHelper {
    
    private const val CHANNEL_ID_NETWORK = "network_status"
    private const val CHANNEL_ID_REMINDER = "wifi_reminder"
    
    private const val NOTIFICATION_ID_WIFI = 1001
    private const val NOTIFICATION_ID_CELLULAR = 1002
    private const val NOTIFICATION_ID_REMINDER = 1003

    fun createNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Network Status Channel
        val networkChannel = NotificationChannel(
            CHANNEL_ID_NETWORK,
            "Network Status",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for WiFi and Cellular status"
        }

        // WiFi Reminder Channel
        val reminderChannel = NotificationChannel(
            CHANNEL_ID_REMINDER,
            "WiFi Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Periodic reminders to turn on WiFi"
        }

        notificationManager.createNotificationChannel(networkChannel)
        notificationManager.createNotificationChannel(reminderChannel)
    }

    fun showWifiConnectedNotification(context: Context, signalStrength: Int, signalLevel: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_NETWORK)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📶 WiFi Connected")
            .setContentText("Signal: $signalStrength% ($signalLevel)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_WIFI, notification)
    }

    fun showCellularConnectedNotification(context: Context, networkType: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_NETWORK)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📱 Using Mobile Data")
            .setContentText("Network Type: $networkType")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_CELLULAR, notification)
    }

    fun showWifiReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⏰ WiFi Reminder")
            .setContentText("Turn on WiFi to save battery!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }
}
