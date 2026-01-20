package com.smartaware.monitor.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.smartaware.monitor.data.AppDatabase
import com.smartaware.monitor.data.NetworkEvent
import com.smartaware.monitor.data.NetworkEventType
import com.smartaware.monitor.util.NetworkUtils
import com.smartaware.monitor.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                handleNetworkChange(context)
            }
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                handleWifiStateChange(context, intent)
            }
        }
    }

    private fun handleNetworkChange(context: Context) {
        val isWifiConnected = NetworkUtils.isWifiConnected(context)
        val isCellularConnected = NetworkUtils.isCellularConnected(context)

        when {
            isWifiConnected -> {
                val signalStrength = NetworkUtils.getWifiSignalStrength(context)
                val signalLevel = NetworkUtils.getWifiSignalLevel(signalStrength)
                
                // Show notification
                NotificationHelper.showWifiConnectedNotification(context, signalStrength, signalLevel)
                
                // Log event
                logEvent(context, NetworkEventType.WIFI_ON, "Signal: $signalStrength% ($signalLevel)")
            }
            isCellularConnected -> {
                val networkType = NetworkUtils.getCellularNetworkType(context)
                
                // Show notification
                NotificationHelper.showCellularConnectedNotification(context, networkType)
                
                // Log event
                logEvent(context, NetworkEventType.CELLULAR_ON, networkType)
            }
        }
    }

    private fun handleWifiStateChange(context: Context, intent: Intent) {
        val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
        
        when (wifiState) {
            WifiManager.WIFI_STATE_ENABLED -> {
                logEvent(context, NetworkEventType.WIFI_ON, "WiFi enabled")
            }
            WifiManager.WIFI_STATE_DISABLED -> {
                logEvent(context, NetworkEventType.WIFI_OFF, "WiFi disabled")
            }
        }
    }

    private fun logEvent(context: Context, eventType: NetworkEventType, details: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            val event = NetworkEvent(
                timestamp = System.currentTimeMillis(),
                eventType = eventType,
                details = details
            )
            database.networkEventDao().insertEvent(event)
        }
    }
}
