package com.hzlgrn.pdxrail.data.room.dao

import androidx.room.*
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
import com.hzlgrn.pdxrail.data.room.model.ArrivalMarker
import kotlinx.coroutines.flow.Flow

@Dao
interface ArrivalDao {

    @Transaction @Query("""
        SELECT * FROM ${ArrivalEntity.TABLE_NAME}
        WHERE locid IN (:listLocId)
        ORDER BY
            scheduled ASC,
            estimated ASC limit 100 
    """)
    fun arrivalMarkersFor(listLocId: List<Long>): Flow<List<ArrivalMarker>>

    @Transaction @Query("""
        SELECT * FROM ${ArrivalEntity.TABLE_NAME}
        WHERE locid IN (:listLocId)
        ORDER BY
            scheduled ASC,
            estimated ASC limit 100
    """)
    fun arrivalItemsFor(listLocId: List<Long>): Flow<List<ArrivalItem>>


    @Query("SELECT * FROM ${LocIdEntity.TABLE_NAME} WHERE latlon = :latlon")
    fun getLocIdFor(latlon: String): LocIdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrReplace(entity: LocIdEntity): Long



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createArrivalEntities(models: List<ArrivalEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBlockPositions(entities: List<BlockPositionEntity>): List<Long>

    @Query("DELETE FROM ${ArrivalEntity.TABLE_NAME} WHERE locid IN (:listLocId)")
    fun deleteArrivals(listLocId: List<Long>)



    @Transaction
    fun updateArrivals(
        csvLocIds: List<Long>,
        blockPositionEntities: List<BlockPositionEntity>,
        arrivalEntities: List<ArrivalEntity>) {

        deleteArrivals(csvLocIds)
        createBlockPositions(blockPositionEntities)
        createArrivalEntities(arrivalEntities)
    }

}