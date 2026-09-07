package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FavoriteRoute
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRoute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(route: FavoriteRoute): Long

    @Delete
    suspend fun deleteFavorite(route: FavoriteRoute)

    @Query("DELETE FROM favorites WHERE fromStand = :fromStand AND toDestination = :toDest")
    suspend fun deleteByRoute(fromStand: String, toDest: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE fromStand = :fromStand AND toDestination = :toDest)")
    suspend fun isFavorite(fromStand: String, toDest: String): Boolean
}
