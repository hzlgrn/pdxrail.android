package com.hzlgrn.pdxrail.data.room.dao

import androidx.room.*
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
import com.hzlgrn.pdxrail.data.room.model.ArrivalMarker
import kotlinx.coroutines.flow.Flow

@Dao
interface TriMetDao {

    @Transaction @Query("SELECT * FROM ${ArrivalEntity.TABLE_NAME} WHERE locid IN (:locIds) ORDER BY scheduled ASC, estimated ASC limit 100")
    fun arrivalMarkersFor(locIds: List<Long>): Flow<List<ArrivalMarker>>

    @Transaction @Query("SELECT * FROM ${ArrivalEntity.TABLE_NAME} WHERE locid IN (:locIds) ORDER BY scheduled ASC, estimated ASC limit 100")
    fun arrivalItemsFor(locIds: List<Long>): Flow<List<ArrivalItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun create(models: List<ArrivalEntity>): List<Long>

    @Query("SELECT * FROM trimet_locid WHERE latlon = :latlon")
    fun getLocIdFor(latlon: String): LocIdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrReplace(entity: LocIdEntity): Long

    @Query("SELECT * FROM trimet_arrival WHERE locid IN (:csvLocIds)")
    fun allByLocIds(csvLocIds: List<Long>): List<ArrivalEntity>

    @Query("DELETE FROM trimet_arrival WHERE locid IN (:csvLocIds)")
    fun deleteCsvLocIds(csvLocIds: List<Long>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBlockPositions(entities: List<BlockPositionEntity>): List<Long>

    @Transaction
    fun updateArrivals(csvLocIds: List<Long>, entities: List<BlockPositionEntity>, models: List<ArrivalEntity>) {
        deleteCsvLocIds(csvLocIds)
        createBlockPositions(entities)
        create(models)
    }

}