package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.InventoryEntity

@Dao
interface InventoryDao {

    @Insert
    suspend fun insert(item: InventoryEntity)

    @Query("SELECT * FROM inventory WHERE type = :type LIMIT 1")
    suspend fun getItem(type: String): InventoryEntity?

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM inventory WHERE type = :type")
    suspend fun deleteByType(type: String)

    @Query("DELETE FROM inventory WHERE expiryDate < :today")
    suspend fun deleteExpired(today: String)
}
