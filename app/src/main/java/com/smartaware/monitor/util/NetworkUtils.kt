package com.smartaware.monitor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager

object NetworkUtils {
    
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun isCellularConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    fun getWifiSignalStrength(context: Context): Int {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val rssi = wifiInfo.rssi
            // Convert RSSI to percentage (0-100)
            return when {
                rssi >= -50 -> 100
                rssi <= -100 -> 0
                else -> 2 * (rssi + 100)
            }
        } catch (e: Exception) {
            return 0
        }
    }

    fun getWifiSignalLevel(strength: Int): String {
        return when {
            strength >= 80 -> "Excellent"
            strength >= 60 -> "Good"
            strength >= 40 -> "Fair"
            strength >= 20 -> "Weak"
            else -> "Poor"
        }
    }

    fun getCellularNetworkType(context: Context): String {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_NR -> "5G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT -> "2G"
                else -> "Mobile Data"
            }
        } catch (e: Exception) {
            return "Mobile Data"
        }
    }

    fun getCurrentNetworkType(context: Context): String {
        return when {
            isWifiConnected(context) -> "WiFi"
            isCellularConnected(context) -> getCellularNetworkType(context)
            else -> "No Connection"
        }
    }
}
