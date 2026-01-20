package com.smartaware.monitor.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartaware.monitor.util.NetworkUtils
import com.smartaware.monitor.util.NotificationHelper

class WiFiReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Check if WiFi is currently connected
        val isWifiConnected = NetworkUtils.isWifiConnected(applicationContext)
        
        // If WiFi is not connected, send reminder notification
        if (!isWifiConnected) {
            NotificationHelper.showWifiReminderNotification(applicationContext)
        }
        
        return Result.success()
    }
}
