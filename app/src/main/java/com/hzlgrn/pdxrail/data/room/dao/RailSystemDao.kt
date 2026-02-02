package com.hzlgrn.pdxrail.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hzlgrn.pdxrail.data.room.entity.RailLineEntity
import com.hzlgrn.pdxrail.data.room.entity.RailStopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RailSystemDao {

    // region Rail Lines

    @Transaction @Query("SELECT * FROM ${RailLineEntity.TABLE_NAME} WHERE 1=1")
    fun railLines(): Flow<List<RailLineEntity>>

    @Query("DELETE FROM ${RailLineEntity.TABLE_NAME} WHERE 1=1")
    fun deleteAllRailLines()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRailLine(entities: List<RailLineEntity>)

    @Transaction
    fun updateRailLines(entities: List<RailLineEntity>) {
        deleteAllRailLines()
        insertRailLine(entities)
    }

    // endregion

    // region Rail Stops

    @Transaction @Query("SELECT * FROM ${RailStopEntity.TABLE_NAME} WHERE 1=1")
    fun railStops(): Flow<List<RailStopEntity>>

    @Query("DELETE FROM ${RailStopEntity.TABLE_NAME} WHERE 1=1")
    fun deleteAllRailStops()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRailStop(entities: List<RailStopEntity>)

    @Transaction
    fun updateRailStops(entities: List<RailStopEntity>) {
        deleteAllRailStops()
        insertRailStop(entities)
    }

    // endregion

    @Transaction
    fun updateRailSystem(railStops: List<RailStopEntity>, railLines: List<RailLineEntity>) {
        updateRailLines(railLines)
        updateRailStops(railStops)
    }

}