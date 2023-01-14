package com.hzlgrn.pdxrail.data.room.dao

import androidx.room.*
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import com.hzlgrn.pdxrail.data.room.model.ArrivalItem
import com.hzlgrn.pdxrail.data.room.model.ArrivalMarker
import com.hzlgrn.pdxrail.data.room.model.PkArrival
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
        SELECT id FROM ${ArrivalEntity.TABLE_NAME}
        WHERE locid IN (:listLocId)
        ORDER BY
            scheduled ASC,
            estimated ASC limit 100
    """)
    fun arrivalItemsFor(listLocId: List<Long>): Flow<List<PkArrival>>

    @Transaction @Query("""
        SELECT * FROM ${ArrivalEntity.TABLE_NAME}
        WHERE id = :uniqueId
        ORDER BY
            scheduled ASC,
            estimated ASC limit 100
    """)
    fun collectArrivalItemFor(uniqueId: String): Flow<ArrivalItem>

    @Transaction @Query("""
        SELECT * FROM ${ArrivalEntity.TABLE_NAME}
        WHERE id = :uniqueId
        ORDER BY
            scheduled ASC,
            estimated ASC limit 100
    """)
    fun getArrivalItemFor(uniqueId: String): ArrivalItem


    @Query("SELECT * FROM ${LocIdEntity.TABLE_NAME} WHERE latlon = :latlon")
    fun getLocIdFor(latlon: String): LocIdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrReplace(entity: LocIdEntity): Long



    @Upsert
    fun upsertArrivalEntities(models: List<ArrivalEntity>): List<Long>

    @Query("DELETE FROM ${ArrivalEntity.TABLE_NAME} WHERE locid IN (:listLocId) AND id NOT IN (:listArrivalId)")
    fun deleteArrivals(listLocId: List<Long>, listArrivalId: List<String>)


    @Upsert
    fun createBlockPositions(entities: List<BlockPositionEntity>): List<Long>



    @Transaction
    fun updateArrivals(
        csvLocIds: List<Long>,
        blockPositionEntities: List<BlockPositionEntity>,
        arrivalEntities: List<ArrivalEntity>) {

        createBlockPositions(blockPositionEntities)
        val listArrivalId = arrivalEntities.map { it.id }
        deleteArrivals(csvLocIds, listArrivalId)
        upsertArrivalEntities(arrivalEntities)
    }

}