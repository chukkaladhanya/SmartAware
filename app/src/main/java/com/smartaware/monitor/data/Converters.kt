package com.smartaware.monitor.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromNetworkEventType(value: NetworkEventType): String {
        return value.name
    }

    @TypeConverter
    fun toNetworkEventType(value: String): NetworkEventType {
        return NetworkEventType.valueOf(value)
    }
}
