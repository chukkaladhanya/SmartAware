package com.smartaware.monitor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkEventDao {
    @Insert
    suspend fun insertEvent(event: NetworkEvent)

    @Query("SELECT * FROM network_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<NetworkEvent>>

    @Query("SELECT * FROM network_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 100): Flow<List<NetworkEvent>>

    @Query("DELETE FROM network_events WHERE timestamp < :cutoffTime")
    suspend fun deleteOldEvents(cutoffTime: Long)

    @Query("DELETE FROM network_events")
    suspend fun deleteAllEvents()
}
