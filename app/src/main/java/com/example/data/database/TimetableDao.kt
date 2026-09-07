package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BusTimetable
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable ORDER BY departureTime ASC")
    fun getAllTimetables(): Flow<List<BusTimetable>>

    @Query("SELECT * FROM timetable WHERE depotOrBusStand = :depot OR depotOrBusStandMr = :depot ORDER BY departureTime ASC")
    fun getBusesFromDepot(depot: String): Flow<List<BusTimetable>>

    @Query("SELECT * FROM timetable WHERE destination = :destination OR destinationMr = :destination ORDER BY departureTime ASC")
    fun getBusesToDestination(destination: String): Flow<List<BusTimetable>>

    @Query("""
        SELECT * FROM timetable 
        WHERE (depotOrBusStand = :fromStand OR depotOrBusStandMr = :fromStand) 
          AND (destination = :toDest OR destinationMr = :toDest)
        ORDER BY departureTime ASC
    """)
    fun getBusesBetween(fromStand: String, toDest: String): Flow<List<BusTimetable>>

    @Query("SELECT DISTINCT depotOrBusStand FROM timetable")
    suspend fun getAllDepots(): List<String>

    @Query("SELECT DISTINCT destination FROM timetable")
    suspend fun getAllDestinations(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timetable: BusTimetable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timetables: List<BusTimetable>)

    @Update
    suspend fun update(timetable: BusTimetable)

    @Delete
    suspend fun delete(timetable: BusTimetable)

    @Query("DELETE FROM timetable WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM timetable")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM timetable")
    suspend fun getCount(): Int

    @Query("SELECT exactDepartureTime FROM timetable LIMIT 1")
    suspend fun getSampleExactDepartureTime(): String?

    @Query("SELECT * FROM timetable")
    suspend fun getAllTimetablesList(): List<BusTimetable>

    @Query("SELECT EXISTS(SELECT 1 FROM timetable WHERE busRouteMr LIKE '%अंजनखेड%' LIMIT 1)")
    suspend fun hasAnjankhedRoute(): Boolean
}
